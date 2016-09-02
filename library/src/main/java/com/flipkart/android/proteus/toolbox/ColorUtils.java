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

package com.flipkart.android.proteus.toolbox;


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.util.StateSet;
import android.webkit.ValueCallback;

import com.flipkart.android.proteus.parser.ParseHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ColorUtils {
    private static final String TAG = "ColorUtils";
    private static HashMap<String, Integer> sAttributesMap = null;

    /**
     * @param context                Application context used to access resources
     * @param value                  JSON representation of the Color
     * @param colorCallback          Callback for return Value if it is a Color Resource
     * @param colorStateListCallback Callback for return Value if it is a ColorStateList
     * @throws android.content.res.Resources.NotFoundException when the animation cannot be loaded
     */
    public static void loadColor(Context context, JsonElement value, ValueCallback<Integer> colorCallback, ValueCallback<ColorStateList> colorStateListCallback) throws Resources.NotFoundException {
        if (value.isJsonPrimitive()) {
            handleString(context, value.getAsString(), colorCallback, colorStateListCallback);
        } else if (value.isJsonObject()) {
            handleElement(context, value.getAsJsonObject(), colorCallback, colorStateListCallback);
        } else {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, "Could not color for : " + value.toString());
            }
        }
    }


    private static void handleString(Context context, String attributeValue, ValueCallback<Integer> colorCallback, ValueCallback<ColorStateList> colorStateListCallback) {
        if (ParseHelper.isColor(attributeValue)) {
            colorCallback.onReceiveValue(Color.parseColor(attributeValue));
        } else if (ParseHelper.isLocalColorResource(attributeValue)) {
            try {
                Resources r = context.getResources();
                int colorId = r.getIdentifier(attributeValue, "color", context.getPackageName());
                ColorStateList colorStateList = null;
                try {
                    colorStateList = r.getColorStateList(colorId);
                } catch (Resources.NotFoundException nfe) {
                    //assuming this is a color Now
                }
                if (null != colorStateList) {
                    colorStateListCallback.onReceiveValue(colorStateList);
                } else {
                    int color = r.getColor(colorId);
                    colorCallback.onReceiveValue(color);
                }
            } catch (Exception ex) {
                if (ProteusConstants.isLoggingEnabled()) {
                    Log.e(TAG, "Could not load local resource " + attributeValue);
                }
            }
        }
    }

    private static void handleElement(Context context, JsonObject value, ValueCallback<Integer> colorCallback, ValueCallback<ColorStateList> colorStateListCallback) {
        JsonObject jsonObject = value.getAsJsonObject();
        ColorStateList colorStateList = inflateFromJson(context, jsonObject);

        if (null != colorStateList) {
            colorStateListCallback.onReceiveValue(colorStateList);
        }
    }

    private static Integer getColorFromAttributeValue(Context context, String attributeValue) {
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


    private static int modulateColorAlpha(int baseColor, float alphaMod) {
        if (alphaMod == 1.0f) {
            return baseColor;
        }
        final int baseAlpha = Color.alpha(baseColor);
        final int alpha = constrain((int) (baseAlpha * alphaMod + 0.5f), 0, 255);
        return (baseColor & 0xFFFFFF) | (alpha << 24);
    }

    private static HashMap<String, Integer> getAttributesMap() {
        if (null == sAttributesMap) {
            synchronized (ColorUtils.class) {
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

    private static Integer getAttribute(String attribute) {
        return getAttributesMap().get(attribute);
    }

    private static ColorStateList inflateFromJson(Context context, JsonObject jsonObject) {
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
}
