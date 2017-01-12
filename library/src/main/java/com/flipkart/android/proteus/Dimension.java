/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 *
 * Copyright (c) 2017 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.flipkart.android.proteus;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;

import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.toolbox.ProteusConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Dimension
 *
 * @author aditya.sharat
 */

public class Dimension extends Value {

    public static final int DIMENSION_UNIT_STYLE_ATTR = -3;
    public static final int DIMENSION_UNIT_RESOURCE = -2;
    public static final int DIMENSION_UNIT_ENUM = -1;
    public static final int DIMENSION_UNIT_PX = TypedValue.COMPLEX_UNIT_PX;
    public static final int DIMENSION_UNIT_DP = TypedValue.COMPLEX_UNIT_DIP;
    public static final int DIMENSION_UNIT_SP = TypedValue.COMPLEX_UNIT_SP;
    public static final int DIMENSION_UNIT_PT = TypedValue.COMPLEX_UNIT_PT;
    public static final int DIMENSION_UNIT_IN = TypedValue.COMPLEX_UNIT_IN;
    public static final int DIMENSION_UNIT_MM = TypedValue.COMPLEX_UNIT_MM;

    public static final String MATCH_PARENT = "match_parent";
    public static final String FILL_PARENT = "fill_parent";
    public static final String WRAP_CONTENT = "wrap_content";

    public static final String PREFIX_DIMENSION = "@dimen/";
    public static final String PREFIX_ATTRIBUTE = "?";

    public static final String SUFFIX_PX = "px";
    public static final String SUFFIX_DP = "dp";
    public static final String SUFFIX_SP = "sp";
    public static final String SUFFIX_PT = "pt";
    public static final String SUFFIX_IN = "in";
    public static final String SUFFIX_MM = "mm";

    public static final Map<String, Integer> sDimensionsMap = new HashMap<>();
    public static final Map<String, Integer> sDimensionsUnitsMap = new HashMap<>();
    public static final Map<String, Integer> styleMap = new HashMap<>();
    public static final Map<String, Integer> attributeMap = new HashMap<>();

    static {
        sDimensionsMap.put(MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        sDimensionsMap.put(FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        sDimensionsMap.put(WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        sDimensionsUnitsMap.put(SUFFIX_PX, DIMENSION_UNIT_PX);
        sDimensionsUnitsMap.put(SUFFIX_DP, DIMENSION_UNIT_DP);
        sDimensionsUnitsMap.put(SUFFIX_SP, DIMENSION_UNIT_SP);
        sDimensionsUnitsMap.put(SUFFIX_PT, DIMENSION_UNIT_PT);
        sDimensionsUnitsMap.put(SUFFIX_IN, DIMENSION_UNIT_IN);
        sDimensionsUnitsMap.put(SUFFIX_MM, DIMENSION_UNIT_MM);
    }

    public static final Dimension ZERO = new Dimension(0, DIMENSION_UNIT_PX);

    public final float value;
    public final int unit;

    public Dimension(float value, int unit) {
        this.value = value;
        this.unit = unit;
    }

    private Dimension(String dimension, Context context) {
        Integer parameter = sDimensionsMap.get(dimension);
        float value;
        int unit;

        if (parameter != null) {
            value = parameter;
            unit = DIMENSION_UNIT_ENUM;
        } else {
            int length = dimension.length();
            if (length < 2) {
                value = 0;
                unit = DIMENSION_UNIT_PX;
            } else { // find the units and value by splitting at the second-last character of the dimension
                Integer u = sDimensionsUnitsMap.get(dimension.substring(length - 2));
                String stringValue = dimension.substring(0, length - 2);
                if (u != null) {
                    value = ParseHelper.parseFloat(stringValue);
                    unit = u;
                } else if (dimension.startsWith(PREFIX_DIMENSION)) { // check if dimension is a local resource
                    try {
                        value = context.getResources().getIdentifier(dimension, "dimen", context.getPackageName());
                        unit = DIMENSION_UNIT_RESOURCE;
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (ProteusConstants.isLoggingEnabled()) {
                            Log.e("Proteus", "could not find a dimension with name " + dimension + ". Error: " + e.getMessage());
                        }
                        value = 0;
                        unit = DIMENSION_UNIT_PX;
                    }
                } else if (dimension.startsWith(PREFIX_ATTRIBUTE)) { // check if dimension is an attribute value
                    try {
                        String[] dimenArr = dimension.substring(1, length).split(":");
                        String style = dimenArr[0];
                        String attr = dimenArr[1];
                        Integer styleId = styleMap.get(style);
                        if (styleId == null) {
                            styleId = R.style.class.getField(style).getInt(null);
                            styleMap.put(style, styleId);
                        }
                        Integer attrId = attributeMap.get(attr);
                        if (attrId == null) {
                            attrId = R.attr.class.getField(attr).getInt(null);
                            attributeMap.put(attr, attrId);
                        }
                        TypedArray a = context.getTheme().obtainStyledAttributes(styleId, new int[]{attrId});
                        value = a.getDimensionPixelSize(0, 0);
                        unit = DIMENSION_UNIT_STYLE_ATTR;
                    } catch (Exception e) {
                        if (ProteusConstants.isLoggingEnabled()) {
                            Log.e("Proteus", "could not find a dimension with name " + dimension + ". Error: " + e.getMessage());
                        }
                        value = 0;
                        unit = DIMENSION_UNIT_PX;
                    }

                } else {
                    value = 0;
                    unit = DIMENSION_UNIT_PX;
                }
            }
        }

        this.value = value;
        this.unit = unit;
    }

    public static Dimension valueOf(String dimension, Context context) {
        if (null == dimension) {
            return ZERO;
        }
        Dimension d = DimensionCache.cache.get(dimension);
        if (null == d) {
            d = new Dimension(dimension, context);
            DimensionCache.cache.put(dimension, d);
        }
        return d;
    }

    public float apply(Context context) {
        float result;

        switch (unit) {
            case DIMENSION_UNIT_ENUM:
            case DIMENSION_UNIT_STYLE_ATTR:
                result = value;
                break;
            case DIMENSION_UNIT_PX:
            case DIMENSION_UNIT_DP:
            case DIMENSION_UNIT_SP:
            case DIMENSION_UNIT_PT:
            case DIMENSION_UNIT_MM:
            case DIMENSION_UNIT_IN:
                result = TypedValue.applyDimension(unit, value, context.getResources().getDisplayMetrics());
                break;
            case DIMENSION_UNIT_RESOURCE:
                result = context.getResources().getDimension((int) value);
                break;
            default:
                result = 0;
        }

        return result;
    }

    private static class DimensionCache {
        private static final LruCache<String, Dimension> cache = new LruCache<>(64);
    }

    @Override
    Value copy() {
        return this;
    }

}
