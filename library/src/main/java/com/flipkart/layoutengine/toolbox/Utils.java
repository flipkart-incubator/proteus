package com.flipkart.layoutengine.toolbox;

import android.util.Log;

import com.flipkart.layoutengine.provider.Provider;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * Created by Aditya Sharat on 08-04-2015.
 */
public class Utils {

    public static final String TAG = Utils.class.getSimpleName();

    public static JsonElement getElementFromData(String dataPath, Provider dataProvider, int childIndex) {
        if (dataPath != null && dataPath.length() > 0) {
            JsonElement elementToReturn = dataProvider.getObject(dataPath, childIndex);
            if (elementToReturn != null) {
                return elementToReturn;
            } else {
                Log.e(TAG, "Got null for dataPath " + dataPath);
            }
        }
        return Utils.getStringAsJsonElement(dataPath);
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

    public static JsonElement getStringAsJsonElement(String string) {
        JsonObject temp = new JsonObject();
        temp.addProperty("value", string);
        return temp.get("value");
    }

    public static JsonElement getNumberAsJsonElement(Number number) {
        JsonObject temp = new JsonObject();
        temp.addProperty("value", number);
        return temp.get("value");
    }

    public static JsonElement getBooleanAsJsonElement(Boolean aBoolean) {
        JsonObject temp = new JsonObject();
        temp.addProperty("value", aBoolean);
        return temp.get("value");
    }

    public static String format(String value, String formatterName) {
        return Formatters.get(formatterName).format(value);
    }

}
