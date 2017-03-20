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

package com.flipkart.android.proteus.value;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.toolbox.ProteusConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * StyleResource
 *
 * @author aditya.sharat
 */

public class StyleResource extends Value {

    public static final StyleResource NULL = new StyleResource();
    private static final Map<String, Integer> styleMap = new HashMap<>();
    private static final Map<String, Integer> attributeMap = new HashMap<>();
    private static final Map<String, Class> sHashMap = new HashMap<>();
    private static final String ATTR_START_LITERAL = "?";

    public final int styleId;
    public final int attributeId;

    private StyleResource() {
        this.styleId = -1;
        this.attributeId = -1;
    }

    private StyleResource(String value, Context context) throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        if (!ParseHelper.isLocalAttribute(value)) {
            throw new IllegalArgumentException(value + " is not a valid style attribute");
        }
        String[] tokens = value.substring(1, value.length()).split(":");
        String style = tokens[0];
        String attr = tokens[1];
        Class clazz;

        Integer styleId = styleMap.get(style);
        if (styleId == null) {
            String className = context.getPackageName() + ".R$style";
            clazz = sHashMap.get(className);
            if (null == clazz) {
                clazz = Class.forName(className);
                sHashMap.put(className, clazz);
            }
            styleId = clazz.getField(style).getInt(null);
            styleMap.put(style, styleId);
        }
        this.styleId = styleId;
        Integer attrId = attributeMap.get(attr);
        if (attrId == null) {
            String className = context.getPackageName() + ".R$attr";
            clazz = sHashMap.get(className);
            if (null == clazz) {
                clazz = Class.forName(className);
                sHashMap.put(className, clazz);
            }
            attrId = clazz.getField(attr).getInt(null);
            attributeMap.put(attr, attrId);
        }
        this.attributeId = attrId;
    }

    public static boolean isStyleResource(String value) {
        return value.startsWith(ATTR_START_LITERAL);
    }

    @Nullable
    public static StyleResource valueOf(String value, Context context) {
        StyleResource style = StyleCache.cache.get(value);
        if (null == style) {
            try {
                style = new StyleResource(value, context);
            } catch (Exception e) {
                if (ProteusConstants.isLoggingEnabled()) {
                    e.printStackTrace();
                }
                style = NULL;
            }
            StyleCache.cache.put(value, style);
        }
        return NULL == style ? null : style;
    }

    public TypedArray apply(Context context) {
        return context.obtainStyledAttributes(styleId, new int[]{attributeId});
    }

    @Override
    public Value copy() {
        return this;
    }

    private static class StyleCache {
        private static final LruCache<String, StyleResource> cache = new LruCache<>(64);
    }
}
