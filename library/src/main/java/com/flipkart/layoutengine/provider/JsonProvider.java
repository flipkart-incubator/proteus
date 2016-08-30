package com.flipkart.layoutengine.provider;

import com.flipkart.layoutengine.toolbox.Result;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.StringTokenizer;

/**
 * Created by kirankumar on 24/06/14.
 */
public class JsonProvider implements Provider {
    private JsonElement rootElement;

    public JsonProvider(JsonElement jsonElement) {
        this.rootElement = jsonElement;
    }

    @Override
    public JsonElement getData() {
        return this.rootElement;
    }

    @Override
    public void setData(JsonElement rootElement) {
        this.rootElement = rootElement;
    }

    @Override
    public Result getObject(String key, int childIndex) {
        return getFromObject(key, childIndex);
    }

    private Result getFromObject(String path, int childIndex) {
        JsonElement root = this.rootElement;
        StringTokenizer tokenizer = new StringTokenizer(path, ProteusConstants.DATA_PATH_DELIMITERS);
        JsonElement elementToReturn = root;
        JsonElement tempElement;
        JsonArray tempArray;

        while (tokenizer.hasMoreTokens()) {
            String segment = tokenizer.nextToken();
            if (elementToReturn == null) {
                return Result.NO_SUCH_DATA_PATH_EXCEPTION;
            }
            if (elementToReturn.isJsonNull()) {
                return Result.JSON_NULL_EXCEPTION;
            }
            if ("".equals(segment)) {
                continue;
            }
            if (elementToReturn.isJsonArray()) {
                tempArray = elementToReturn.getAsJsonArray();
                if (ProteusConstants.CHILD_INDEX_REFERENCE.equals(segment)) {
                    if (childIndex < tempArray.size()) {
                        elementToReturn = tempArray.get(childIndex);
                    } else {
                        return Result.NO_SUCH_DATA_PATH_EXCEPTION;
                    }
                } else if (ProteusConstants.ARRAY_DATA_LENGTH_REFERENCE.equals(segment)) {
                    elementToReturn = new JsonPrimitive(tempArray.size());
                } else if (ProteusConstants.ARRAY_DATA_LAST_INDEX_REFERENCE.equals(segment)) {
                    if (tempArray.size() == 0) {
                        return Result.NO_SUCH_DATA_PATH_EXCEPTION;
                    }
                    elementToReturn = tempArray.get(tempArray.size() - 1);
                } else {
                    int index;
                    try {
                        index = Integer.parseInt(segment);
                    } catch (NumberFormatException e) {
                        return Result.INVALID_DATA_PATH_EXCEPTION;
                    }
                    if (index < tempArray.size()) {
                        elementToReturn = tempArray.get(index);
                    } else {
                        return Result.NO_SUCH_DATA_PATH_EXCEPTION;
                    }
                }

            } else if (elementToReturn.isJsonObject()) {
                tempElement = elementToReturn.getAsJsonObject().get(segment);
                if (tempElement != null) {
                    elementToReturn = tempElement;
                } else {
                    return Result.NO_SUCH_DATA_PATH_EXCEPTION;
                }
            } else if (elementToReturn.isJsonPrimitive()) {
                return Result.INVALID_DATA_PATH_EXCEPTION;
            } else {
                return Result.NO_SUCH_DATA_PATH_EXCEPTION;
            }
        }
        if (elementToReturn.isJsonNull()) {
            return Result.JSON_NULL_EXCEPTION;
        }
        return Result.success(elementToReturn);
    }

    @Override
    public Provider clone() {
        try {
            return (Provider) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
