package com.flipkart.layoutengine.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * Created by kirankumar on 24/06/14.
 */
public class GsonProvider implements Provider {
    private JsonElement rootElement;
    public static final String DATA_PATH_DELIMITER = "\\.|\\[|\\]";
    public static final String CHILD_INDEX_REFERENCE = "$index";

    public GsonProvider(JsonElement jsonElement) {
        this.rootElement = jsonElement;
    }

    @Override
    public void setRoot(JsonElement rootElement) {
        this.rootElement = rootElement;
    }

    @Override
    public JsonElement getRoot() {
        return this.rootElement;
    }

    @Override
    public JsonElement getObject(String key, int childIndex) {
        return getFromObject(key, childIndex);
    }

    private JsonElement getFromObject(String path, int childIndex) {
        JsonElement root = this.rootElement;
        String[] segments = path.split(DATA_PATH_DELIMITER);
        JsonElement elementToReturn = root;
        JsonElement tempElement = null;
        JsonArray tempArray = null;

        for (String segment : segments) {
            if (elementToReturn == null) {
                return null;
            }
            if ("".equals(segment)) {
                continue;
            }
            if (elementToReturn.isJsonArray()) {
                tempArray = elementToReturn.getAsJsonArray();
                if (tempArray != null) {
                    if (CHILD_INDEX_REFERENCE.equals(segment)) {
                        elementToReturn = tempArray.get(childIndex);
                    } else {
                        int index = Integer.parseInt(segment);
                        elementToReturn = tempArray.get(index);
                    }
                } else {
                    elementToReturn = null;
                    break;
                }
            } else {
                tempElement = elementToReturn.getAsJsonObject().get(segment);
                if (tempElement != null) {
                    elementToReturn = tempElement;
                } else {
                    elementToReturn = null;
                    break;
                }
            }
        }
        return elementToReturn;
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
