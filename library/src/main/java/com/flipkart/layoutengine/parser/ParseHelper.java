package com.flipkart.layoutengine.parser;

import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;

/**
 * Created by kiran.kumar on 17/05/14.
 */
public class ParseHelper {

    public static int parseGravity(String gravity)
    {
        String attributeValue = gravity;
        int gravityValue = Gravity.NO_GRAVITY;
        if ("center".equals(attributeValue)) {
            gravityValue = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        } else if ("center_horizontal".equals(attributeValue)) {
            gravityValue = Gravity.CENTER_HORIZONTAL;
        } else if ("center_vertical".equals(attributeValue)) {
            gravityValue = Gravity.CENTER_VERTICAL;
        } else if ("left".equals(attributeValue)) {
            gravityValue = Gravity.LEFT;
        } else if ("right".equals(attributeValue)) {
            gravityValue = Gravity.RIGHT;
        } else if("top".equals(attributeValue))
        {
            gravityValue = Gravity.TOP;
        }else if("bottom".equals(attributeValue))
        {
            gravityValue = Gravity.BOTTOM;
        }
        return gravityValue;
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
        int dimensionInPixels = dpToPx(Integer.parseInt(dimension));
        return dimensionInPixels;
    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
