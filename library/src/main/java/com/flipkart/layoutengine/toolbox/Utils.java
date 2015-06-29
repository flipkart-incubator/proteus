package com.flipkart.layoutengine.toolbox;

import com.flipkart.layoutengine.exceptions.InvalidDataPathException;
import com.flipkart.layoutengine.exceptions.JsonNullException;
import com.flipkart.layoutengine.exceptions.NoSuchDataPathException;
import com.flipkart.layoutengine.provider.JsonProvider;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Map;
import java.util.Set;

/**
 * @author Aditya Sharat
 */
public class Utils {
    public static final String LIB_NAME = "proteus";
    public static final String VERSION = "2.5.4-RC1";
    public static final String TAG = getTagPrefix() + Utils.class.getSimpleName();

    public static JsonElement getElementFromData(String dataPath, JsonProvider dataProvider, int childIndex)
            throws JsonNullException, NoSuchDataPathException, InvalidDataPathException {
        // replace CHILD_INDEX_REFERENCE reference with index value
        if (JsonProvider.CHILD_INDEX_REFERENCE.equals(dataPath)) {
            dataPath = dataPath.replace(JsonProvider.CHILD_INDEX_REFERENCE, String.valueOf(childIndex));
            return Utils.getStringAsJsonElement(dataPath);
        } else {
            return dataProvider.getObject(dataPath, childIndex);
        }
    }

    public static JsonObject merge(JsonObject x, JsonObject y) {
        for (Map.Entry<String, JsonElement> entry : y.entrySet()) {
            String key = entry.getKey();
            JsonElement oldDataElement = x.get(key);
            JsonElement newDataElement = y.get(key);
            if (oldDataElement != null && oldDataElement.isJsonObject() && newDataElement != null) {
                newDataElement = merge(entry.getValue().getAsJsonObject(), newDataElement.getAsJsonObject());
            }
            x.add(key, newDataElement);
        }
        return x;
    }

    public static JsonObject addElements(JsonObject jsonObject, Set<Map.Entry<String, JsonElement>> members, boolean override) {
        for (Map.Entry<String, JsonElement> entry : members) {
            if (override && jsonObject.get(entry.getKey()) != null) {
                break;
            }
            jsonObject.add(entry.getKey(), entry.getValue());
        }
        return jsonObject;
    }

    public static JsonElement getStringAsJsonElement(String string) {
        return new JsonPrimitive(string);
    }

    public static JsonElement getNumberAsJsonElement(Number number) {
        return new JsonPrimitive(number);
    }

    public static JsonElement getBooleanAsJsonElement(Boolean aBoolean) {
        return new JsonPrimitive(aBoolean);
    }

    public static String format(String value, String formatterName) {
        return Formatters.get(formatterName).format(value);
    }

    public static String getTagPrefix() {
        return LIB_NAME + ":" + VERSION + ":";
    }

}
