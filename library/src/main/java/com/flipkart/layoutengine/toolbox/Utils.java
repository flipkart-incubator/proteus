package com.flipkart.layoutengine.toolbox;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;

import com.flipkart.layoutengine.exceptions.InvalidDataPathException;
import com.flipkart.layoutengine.exceptions.JsonNullException;
import com.flipkart.layoutengine.exceptions.NoSuchDataPathException;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.provider.JsonProvider;
import com.flipkart.layoutengine.provider.ProteusConstants;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Map;

/**
 * @author Aditya Sharat
 */
public class Utils {

    public static final String LIB_NAME = "proteus";
    public static final String VERSION = "3.1.3-SNAPSHOT";
    public static final String TAG_DEBUG = Utils.getTagPrefix() + "debug";
    public static final String TAG_ERROR = Utils.getTagPrefix() + "error";

    public static final String ATTRIBUTE_BORDER_WIDTH = "width";
    public static final String ATTRIBUTE_BORDER_COLOR = "color";
    public static final String ATTRIBUTE_BORDER_RADIUS = "radius";
    public static final String ATTRIBUTE_BG_COLOR = "bgColor";

    public static JsonElement getElementFromData(String dataPath, JsonProvider dataProvider, int childIndex)
            throws JsonNullException, NoSuchDataPathException, InvalidDataPathException {
        // replace CHILD_INDEX_REFERENCE reference with index value
        if (ProteusConstants.CHILD_INDEX_REFERENCE.equals(dataPath)) {
            dataPath = dataPath.replace(ProteusConstants.CHILD_INDEX_REFERENCE, String.valueOf(childIndex));
            return new JsonPrimitive(dataPath);
        } else {
            return dataProvider.getObject(dataPath, childIndex);
        }
    }

    public static JsonElement merge(JsonElement oldJson, JsonElement newJson, boolean useCopy, Gson gson) {

        JsonElement newDataElement;
        JsonArray oldArray;
        JsonArray newArray;
        JsonElement oldArrayItem;
        JsonElement newArrayItem;
        JsonObject oldObject;

        if (oldJson == null || oldJson.isJsonNull()) {
            return useCopy ? gson.fromJson(newJson, JsonElement.class) : newJson;
        }

        if (newJson == null || newJson.isJsonNull()) {
            newJson = JsonNull.INSTANCE;
            return newJson;
        }

        if (newJson.isJsonPrimitive()) {
            JsonPrimitive value;
            if (!useCopy) {
                return newJson;
            }
            if (newJson.getAsJsonPrimitive().isBoolean()) {
                value = new JsonPrimitive(newJson.getAsBoolean());
            } else if (newJson.getAsJsonPrimitive().isNumber()) {
                value = new JsonPrimitive(newJson.getAsNumber());
            } else if (newJson.getAsJsonPrimitive().isString()) {
                value = new JsonPrimitive(newJson.getAsString());
            } else {
                value = newJson.getAsJsonPrimitive();
            }
            return value;
        }

        if (newJson.isJsonArray()) {
            if (!oldJson.isJsonArray()) {
                return useCopy ? gson.fromJson(newJson, JsonArray.class) : newJson;
            } else {
                oldArray = oldJson.getAsJsonArray();
                newArray = newJson.getAsJsonArray();

                if (oldArray.size() > newArray.size()) {
                    while (oldArray.size() > newArray.size()) {
                        oldArray.remove(oldArray.size() - 1);
                    }
                }

                for (int index = 0; index < newArray.size(); index++) {
                    if (index < oldArray.size()) {
                        oldArrayItem = oldArray.get(index);
                        newArrayItem = newArray.get(index);
                        oldArray.set(index, merge(oldArrayItem, newArrayItem, useCopy, gson));
                    } else {
                        oldArray.add(newArray.get(index));
                    }
                }
            }
        } else if (newJson.isJsonObject()) {
            if (!oldJson.isJsonObject()) {
                return useCopy ? gson.fromJson(newJson, JsonObject.class) : newJson;
            } else {
                oldObject = oldJson.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : newJson.getAsJsonObject().entrySet()) {
                    newDataElement = merge(oldObject.get(entry.getKey()), entry.getValue(), useCopy, gson);
                    oldObject.add(entry.getKey(), newDataElement);
                }
            }
        } else {
            return useCopy ? gson.fromJson(newJson, JsonElement.class) : newJson;
        }

        return oldJson;
    }

    public static JsonObject addElements(JsonObject destination, JsonObject source, boolean override) {
        for (Map.Entry<String, JsonElement> entry : source.entrySet()) {
            if (!override && destination.get(entry.getKey()) != null) {
                continue;
            }
            destination.add(entry.getKey(), entry.getValue());
        }
        return destination;
    }

    public static String getStringFromArray(JsonArray array, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).isJsonPrimitive()) {
                sb.append(array.get(i).getAsString());
            } else {
                sb.append(array.get(i).toString());
            }
            if (i < array.size() - 1) {
                sb.append(delimiter).append(" ");
            }
        }
        return sb.toString();
    }

    public static String getPropertyAsString(JsonObject object, String property) {
        if (object == null || object.isJsonNull()) {
            return null;
        }
        JsonElement element = object.get(property);
        if (element == null) {
            return null;
        }
        String string;
        if (!element.isJsonNull() && element.isJsonPrimitive()) {
            string = element.getAsString();
        } else {
            string = element.toString();
        }
        return string;
    }

    @NonNull
    public static String getLayoutIdentifier(JsonObject layout) {
        String noLayoutId = "no ID or TAG.";
        if (layout == null) {
            return noLayoutId;
        }
        String value = Utils.getPropertyAsString(layout, ProteusConstants.ID);
        if (value != null) {
            return "ID: " + value + ".";
        }
        value = Utils.getPropertyAsString(layout, ProteusConstants.TAG);
        if (value != null) {
            return "TAG: " + value + ".";
        }
        return noLayoutId;
    }

    public static Drawable getBorderDrawable(JsonElement attributeValue, Context context) {

        if (!attributeValue.isJsonObject() || attributeValue.isJsonNull()) {
            return null;
        }

        float cornerRadius = 0;
        int borderWidth = 0, borderColor = Color.TRANSPARENT, bgColor = Color.TRANSPARENT;
        JsonObject data = attributeValue.getAsJsonObject();

        String value = Utils.getPropertyAsString(data, ATTRIBUTE_BG_COLOR);
        if (value != null && !value.equals("-1")) {
            bgColor = ParseHelper.parseColor(value);
        }

        value = Utils.getPropertyAsString(data, ATTRIBUTE_BORDER_COLOR);
        if (value != null) {
            borderColor = ParseHelper.parseColor(value);
        }

        value = Utils.getPropertyAsString(data, ATTRIBUTE_BORDER_RADIUS);
        if (value != null) {
            cornerRadius = ParseHelper.parseDimension(value, context);
        }

        value = Utils.getPropertyAsString(data, ATTRIBUTE_BORDER_WIDTH);
        if (value != null) {
            borderWidth = (int) ParseHelper.parseDimension(value, context);
        }

        GradientDrawable border = new GradientDrawable();
        border.setCornerRadius(cornerRadius);
        border.setShape(GradientDrawable.RECTANGLE);
        border.setStroke(borderWidth, borderColor);
        border.setColor(bgColor);

        return border;
    }

    public static String getTagPrefix() {
        return LIB_NAME + ":";
    }

    public static String getVersion() {
        return VERSION;
    }
}
