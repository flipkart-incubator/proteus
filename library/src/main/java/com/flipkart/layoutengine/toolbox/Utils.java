package com.flipkart.layoutengine.toolbox;

import com.flipkart.layoutengine.exceptions.InvalidDataPathException;
import com.flipkart.layoutengine.exceptions.JsonNullException;
import com.flipkart.layoutengine.exceptions.NoSuchDataPathException;
import com.flipkart.layoutengine.provider.JsonProvider;
import com.flipkart.layoutengine.provider.ProteusConstants;
import com.google.gson.JsonArray;
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
    public static final String VERSION = "2.6.12-SNAPSHOT";
    public static final String TAG = getTagPrefix() + Utils.class.getSimpleName();

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

    public static JsonObject merge(JsonObject oldJson, JsonObject newJson) {
        String key;
        JsonElement oldDataElement;
        JsonElement newDataElement;
        JsonArray oldArray;
        JsonArray newArray;
        JsonElement oldArrayItem;
        JsonElement newArrayItem;

        for (Map.Entry<String, JsonElement> entry : newJson.entrySet()) {
            key = entry.getKey();
            oldDataElement = oldJson.get(key);
            newDataElement = newJson.get(key);
            if (oldDataElement != null && oldDataElement.isJsonObject() && newDataElement != null) {
                newDataElement = merge(oldDataElement.getAsJsonObject(), newDataElement.getAsJsonObject());
            } else if (oldDataElement != null && oldDataElement.isJsonArray() && newDataElement != null) {
                oldArray = oldDataElement.getAsJsonArray();
                newArray = newDataElement.getAsJsonArray();

                if (oldArray.size() > newArray.size()) {
                    while (oldArray.size() > newArray.size()) {
                        oldArray.remove(oldArray.size() - 1);
                    }
                }

                for (int index = 0; index < newArray.size(); index++) {
                    if (index < oldArray.size()) {
                        oldArrayItem = oldArray.get(index);
                        newArrayItem = newArray.get(index);
                        if (oldArrayItem.isJsonObject() && newArrayItem.isJsonObject()) {
                            oldArray.set(index, merge(oldArrayItem.getAsJsonObject(), newArrayItem.getAsJsonObject()));
                        } else {
                            oldArray.set(index, newArrayItem);
                        }
                    } else {
                        oldArray.add(newArray.get(index));
                    }
                }

                newDataElement = oldArray;
            }
            oldJson.add(key, newDataElement);
        }
        return oldJson;
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

    public static String getTagPrefix() {
        return LIB_NAME + ":" + VERSION + ":";
    }

}
