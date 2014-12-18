package com.flipkart.layoutengine.parser;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by kiran.kumar on 17/05/14.
 */
public class ParseHelper {

    private static final String TAG = ParseHelper.class.getSimpleName();

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
            returnGravity |= gravityValue;
        }
        return returnGravity;
    }

    public static int parseVisibility(String attributeValue) {
        int returnValue = View.VISIBLE;
        if("visible".equals(attributeValue))
        {
            returnValue  = View.VISIBLE;
        }
        else if("invisible".equals(attributeValue))
        {
            returnValue = View.INVISIBLE;
        }
        else if("gone".equals(attributeValue))
        {
            returnValue = View.GONE;
        }
        else if("true".equals(attributeValue))
        {
            returnValue  = View.VISIBLE;
        }
        else if("false".equals(attributeValue))
        {
            returnValue = View.GONE;
        }

        return returnValue;
    }

    public static int parseDimension(String dimension)
    {

        int dimensionInPixels = 0;
        if ("match_parent".equals(dimension) || "fill_parent".equals(dimension)) {
            dimensionInPixels = ViewGroup.LayoutParams.MATCH_PARENT;
        } else if ("wrap_content".equals(dimension)) {
            dimensionInPixels = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        else if(dimension.endsWith("dp")) {
            dimension = dimension.substring(0,dimension.length()-2);
            dimensionInPixels = dpToPx(Float.parseFloat(dimension));
        }
        else if(dimension.endsWith("px") || dimension.endsWith("sp"))
        {
            dimension = dimension.substring(0,dimension.length()-2);
            dimensionInPixels = Integer.parseInt(dimension);
        }

        return dimensionInPixels;
    }

    public static boolean isColor(String color)
    {
        return color.startsWith("#");
    }

    public static int parseColor(String color)
    {

        return Color.parseColor(color);

    }

    public static Integer parseId(String id)
    {
        try {
            Integer idInt = Integer.valueOf(id);
            return idInt;
        }
        catch (NumberFormatException ex)
        {
            ex.printStackTrace();
        }

        return null;

    }

    public static boolean parseBoolean(String trueOrFalse)
    {
        if(trueOrFalse.equals("true"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static int parseRelativeLayoutBoolean(String trueOrFalse)
    {
        if(trueOrFalse.equals("true"))
        {
            return RelativeLayout.TRUE;
        }
        else
        {
            return 0;
        }
    }

    public static void addRelativeLayoutRule(View view, int verb, int anchor)
    {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if(layoutParams instanceof RelativeLayout.LayoutParams)
        {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutParams;
            params.addRule(verb,anchor);
            view.setLayoutParams(params);
        }
        else
        {
            Log.d(TAG,"cannot add relative layout rules when container is not relative");
        }
    }

    public static int dpToPx(float dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static float pxToDp(int px)
    {
        return (float) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int parseTextStyle(String attributeValue) {
        if(attributeValue!=null)
        {
            Typeface typeface = Typeface.DEFAULT;
            attributeValue = attributeValue.toLowerCase();
            if("bold".equals(attributeValue))
            {
                typeface = Typeface.DEFAULT_BOLD;
            }
            else if("italic".equals(attributeValue))
            {
                //typeface = Typ
            }
        }
        return 0;
    }

    public static boolean isLocalResource(String attributeValue) {
        return attributeValue.startsWith("@drawable/");
    }


    /**
     * Parses the json object which represents a single state of a {@link android.graphics.drawable.StateListDrawable}
     * @param jsonObject Object representing a single state. Typically a child of a selector.
     * @return a pair of 2 things. First the states useful for StateListDrawable and a string representing the drawable
     */
    public static Map<String,Integer> stateMap = new HashMap<String, Integer>();

    static
    {
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
    public static Pair<int[],String> parseState(JsonObject stateObject)
    {


        //drawable
        JsonElement jsonElement = stateObject.get("drawable");
        if(jsonElement.isJsonPrimitive()) {
            String drawable = jsonElement.getAsString();

            //states
            Set<Map.Entry<String, JsonElement>> entries = stateObject.entrySet();
            List<Integer> statesToReturn = new ArrayList<Integer>();
            for (Map.Entry<String, JsonElement> entry : entries) {
                JsonElement value = entry.getValue();
                String state = entry.getKey();
                Integer stateInteger = stateMap.get(state);
                if (stateInteger != null) {
                    String stateValue = entry.getValue().getAsString();
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
            return new Pair<int[], String>(statesToReturnInteger, drawable);
        }
        return null;
    }

    /**
     * Uses reflection to fetch the R.id from the given class.
     * This method is faster than using {@link android.content.res.Resources#getResourceName(int)}
     * @param variableName
     * @param с
     * @return
     */
    public static int getResId(String variableName, Class<?> с) {

        Field field = null;
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
     * @param fullResIdString
     * @return
     */
    public static int getAndroidResIdByXmlResId(String fullResIdString)
    {

        if(fullResIdString!=null)
        {
            int i = fullResIdString.indexOf("/");
            if(i>=0)
            {
                String idString = fullResIdString.substring(i+1);
                if(idString!=null)
                {
                    return getResId(idString,android.R.id.class);

                }
            }
        }
        return View.NO_ID;
    }

    /**
     * Parses a single layer item (represented by {@param child) inside a layer list and gives a pair of android:id and a string for the drawable path
     * @param child
     * @return
     */
    public static Pair<Integer,String> parseLayer(JsonObject child) {

        JsonElement id = child.get("id");
        int androidResIdByXmlResId = View.NO_ID;
        String idAsString = null;
        if(id!=null) {
            idAsString = id.getAsString();
        }
        if(idAsString!=null) {
             androidResIdByXmlResId = getAndroidResIdByXmlResId(idAsString);
        }
        String drawable = null;
        JsonElement drawableElement = child.get("drawable");
        if(drawableElement!=null)
        {
            drawable = drawableElement.getAsString();
        }

        return new Pair<Integer, String>(androidResIdByXmlResId,drawable);
    }

    public static ImageView.ScaleType parseScaleType(String attributeValue) {
        ImageView.ScaleType type = null;
        if(attributeValue!=null)
        {
            attributeValue = attributeValue.toLowerCase();
            if("center".equals(attributeValue))
            {
                type = ImageView.ScaleType.CENTER;
            }
            else if("center_crop".equals(attributeValue))
            {
                type = ImageView.ScaleType.CENTER_CROP;
            }
            else if("center_inside".equals(attributeValue))
            {
                type = ImageView.ScaleType.CENTER_INSIDE;
            }
            else if("fit_center".equals(attributeValue))
            {
                type = ImageView.ScaleType.FIT_CENTER;
            }
            else if("fit_xy".equals(attributeValue))
            {
                type = ImageView.ScaleType.FIT_XY;
            }
            else if("matrix".equals(attributeValue))
            {
                type = ImageView.ScaleType.MATRIX;
            }
        }
        return type;
    }
}
