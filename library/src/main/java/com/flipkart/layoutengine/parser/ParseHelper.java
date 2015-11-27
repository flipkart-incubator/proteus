package com.flipkart.layoutengine.parser;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flipkart.layoutengine.library.R;
import com.flipkart.layoutengine.provider.ProteusConstants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final String TEXT_ALIGNMENT_INHERIT = "inherit";
    private static final String TEXT_ALIGNMENT_GRAVITY = "gravity";
    private static final String TEXT_ALIGNMENT_CENTER = "center";
    private static final String TEXT_ALIGNMENT_TEXT_START = "start";
    private static final String TEXT_ALIGNMENT_TEXT_END = "end";
    private static final String TEXT_ALIGNMENT_VIEW_START = "viewStart";
    private static final String TEXT_ALIGNMENT_VIEW_END = "viewEnd";

    private static final String SUFFIX_DP = "dp";
    private static final String SUFFIX_SP = "sp";
    private static final String SUFFIX_PX = "px";

    private static final String ATTR_START_LITERAL = "?";
    private static final String COLOR_PREFIX_LITERAL = "#";

    private static final String DRAWABLE_LOCAL_RESOURCE_STR = "@drawable/";
    private static final String STRING_LOCAL_RESOURCE_STR = "@string/";
    private static final String TWEEN_LOCAL_RESOURCE_STR = "@anim/";
    private static final String COLOR_LOCAL_RESOURCE_STR = "@color/";
    private static final String DIMENSION_LOCAL_RESOURCE_STR = "@dimen/";


    private static final String DRAWABLE_STR = "drawable";
    private static final String ID_STR = "id";


    private static Map<String, Integer> styleMap = new HashMap<>();
    private static Map<String, Integer> attributeMap = new HashMap<>();


    private static final Pattern sAttributePattern = Pattern.compile("(\\?)(\\S*)(:?)(attr\\/?)(\\S*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final HashMap<String, Class> sHashMap = new HashMap<>();
    private static final HashMap<String, Integer> sAttributeCache = new HashMap<>();

    public static final Map<String, Integer> sStateMap = new HashMap<>();
    public static final Map<String, Integer> sGravityMap = new HashMap<>();
    public static final Map<String, Integer> sDividerMode = new HashMap<>();
    public static final Map<String, Enum> sEllipsizeMode = new HashMap<>();
    public static final Map<String, Integer> sVisibilityMode = new HashMap<>();
    public static final Map<String, Integer> sTextAligment = new HashMap<>();


    public static final Map<String, Integer> sDimensionsMap = new HashMap<>();

    public static final Map<String, ImageView.ScaleType> sImageScaleType = new HashMap<>();
    private static Logger logger = LoggerFactory.getLogger(ParseHelper.class);


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

        sImageScaleType.put(CENTER, ImageView.ScaleType.CENTER);
        sImageScaleType.put("center_crop", ImageView.ScaleType.CENTER_CROP);
        sImageScaleType.put("center_inside", ImageView.ScaleType.CENTER_INSIDE);
        sImageScaleType.put("fitCenter", ImageView.ScaleType.FIT_CENTER);
        sImageScaleType.put("fit_xy", ImageView.ScaleType.FIT_XY);
        sImageScaleType.put("matrix", ImageView.ScaleType.MATRIX);


        sTextAligment.put(TEXT_ALIGNMENT_INHERIT, View.TEXT_ALIGNMENT_INHERIT);
        sTextAligment.put(TEXT_ALIGNMENT_GRAVITY, View.TEXT_ALIGNMENT_GRAVITY);
        sTextAligment.put(TEXT_ALIGNMENT_CENTER, View.TEXT_ALIGNMENT_CENTER);
        sTextAligment.put(TEXT_ALIGNMENT_TEXT_START, View.TEXT_ALIGNMENT_TEXT_START);
        sTextAligment.put(TEXT_ALIGNMENT_TEXT_END, View.TEXT_ALIGNMENT_TEXT_END);
        sTextAligment.put(TEXT_ALIGNMENT_VIEW_START, View.TEXT_ALIGNMENT_VIEW_START);
        sTextAligment.put(TEXT_ALIGNMENT_VIEW_END, View.TEXT_ALIGNMENT_VIEW_END);
    }

    public static int parseInt(String attributeValue) {
        int number;
        if (ProteusConstants.DATA_NULL.equals(attributeValue)) {
            return 0;
        }
        try {
            number = Integer.parseInt(attributeValue);
        } catch (NumberFormatException e) {
            if (logger.isErrorEnabled()) {
                logger.error(attributeValue + " is NAN. Error: " + e.getMessage());
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
            if (logger.isErrorEnabled()) {
                logger.error(attributeValue + " is NAN. Error: " + e.getMessage());
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
            if (logger.isErrorEnabled()) {
                logger.error(attributeValue + " is NAN. Error: " + e.getMessage());
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

    public static int parseDimension(String dimension, Context context) {
        Integer dimensionInPixels = sDimensionsMap.get(dimension);

        if (null == dimensionInPixels && dimension.length() > 2) {
            String suffix = dimension.substring(dimension.length() - 2);
            if (SUFFIX_DP.equals(suffix)) {
                dimension = dimension.substring(0, dimension.length() - 2);
                dimensionInPixels = dpToPx(ParseHelper.parseFloat(dimension));
            } else if (SUFFIX_PX.equals(suffix) || SUFFIX_SP.equals(suffix)) {
                dimension = dimension.substring(0, dimension.length() - 2);
                try {
                    dimensionInPixels = Integer.parseInt(dimension);
                } catch (NumberFormatException e) {
                    if (logger.isErrorEnabled()) {
                        logger.error(dimension + " is NAN. Error: " + e.getMessage());
                    }
                    dimensionInPixels = 0;
                }
            } else if (dimension.startsWith(DIMENSION_LOCAL_RESOURCE_STR)) {
                try {
                    int resourceId = context.getResources().getIdentifier(dimension, "dimen", context.getPackageName());
                    dimensionInPixels = (int) context.getResources().getDimension(resourceId);
                } catch (Exception e) {
                    if (logger.isErrorEnabled()) {
                        logger.error("could not find a dimension with name " + dimension + ". Error: " + e.getMessage());
                    }
                    dimensionInPixels = 0;
                }
            } else if (dimension.startsWith(ATTR_START_LITERAL)) {
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
                    a.recycle();
                } catch (Exception e) {
                    if (logger.isErrorEnabled()) {
                        logger.error("could not find a dimension with name " + dimension + ". Error: " + e.getMessage());
                    }
                    dimensionInPixels = 0;
                }
            }
        }

        return dimensionInPixels == null ? 0 : dimensionInPixels;
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
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
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
            if (logger.isErrorEnabled()) {
                logger.error("Invalid color : " + color + ". Using #000000");
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
            if (logger.isErrorEnabled()) {
                logger.error(id + " is not a valid resource ID.");
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
            if (logger.isErrorEnabled()) {
                logger.error("cannot add relative layout rules when container is not relative");
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


    public static Pair<int[], String> parseState(JsonObject stateObject) {

        //drawable
        JsonElement jsonElement = stateObject.get(DRAWABLE_STR);
        if (jsonElement.isJsonPrimitive()) {
            String drawable = jsonElement.getAsString();

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
    public static Pair<Integer, String> parseLayer(JsonObject child) {

        JsonElement id = child.get(ID_STR);
        int androidResIdByXmlResId = View.NO_ID;
        String idAsString = null;
        if (id != null) {
            idAsString = id.getAsString();
        }
        if (idAsString != null) {
            androidResIdByXmlResId = getAndroidResIdByXmlResId(idAsString);
        }
        String drawable = null;
        JsonElement drawableElement = child.get(DRAWABLE_STR);
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
        return !TextUtils.isEmpty(attributeValue) ? sImageScaleType.get(attributeValue) : null;
    }

    /**
     * parses TextView typeface
     *
     * @param attributeValue value of the typeface attribute
     * @return the typeface value
     */
    public static int parseTypeFace(String attributeValue) {
        return (attributeValue != null && BOLD.equals(attributeValue)) ? Typeface.BOLD : 0;
    }

    /**
     * parses Text Alignment
     *
     * @param attributeValue value of the typeface attribute
     * @return the text alignment value
     */
    public static Integer parseTextAlignment(String attributeValue){
        return !TextUtils.isEmpty(attributeValue) ? sTextAligment.get(attributeValue) : null;
    }
}
