package com.flipkart.layoutengine.processor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.StateSet;
import android.view.View;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class ColorResourceProcessor<V extends View> extends AttributeProcessor<V> {

    private static HashMap<String, Integer> sAttributesMap = null;
    private static final String TAG = ColorResourceProcessor.class.getSimpleName();
    private Logger logger = LoggerFactory.getLogger(ColorResourceProcessor.class);

    public ColorResourceProcessor() {

    }

    private static HashMap<String, Integer> getAttributesMap() {
        if (null == sAttributesMap) {
            synchronized (ColorResourceProcessor.class) {
                if (null == sAttributesMap) {
                    sAttributesMap = new HashMap<>(15);
                    sAttributesMap.put("type", android.R.attr.type);
                    sAttributesMap.put("color", android.R.attr.color);
                    sAttributesMap.put("alpha", android.R.attr.alpha);
                    sAttributesMap.put("state_pressed", android.R.attr.state_pressed);
                    sAttributesMap.put("state_focused", android.R.attr.state_focused);
                    sAttributesMap.put("state_selected", android.R.attr.state_selected);
                    sAttributesMap.put("state_checkable", android.R.attr.state_checkable);
                    sAttributesMap.put("state_checked", android.R.attr.state_checked);
                    sAttributesMap.put("state_enabled", android.R.attr.state_enabled);
                    sAttributesMap.put("state_window_focused", android.R.attr.state_window_focused);
                }
            }
        }
        return sAttributesMap;
    }

    private Integer getAttribute(String attribute) {
        return getAttributesMap().get(attribute);
    }


    @Override
    public void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue, V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
        if (attributeValue.isJsonPrimitive()) {
            handleString(parserContext, attributeKey, attributeValue.getAsString(), view, proteusView, parent, layout, index);
        } else if (attributeValue.isJsonObject()) {
            handleElement(parserContext, attributeKey, attributeValue, view, proteusView, parent, layout, index);
        } else {
            if(logger.isErrorEnabled()) {
                logger.error("#handle() : Resource for key: " + attributeKey + " must be a primitive or an object. value -> " + attributeValue.toString());
            }
        }
    }

    /**
     * This block handles ColorStateList
     *
     * @param parserContext
     * @param attributeKey
     * @param attributeValue
     * @param view
     * @param proteusView
     * @param parent
     * @param layout
     */
    protected void handleElement(ParserContext parserContext, String attributeKey,
                                 JsonElement attributeValue, V view, ProteusView proteusView,
                                 ProteusView parent, JsonObject layout, int index) {
        JsonObject jsonObject = attributeValue.getAsJsonObject();
        ColorStateList colorStateList = inflateFromJson(view.getContext(), jsonObject);

        if (null != colorStateList) {
            setColor(view, colorStateList);
        }
    }

    private Integer getColorFromAttributeValue(Context context, String attributeValue) {
        Integer result = null;
        if (ParseHelper.isColor(attributeValue)) {
            result = Color.parseColor(attributeValue);
        } else if (ParseHelper.isLocalColorResource(attributeValue)) {
            Resources r = context.getResources();
            int colorId = r.getIdentifier(attributeValue, "color", context.getPackageName());
            result = r.getColor(colorId);
        }
        return result;
    }

    /**
     * Any string based colors and the ones which are defined in local resources are handled here
     *
     * @param parserContext
     * @param attributeKey
     * @param attributeValue
     * @param view
     * @param proteusView
     * @param parent
     * @param layout
     */
    protected void handleString(ParserContext parserContext, String attributeKey, final String attributeValue,
                                final V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
        if (ParseHelper.isColor(attributeValue)) {
            setColor(view, Color.parseColor(attributeValue));
        } else if (ParseHelper.isLocalColorResource(attributeValue)) {
            try {
                Resources r = view.getResources();
                int colorId = r.getIdentifier(attributeValue, "color", view.getContext().getPackageName());
                ColorStateList colorStateList = null;
                try {
                    colorStateList = r.getColorStateList(colorId);
                } catch (Resources.NotFoundException nfe) {
                    //assuming this is a color Now
                }
                if (null != colorStateList) {
                    setColor(view, colorStateList);
                } else {
                    int color = r.getColor(colorId);
                    setColor(view, color);
                }
            } catch (Exception ex) {
                if(logger.isErrorEnabled()) {
                    logger.error("Could not load local resource " + attributeValue);
                }
            }
        }
    }


    private static int idealByteArraySize(int need) {
        for (int i = 4; i < 32; i++) {
            if (need <= (1 << i) - 12) {
                return (1 << i) - 12;
            }
        }

        return need;
    }

    private static int idealIntArraySize(int need) {
        return idealByteArraySize(need * 4) / 4;
    }


    public static int constrain(int amount, int low, int high) {
        return amount < low ? low : (amount > high ? high : amount);
    }


    private int modulateColorAlpha(int baseColor, float alphaMod) {
        if (alphaMod == 1.0f) {
            return baseColor;
        }
        final int baseAlpha = Color.alpha(baseColor);
        final int alpha = constrain((int) (baseAlpha * alphaMod + 0.5f), 0, 255);
        return (baseColor & 0xFFFFFF) | (alpha << 24);
    }


    private ColorStateList inflateFromJson(Context context, JsonObject jsonObject) {
        ColorStateList result = null;
        JsonElement type = jsonObject.get("type");
        if (null != type && type.isJsonPrimitive()) {
            String colorType = type.getAsString();
            if (TextUtils.equals(colorType, "selector")) {
                JsonElement childrenElement = jsonObject.get("children");

                if (null != childrenElement && childrenElement.isJsonArray()) {
                    JsonArray children = childrenElement.getAsJsonArray();
                    int listAllocated = 20;
                    int listSize = 0;
                    int[] colorList = new int[listAllocated];
                    int[][] stateSpecList = new int[listAllocated][];

                    for (int idx = 0; idx < children.size(); idx++) {
                        JsonElement itemObject = children.get(idx);
                        if (!itemObject.isJsonObject()) {
                            continue;
                        }

                        Set<Map.Entry<String, JsonElement>> entrySet = ((JsonObject) itemObject).entrySet();
                        if (entrySet.size() == 0) {
                            continue;
                        }

                        int j = 0;
                        Integer baseColor = null;
                        float alphaMod = 1.0f;

                        int[] stateSpec = new int[entrySet.size() - 1];
                        boolean ignoreItem = false;
                        for (Map.Entry<String, JsonElement> entry : entrySet) {
                            if (ignoreItem) {
                                break;
                            }
                            if (!entry.getValue().isJsonPrimitive()) {
                                continue;
                            }
                            Integer attributeId = getAttribute(entry.getKey());
                            if (null != attributeId) {
                                switch (attributeId) {
                                    case android.R.attr.type:
                                        if (!TextUtils.equals("item", entry.getValue().getAsString())) {
                                            ignoreItem = true;
                                        }
                                        break;
                                    case android.R.attr.color:
                                        String colorRes = entry.getValue().getAsString();
                                        if (!TextUtils.isEmpty(colorRes)) {
                                            baseColor = getColorFromAttributeValue(context, colorRes);
                                        }
                                        break;
                                    case android.R.attr.alpha:
                                        String alphaStr = entry.getValue().getAsString();
                                        if (!TextUtils.isEmpty(alphaStr)) {
                                            alphaMod = Float.parseFloat(alphaStr);
                                        }
                                        break;
                                    default:
                                        stateSpec[j++] = entry.getValue().getAsBoolean()
                                                ? attributeId
                                                : -attributeId;
                                        break;
                                }
                            }
                        }
                        if (!ignoreItem) {
                            stateSpec = StateSet.trimStateSet(stateSpec, j);
                            if (null == baseColor) {
                                throw new IllegalStateException("No Color Specified");
                            }

                            if (listSize + 1 >= listAllocated) {
                                listAllocated = idealIntArraySize(listSize + 1);
                                int[] ncolor = new int[listAllocated];
                                System.arraycopy(colorList, 0, ncolor, 0, listSize);
                                int[][] nstate = new int[listAllocated][];
                                System.arraycopy(stateSpecList, 0, nstate, 0, listSize);
                                colorList = ncolor;
                                stateSpecList = nstate;
                            }

                            final int color = modulateColorAlpha(baseColor, alphaMod);


                            colorList[listSize] = color;
                            stateSpecList[listSize] = stateSpec;
                            listSize++;
                        }
                    }
                    if (listSize > 0) {
                        int[] colors = new int[listSize];
                        int[][] stateSpecs = new int[listSize][];
                        System.arraycopy(colorList, 0, colors, 0, listSize);
                        System.arraycopy(stateSpecList, 0, stateSpecs, 0, listSize);
                        result = new ColorStateList(stateSpecs, colors);
                    }
                }
            }
        }
        return result;
    }


    public abstract void setColor(V view, int color);

    public abstract void setColor(V view, ColorStateList colors);
}
