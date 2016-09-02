/*
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.android.proteus.parser;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flipkart.android.proteus.R;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kiran.kumar
 */
public class ParseHelper {

    private static final String TAG = "ParseHelper";

    private static final String TRUE = "true";
    private static final String FALSE = "false";

    private static final String VISIBLE = "visible";
    private static final String INVISIBLE = "invisible";
    private static final String GONE = "gone";

    private static final String CENTER = "center";
    private static final String CENTER_HORIZONTAL = "center_horizontal";
    private static final String CENTER_VERTICAL = "center_vertical";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String TOP = "top";
    private static final String BOTTOM = "bottom";
    private static final String START = "start";
    private static final String END = "end";
    private static final String MIDDLE = "middle";
    private static final String BEGINNING = "beginning";
    private static final String MARQUEE = "marquee";


    private static final String MATCH_PARENT = "match_parent";
    private static final String FILL_PARENT = "fill_parent";
    private static final String WRAP_CONTENT = "wrap_content";

    private static final String BOLD = "bold";
    private static final String ITALIC = "italic";
    private static final String BOLD_ITALIC = "bold|italic";

    private static final String TEXT_ALIGNMENT_INHERIT = "inherit";
    private static final String TEXT_ALIGNMENT_GRAVITY = "gravity";
    private static final String TEXT_ALIGNMENT_CENTER = "center";
    private static final String TEXT_ALIGNMENT_TEXT_START = "start";
    private static final String TEXT_ALIGNMENT_TEXT_END = "end";
    private static final String TEXT_ALIGNMENT_VIEW_START = "viewStart";
    private static final String TEXT_ALIGNMENT_VIEW_END = "viewEnd";

    private static final String SUFFIX_PX = "px";
    private static final String SUFFIX_DP = "dp";
    private static final String SUFFIX_SP = "sp";
    private static final String SUFFIX_PT = "pt";
    private static final String SUFFIX_IN = "in";
    private static final String SUFFIX_MM = "mm";

    private static final String ATTR_START_LITERAL = "?";
    private static final String COLOR_PREFIX_LITERAL = "#";

    private static final String DRAWABLE_LOCAL_RESOURCE_STR = "@drawable/";
    private static final String STRING_LOCAL_RESOURCE_STR = "@string/";
    private static final String TWEEN_LOCAL_RESOURCE_STR = "@anim/";
    private static final String COLOR_LOCAL_RESOURCE_STR = "@color/";
    private static final String DIMENSION_LOCAL_RESOURCE_STR = "@dimen/";

    private static final String DRAWABLE_STR = "drawable";
    private static final String ID_STR = "id";

    private static final Pattern sAttributePattern = Pattern.compile("(\\?)(\\S*)(:?)(attr\\/?)(\\S*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Map<String, Class> sHashMap = new HashMap<>();
    private static final Map<String, Integer> sAttributeCache = new HashMap<>();
    private static final Map<String, Integer> sStateMap = new HashMap<>();
    private static final Map<String, Integer> sGravityMap = new HashMap<>();
    private static final Map<String, Integer> sDividerMode = new HashMap<>();
    private static final Map<String, Enum> sEllipsizeMode = new HashMap<>();
    private static final Map<String, Integer> sVisibilityMode = new HashMap<>();
    private static final Map<String, Integer> sTextAlignment = new HashMap<>();
    private static final Map<String, Integer> sDimensionsMap = new HashMap<>();
    private static final Map<String, Integer> sDimensionsUnitsMap = new HashMap<>();
    private static final Map<String, ImageView.ScaleType> sImageScaleType = new HashMap<>();
    private static Map<String, Integer> styleMap = new HashMap<>();
    private static Map<String, Integer> attributeMap = new HashMap<>();

    static {
        sStateMap.put("state_pressed", android.R.attr.state_pressed);
        sStateMap.put("state_enabled", android.R.attr.state_enabled);
        sStateMap.put("state_focused", android.R.attr.state_focused);
        sStateMap.put("state_hovered", android.R.attr.state_hovered);
        sStateMap.put("state_selected", android.R.attr.state_selected);
        sStateMap.put("state_checkable", android.R.attr.state_checkable);
        sStateMap.put("state_checked", android.R.attr.state_checked);
        sStateMap.put("state_activated", android.R.attr.state_activated);
        sStateMap.put("state_window_focused", android.R.attr.state_window_focused);

        sGravityMap.put(CENTER, Gravity.CENTER);
        sGravityMap.put(CENTER_HORIZONTAL, Gravity.CENTER_HORIZONTAL);
        sGravityMap.put(CENTER_VERTICAL, Gravity.CENTER_VERTICAL);
        sGravityMap.put(LEFT, Gravity.LEFT);
        sGravityMap.put(RIGHT, Gravity.RIGHT);
        sGravityMap.put(TOP, Gravity.TOP);
        sGravityMap.put(BOTTOM, Gravity.BOTTOM);
        sGravityMap.put(START, Gravity.START);
        sGravityMap.put(END, Gravity.END);

        sDividerMode.put(END, LinearLayout.SHOW_DIVIDER_END);
        sDividerMode.put(MIDDLE, LinearLayout.SHOW_DIVIDER_MIDDLE);
        sDividerMode.put(BEGINNING, LinearLayout.SHOW_DIVIDER_BEGINNING);

        sEllipsizeMode.put(END, TextUtils.TruncateAt.END);
        sEllipsizeMode.put(START, TextUtils.TruncateAt.START);
        sEllipsizeMode.put(MARQUEE, TextUtils.TruncateAt.MARQUEE);
        sEllipsizeMode.put(MIDDLE, TextUtils.TruncateAt.MIDDLE);

        sVisibilityMode.put(VISIBLE, View.VISIBLE);
        sVisibilityMode.put(INVISIBLE, View.INVISIBLE);
        sVisibilityMode.put(GONE, View.GONE);

        sDimensionsMap.put(MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        sDimensionsMap.put(FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        sDimensionsMap.put(WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        sDimensionsUnitsMap.put(SUFFIX_PX, TypedValue.COMPLEX_UNIT_PX);
        sDimensionsUnitsMap.put(SUFFIX_DP, TypedValue.COMPLEX_UNIT_DIP);
        sDimensionsUnitsMap.put(SUFFIX_SP, TypedValue.COMPLEX_UNIT_SP);
        sDimensionsUnitsMap.put(SUFFIX_PT, TypedValue.COMPLEX_UNIT_PT);
        sDimensionsUnitsMap.put(SUFFIX_IN, TypedValue.COMPLEX_UNIT_IN);
        sDimensionsUnitsMap.put(SUFFIX_MM, TypedValue.COMPLEX_UNIT_MM);

        sImageScaleType.put(CENTER, ImageView.ScaleType.CENTER);
        sImageScaleType.put("center_crop", ImageView.ScaleType.CENTER_CROP);
        sImageScaleType.put("center_inside", ImageView.ScaleType.CENTER_INSIDE);
        sImageScaleType.put("fitCenter", ImageView.ScaleType.FIT_CENTER);
        sImageScaleType.put("fit_xy", ImageView.ScaleType.FIT_XY);
        sImageScaleType.put("matrix", ImageView.ScaleType.MATRIX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            sTextAlignment.put(TEXT_ALIGNMENT_INHERIT, View.TEXT_ALIGNMENT_INHERIT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            sTextAlignment.put(TEXT_ALIGNMENT_GRAVITY, View.TEXT_ALIGNMENT_GRAVITY);
            sTextAlignment.put(TEXT_ALIGNMENT_CENTER, View.TEXT_ALIGNMENT_CENTER);
            sTextAlignment.put(TEXT_ALIGNMENT_TEXT_START, View.TEXT_ALIGNMENT_TEXT_START);
            sTextAlignment.put(TEXT_ALIGNMENT_TEXT_END, View.TEXT_ALIGNMENT_TEXT_END);
            sTextAlignment.put(TEXT_ALIGNMENT_VIEW_START, View.TEXT_ALIGNMENT_VIEW_START);
            sTextAlignment.put(TEXT_ALIGNMENT_VIEW_END, View.TEXT_ALIGNMENT_VIEW_END);
        }
    }

    public static int parseInt(String attributeValue) {
        int number;
        if (ProteusConstants.DATA_NULL.equals(attributeValue)) {
            return 0;
        }
        try {
            number = Integer.parseInt(attributeValue);
        } catch (NumberFormatException e) {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, attributeValue + " is NAN. Error: " + e.getMessage());
            }
            number = 0;
        }
        return number;
    }

    public static float parseFloat(String attributeValue) {
        float number;
        if (ProteusConstants.DATA_NULL.equals(attributeValue)) {
            return 0;
        }
        try {
            number = Float.parseFloat(attributeValue);
        } catch (NumberFormatException e) {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, attributeValue + " is NAN. Error: " + e.getMessage());
            }
            number = 0;
        }
        return number;
    }

    public static double parseDouble(String attributeValue) {
        double number;
        if (ProteusConstants.DATA_NULL.equals(attributeValue)) {
            return 0;
        }
        try {
            number = Double.parseDouble(attributeValue);
        } catch (NumberFormatException e) {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, attributeValue + " is NAN. Error: " + e.getMessage());
            }
            number = 0;
        }
        return number;
    }


    public static int parseGravity(String attributeValue) {
        String[] gravities = attributeValue.split("\\|");
        int returnGravity = Gravity.NO_GRAVITY;
        for (String gravity : gravities) {
            Integer gravityValue = sGravityMap.get(gravity.trim().toLowerCase());
            if (null != gravityValue) {
                returnGravity |= gravityValue;
            }
        }
        return returnGravity;
    }

    public static int parseDividerMode(String attributeValue) {
        Integer returnValue = sDividerMode.get(attributeValue);
        return returnValue == null ? LinearLayout.SHOW_DIVIDER_NONE : returnValue;
    }

    public static Enum parseEllipsize(String attributeValue) {
        Enum returnValue = sEllipsizeMode.get(attributeValue);
        return returnValue == null ? TextUtils.TruncateAt.END : returnValue;
    }

    public static int parseVisibility(JsonElement element) {
        Integer returnValue = null;
        if (element.isJsonPrimitive()) {
            String attributeValue = element.getAsString();
            returnValue = sVisibilityMode.get(attributeValue);
            if (null == returnValue && (attributeValue.isEmpty() ||
                    FALSE.equals(attributeValue) ||
                    ProteusConstants.DATA_NULL.equals(attributeValue))) {
                returnValue = View.GONE;
            }
        } else if (element.isJsonNull()) {
            returnValue = View.GONE;
        }
        return returnValue == null ? View.VISIBLE : returnValue;
    }

    public static int parseInvisibility(JsonElement element) {
        Integer returnValue = null;
        if (element.isJsonPrimitive()) {
            String attributeValue = element.getAsString();
            returnValue = sVisibilityMode.get(attributeValue);
            if (null == returnValue && (attributeValue.isEmpty() ||
                    FALSE.equals(attributeValue) ||
                    ProteusConstants.DATA_NULL.equals(attributeValue))) {
                returnValue = View.VISIBLE;
            }
        } else if (element.isJsonNull()) {
            returnValue = View.VISIBLE;
        }

        return returnValue == null ? View.GONE : returnValue;
    }

    public static float parseDimension(final String dimension, Context context) {
        Integer parameter = sDimensionsMap.get(dimension);
        if (parameter != null) {
            return parameter;
        }

        int length = dimension.length();
        if (length < 2) {
            return 0;
        }

        // find the units and value by splitting at the second-last character of the dimension
        Integer units = sDimensionsUnitsMap.get(dimension.substring(length - 2));
        String stringValue = dimension.substring(0, length - 2);
        if (units != null) {
            float value = parseFloat(stringValue);
            DisplayMetrics displayMetric = context.getResources().getDisplayMetrics();
            return TypedValue.applyDimension(units, value, displayMetric);
        }

        // check if dimension is a local resource
        if (dimension.startsWith(DIMENSION_LOCAL_RESOURCE_STR)) {
            float value;
            try {
                int resourceId = context.getResources().getIdentifier(dimension, "dimen", context.getPackageName());
                value = (int) context.getResources().getDimension(resourceId);
            } catch (Exception e) {
                if (ProteusConstants.isLoggingEnabled()) {
                    Log.e(TAG, "could not find a dimension with name " + dimension + ". Error: " + e.getMessage());
                }
                value = 0;
            }
            return value;
        }

        // check if dimension is an attribute value
        if (dimension.startsWith(ATTR_START_LITERAL)) {
            float value;
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
                a.recycle();
            } catch (Exception e) {
                if (ProteusConstants.isLoggingEnabled()) {
                    Log.e(TAG, "could not find a dimension with name " + dimension + ". Error: " + e.getMessage());
                }
                value = 0;
            }
            return value;
        }

        return 0;
    }

    public static int getAttributeId(Context context, String attribute) {
        Integer result = sAttributeCache.get(attribute);
        if (null == result && attribute.length() > 1) {
            try {
                String attributeName = "";
                String packageName = "";
                Matcher matcher = sAttributePattern.matcher(attribute);
                if (matcher.matches()) {
                    attributeName = matcher.group(5);
                    packageName = matcher.group(2);
                } else {
                    attributeName = attribute.substring(1);
                }

                Class clazz = null;
                if (!TextUtils.isEmpty(packageName)) {
                    packageName = packageName.substring(0, packageName.length() - 1);
                } else {
                    packageName = context.getPackageName();
                }
                String className = packageName + ".R$attr";
                clazz = sHashMap.get(className);
                if (null == clazz) {
                    clazz = Class.forName(className);
                    sHashMap.put(className, clazz);
                }

                if (null != clazz) {
                    Field field = clazz.getField(attributeName);
                    if (null != field) {
                        result = field.getInt(null);
                        sAttributeCache.put(attribute, result);
                    }
                }

            } catch (ClassNotFoundException e) {
                if (ProteusConstants.isLoggingEnabled()) {
                    Log.e(TAG, e.getMessage() + "");
                }
            } catch (NoSuchFieldException e) {
                if (ProteusConstants.isLoggingEnabled()) {
                    Log.e(TAG, e.getMessage() + "");
                }
            } catch (IllegalAccessException e) {
                if (ProteusConstants.isLoggingEnabled()) {
                    Log.e(TAG, e.getMessage() + "");
                }
            }
        }
        return result == null ? 0 : result;
    }

    public static boolean isColor(String color) {
        return color.startsWith(COLOR_PREFIX_LITERAL);
    }

    public static int parseColor(String color) {
        try {
            return Color.parseColor(color);
        } catch (IllegalArgumentException ex) {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, "Invalid color : " + color + ". Using #000000");
            }
            return Color.BLACK;
        }
    }

    public static Integer parseId(String id) {
        if (ProteusConstants.DATA_NULL.equals(id)) {
            return null;
        }
        try {
            return Integer.valueOf(id);
        } catch (NumberFormatException ex) {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, id + " is not a valid resource ID.");
            }
        }
        return null;
    }

    public static boolean parseBoolean(String trueOrFalse) {
        return TRUE.equalsIgnoreCase(trueOrFalse);
    }

    public static int parseRelativeLayoutBoolean(String trueOrFalse) {
        return TRUE.equalsIgnoreCase(trueOrFalse) ? RelativeLayout.TRUE : 0;
    }

    public static void addRelativeLayoutRule(View view, int verb, int anchor) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutParams;
            params.addRule(verb, anchor);
            view.setLayoutParams(params);
        } else {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, "cannot add relative layout rules when container is not relative");
            }
        }
    }

    public static int dpToPx(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static float pxToDp(int px) {
        return (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int parseTextStyle(String attributeValue) {
        int typeface = Typeface.NORMAL;
        if (attributeValue != null) {
            attributeValue = attributeValue.toLowerCase();
            switch (attributeValue) {
                case BOLD:
                    typeface = Typeface.BOLD;
                    break;
                case ITALIC:
                    typeface = Typeface.ITALIC;
                    break;
                case BOLD_ITALIC:
                    typeface = Typeface.BOLD_ITALIC;
                    break;
                default:
                    typeface = Typeface.NORMAL;
                    break;
            }
        }
        return typeface;
    }

    public static boolean isLocalResourceAttribute(String attributeValue) {
        return attributeValue.startsWith(ATTR_START_LITERAL);
    }

    public static boolean isLocalStringResource(String attributeValue) {
        return attributeValue.startsWith(STRING_LOCAL_RESOURCE_STR);
    }


    public static boolean isLocalDrawableResource(String attributeValue) {
        return attributeValue.startsWith(DRAWABLE_LOCAL_RESOURCE_STR);
    }

    public static boolean isTweenAnimationResource(String attributeValue) {
        return attributeValue.startsWith(TWEEN_LOCAL_RESOURCE_STR);
    }

    public static boolean isLocalColorResource(String attributeValue) {
        return attributeValue.startsWith(COLOR_LOCAL_RESOURCE_STR);
    }


    public static Pair<int[], JsonElement> parseState(JsonObject stateObject) {

        //drawable
        JsonElement drawableJson = stateObject.get(DRAWABLE_STR);
        if (null != drawableJson) {

            //states
            Set<Map.Entry<String, JsonElement>> entries = stateObject.entrySet();
            List<Integer> statesToReturn = new ArrayList<>();
            for (Map.Entry<String, JsonElement> entry : entries) {
                JsonElement value = entry.getValue();
                String state = entry.getKey();
                Integer stateInteger = sStateMap.get(state);
                if (stateInteger != null) {
                    String stateValue = value.getAsString();
                    //e.g state_pressed = true state_pressed = false
                    statesToReturn.add(ParseHelper.parseBoolean(stateValue) ? stateInteger : -stateInteger);
                }
            }

            int[] statesToReturnInteger = new int[statesToReturn.size()];
            for (int i = 0; i < statesToReturn.size(); i++) {
                statesToReturnInteger[i] = statesToReturn.get(i);
            }

            return new Pair<>(statesToReturnInteger, drawableJson);
        }
        return null;
    }

    /**
     * Uses reflection to fetch the R.id from the given class.
     * This method is faster than using {@link android.content.res.Resources#getResourceName(int)}
     *
     * @param variableName the name of the variable
     * @param с            The class
     * @return resource id
     */
    public static int getResId(String variableName, Class<?> с) {

        Field field;
        int resId = 0;
        try {
            field = с.getField(variableName);
            resId = field.getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resId;

    }

    /**
     * Get int resource id, by just passing the string value of android:id from xml file.
     * Note : This method only works for @android:id or @+android:id right now
     *
     * @param fullResIdString the string id of the view
     * @return the number id of the view
     */
    public static int getAndroidResIdByXmlResId(String fullResIdString) {

        if (fullResIdString != null) {
            int i = fullResIdString.indexOf("/");
            if (i >= 0) {
                String idString = fullResIdString.substring(i + 1);
                return getResId(idString, android.R.id.class);
            }
        }
        return View.NO_ID;
    }

    /**
     * Parses a single layer item (represented by {@param child}) inside a layer list and gives
     * a pair of android:id and a string for the drawable path.
     *
     * @param child
     * @return The layer info as a {@link Pair}
     */
    public static Pair<Integer, JsonElement> parseLayer(JsonObject child) {

        JsonElement id = child.get(ID_STR);
        int androidResIdByXmlResId = View.NO_ID;
        String idAsString = null;
        if (id != null) {
            idAsString = id.getAsString();
        }
        if (idAsString != null) {
            androidResIdByXmlResId = getAndroidResIdByXmlResId(idAsString);
        }
        JsonElement drawableElement = child.get(DRAWABLE_STR);
        return (drawableElement != null) ? new Pair<>(androidResIdByXmlResId, drawableElement) : null;
    }

    /**
     * Parses a image view scale type
     *
     * @param attributeValue value of the scale type attribute
     * @return {@link android.widget.ImageView.ScaleType} enum
     */
    public static ImageView.ScaleType parseScaleType(String attributeValue) {
        return !TextUtils.isEmpty(attributeValue) ? sImageScaleType.get(attributeValue) : null;
    }

    /**
     * parses Text Alignment
     *
     * @param attributeValue value of the typeface attribute
     * @return the text alignment value
     */
    public static Integer parseTextAlignment(String attributeValue) {
        return !TextUtils.isEmpty(attributeValue) ? sTextAlignment.get(attributeValue) : null;
    }
}
