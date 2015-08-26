package com.flipkart.layoutengine.parser;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flipkart.layoutengine.library.R;
import com.flipkart.layoutengine.provider.ProteusConstants;
import com.flipkart.layoutengine.toolbox.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author kiran.kumar
 */
public class ParseHelper {

    private static final String TAG = ParseHelper.class.getSimpleName();

    private static Map<String, Integer> styleMap = new HashMap<>();
    private static Map<String, Integer> attributeMap = new HashMap<>();

    public static int parseInt(String attributeValue) {
        int number;
        try {
            number = Integer.parseInt(attributeValue);
        } catch (NumberFormatException e) {
            Log.e(Utils.getTagPrefix() + ".ParseHelper",
                    attributeValue + " is NAN. Error: " + e.getMessage());
            number = 0;
        }
        return number;
    }

    public static float parseFloat(String attributeValue) {
        float number;
        try {
            number = Float.parseFloat(attributeValue);
        } catch (NumberFormatException e) {
            Log.e(Utils.getTagPrefix() + ".ParseHelper",
                    attributeValue + " is NAN. Error: " + e.getMessage());
            number = 0;
        }
        return number;
    }

    public static double parseDouble(String attributeValue) {
        double number;
        try {
            number = Double.parseDouble(attributeValue);
        } catch (NumberFormatException e) {
            Log.e(Utils.getTagPrefix() + ".ParseHelper",
                    attributeValue + " is NAN. Error: " + e.getMessage());
            number = 0;
        }
        return number;
    }

    public static int parseGravity(String attributeValue) {
        String[] gravities = attributeValue.split("\\|");
        int returnGravity = Gravity.NO_GRAVITY;
        for (String gravity : gravities) {
            gravity = gravity.trim().toLowerCase();
            int gravityValue = Gravity.NO_GRAVITY;
            if ("center".equals(gravity)) {
                gravityValue = Gravity.CENTER;
            } else if ("center_horizontal".equals(gravity)) {
                gravityValue = Gravity.CENTER_HORIZONTAL;
            } else if ("center_vertical".equals(gravity)) {
                gravityValue = Gravity.CENTER_VERTICAL;
            } else if ("left".equals(gravity)) {
                gravityValue = Gravity.LEFT;
            } else if ("right".equals(gravity)) {
                gravityValue = Gravity.RIGHT;
            } else if ("top".equals(gravity)) {
                gravityValue = Gravity.TOP;
            } else if ("bottom".equals(gravity)) {
                gravityValue = Gravity.BOTTOM;
            } else if ("start".equals(gravity)) {
                gravityValue = Gravity.START;
            } else if ("end".equals(gravity)) {
                gravityValue = Gravity.END;
            }

            returnGravity = gravityValue;
        }


        return returnGravity;
    }

    public static int parseDividerMode(String attributeValue) {

        int returnValue = LinearLayout.SHOW_DIVIDER_NONE;
        if (attributeValue.equals("end")) {
            returnValue = LinearLayout.SHOW_DIVIDER_END;
        } else if (attributeValue.equals("middle")) {
            returnValue = LinearLayout.SHOW_DIVIDER_MIDDLE;
        } else if (attributeValue.equals("beginning")) {
            returnValue = LinearLayout.SHOW_DIVIDER_BEGINNING;
        } else {
            returnValue = LinearLayout.SHOW_DIVIDER_NONE;
        }

        return returnValue;
    }

    public static Enum parseEllipsize(String attributeValue) {
        Enum returnValue = TextUtils.TruncateAt.END;

        if (attributeValue.equals("end"))
            returnValue = TextUtils.TruncateAt.END;
        else if (attributeValue.equals("start"))
            returnValue = TextUtils.TruncateAt.START;
        else if (attributeValue.equals("marquee"))
            returnValue = TextUtils.TruncateAt.MARQUEE;
        else if (attributeValue.equals("middle"))
            returnValue = TextUtils.TruncateAt.MIDDLE;

        return returnValue;
    }

    public static int parseVisibility(JsonElement element) {
        String attributeValue;
        int returnValue;

        if (element.isJsonPrimitive()) {
            if (!element.getAsString().equals("")
                    && !element.getAsString().equals("false")
                    && !element.getAsString().equals(ProteusConstants.DATA_NULL)) {
                attributeValue = element.getAsString();
            } else {
                attributeValue = "gone";
            }
        } else if (element.isJsonNull()) {
            attributeValue = "gone";
        } else {
            attributeValue = "visible";
        }

        if ("visible".equals(attributeValue)) {
            returnValue = View.VISIBLE;
        } else if ("invisible".equals(attributeValue)) {
            returnValue = View.INVISIBLE;
        } else if ("gone".equals(attributeValue)) {
            returnValue = View.GONE;
        } else if ("true".equals(attributeValue)) {
            returnValue = View.VISIBLE;
        } else if ("false".equals(attributeValue)) {
            returnValue = View.GONE;
        } else {
            returnValue = View.VISIBLE;
        }

        return returnValue;
    }

    public static int parseDimension(String dimension, Context context) {
        int dimensionInPixels;
        if ("match_parent".equals(dimension) || "fill_parent".equals(dimension)) {
            dimensionInPixels = ViewGroup.LayoutParams.MATCH_PARENT;
        } else if ("wrap_content".equals(dimension)) {
            dimensionInPixels = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else if (dimension.endsWith("dp")) {
            dimension = dimension.substring(0, dimension.length() - 2);
            dimensionInPixels = dpToPx(ParseHelper.parseFloat(dimension));
        } else if (dimension.endsWith("px") || dimension.endsWith("sp")) {
            dimension = dimension.substring(0, dimension.length() - 2);
            try {
                dimensionInPixels = Integer.parseInt(dimension);
            } catch (NumberFormatException e) {
                Log.e(Utils.getTagPrefix() + ".ParseHelper",
                        dimension + " is NAN. Error: " + e.getMessage());
                dimensionInPixels = 0;
            }
        } else if (dimension.startsWith("@dimen/")) {
            try {
                int resourceId = context.getResources().getIdentifier(dimension, "dimen", context.getPackageName());
                dimensionInPixels = (int) context.getResources().getDimension(resourceId);
            } catch (Exception e) {
                Log.e(Utils.getTagPrefix() + ".ParseHelper",
                        "could not find a dimension with name " + dimension + ". Error: " + e.getMessage());
                dimensionInPixels = 0;
            }
        } else if (dimension.startsWith("?")) {
            try {
                String[] dimenArr = dimension.substring(1, dimension.length()).split(":");
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
                dimensionInPixels = a.getDimensionPixelSize(0, 0);
            } catch (Exception e) {
                Log.e(Utils.getTagPrefix() + ".ParseHelper",
                        "could not find a dimension with name " + dimension + ". Error: " + e.getMessage());
                dimensionInPixels = 0;
            }
        } else {
            dimensionInPixels = 0;
        }

        return dimensionInPixels;
    }

    public static boolean isColor(String color) {
        return color.startsWith("#");
    }

    public static int parseColor(String color) {
        try {
            return Color.parseColor(color);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "Invalid color : " + color + ". Using #000000");
            return Color.BLACK;
        }
    }

    public static Integer parseId(String id) {
        try {
            return Integer.valueOf(id);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean parseBoolean(String trueOrFalse) {
        return trueOrFalse.equals("true");
    }

    public static int parseRelativeLayoutBoolean(String trueOrFalse) {
        if (trueOrFalse.equals("true")) {
            return RelativeLayout.TRUE;
        } else {
            return 0;
        }
    }

    public static void addRelativeLayoutRule(View view, int verb, int anchor) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutParams;
            params.addRule(verb, anchor);
            view.setLayoutParams(params);
        } else {
            Log.d(TAG, "cannot add relative layout rules when container is not relative");
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
                case "bold":
                    typeface = Typeface.BOLD;
                    break;
                case "italic":
                    typeface = Typeface.ITALIC;
                    break;
                default:
                    typeface = Typeface.NORMAL;
                    break;
            }
        }
        return typeface;
    }

    public static boolean isLocalResource(String attributeValue) {
        return attributeValue.startsWith("@drawable/");
    }

    public static Map<String, Integer> stateMap = new HashMap<>();

    static {
        stateMap.put("state_pressed", android.R.attr.state_pressed);
        stateMap.put("state_enabled", android.R.attr.state_enabled);
        stateMap.put("state_focused", android.R.attr.state_focused);
        stateMap.put("state_hovered", android.R.attr.state_hovered);
        stateMap.put("state_selected", android.R.attr.state_selected);
        stateMap.put("state_checkable", android.R.attr.state_checkable);
        stateMap.put("state_checked", android.R.attr.state_checked);
        stateMap.put("state_activated", android.R.attr.state_activated);
        stateMap.put("state_window_focused", android.R.attr.state_window_focused);

    }

    public static Pair<int[], String> parseState(JsonObject stateObject) {

        //drawable
        JsonElement jsonElement = stateObject.get("drawable");
        if (jsonElement.isJsonPrimitive()) {
            String drawable = jsonElement.getAsString();

            //states
            Set<Map.Entry<String, JsonElement>> entries = stateObject.entrySet();
            List<Integer> statesToReturn = new ArrayList<>();
            for (Map.Entry<String, JsonElement> entry : entries) {
                JsonElement value = entry.getValue();
                String state = entry.getKey();
                Integer stateInteger = stateMap.get(state);
                if (stateInteger != null) {
                    String stateValue = value.getAsString();
                    boolean stateValueBoolean = ParseHelper.parseBoolean(stateValue);
                    if (stateValueBoolean) {
                        //e.g state_pressed = true
                        statesToReturn.add(stateInteger);
                    } else {
                        //e.g state_pressed = false
                        statesToReturn.add(-stateInteger);
                    }
                }
            }


            //return

            int[] statesToReturnInteger = new int[statesToReturn.size()];
            for (int i = 0; i < statesToReturn.size(); i++)
                statesToReturnInteger[i] = statesToReturn.get(i);
            return new Pair<>(statesToReturnInteger, drawable);
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
            try {
                resId = field.getInt(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
     * Parses a single layer item (represented by {@param child) inside a layer list and gives a pair of android:id and a string for the drawable path
     *
     * @param child
     * @return
     */
    public static Pair<Integer, String> parseLayer(JsonObject child) {

        JsonElement id = child.get("id");
        int androidResIdByXmlResId = View.NO_ID;
        String idAsString = null;
        if (id != null) {
            idAsString = id.getAsString();
        }
        if (idAsString != null) {
            androidResIdByXmlResId = getAndroidResIdByXmlResId(idAsString);
        }
        String drawable = null;
        JsonElement drawableElement = child.get("drawable");
        if (drawableElement != null) {
            drawable = drawableElement.getAsString();
        }

        return new Pair<>(androidResIdByXmlResId, drawable);
    }

    /**
     * Parses a image view scale type
     *
     * @param attributeValue value of the scale type attribute
     * @return {@link android.widget.ImageView.ScaleType} enum
     */
    public static ImageView.ScaleType parseScaleType(String attributeValue) {
        ImageView.ScaleType type = null;
        if (attributeValue != null) {
            attributeValue = attributeValue.toLowerCase();
            if ("center".equals(attributeValue)) {
                type = ImageView.ScaleType.CENTER;
            } else if ("center_crop".equals(attributeValue)) {
                type = ImageView.ScaleType.CENTER_CROP;
            } else if ("center_inside".equals(attributeValue)) {
                type = ImageView.ScaleType.CENTER_INSIDE;
            } else if ("fitCenter".equals(attributeValue)) {
                type = ImageView.ScaleType.FIT_CENTER;
            } else if ("fit_xy".equals(attributeValue)) {
                type = ImageView.ScaleType.FIT_XY;
            } else if ("matrix".equals(attributeValue)) {
                type = ImageView.ScaleType.MATRIX;
            }
        }
        return type;
    }

    /**
     * parses TextView typeface
     *
     * @param attributeValue value of the typeface attribute
     * @return the typeface value
     */
    public static int parseTypeFace(String attributeValue) {
        int typeface = 0;
        if (attributeValue != null) {
            if ("bold".equals(attributeValue)) {
                typeface = Typeface.BOLD;
            }
        }
        return typeface;
    }

}
