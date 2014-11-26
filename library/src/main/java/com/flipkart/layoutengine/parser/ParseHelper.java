package com.flipkart.layoutengine.parser;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by kiran.kumar on 17/05/14.
 */
public class ParseHelper {

    private static final String TAG = ParseHelper.class.getSimpleName();

    public static int parseGravity(String attributeValue)
    {
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
            } else if("top".equals(gravity))
            {
                gravityValue = Gravity.TOP;
            }else if("bottom".equals(gravity))
            {
                gravityValue = Gravity.BOTTOM;
            }
            returnGravity|=gravityValue;
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
            dimensionInPixels = dpToPx(Integer.parseInt(dimension));
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

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
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

}
