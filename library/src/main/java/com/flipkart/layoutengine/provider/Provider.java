package com.flipkart.layoutengine.provider;

import com.google.gson.JsonElement;

/**
 * Created by kirankumar on 20/06/14.
 */
public interface Provider extends Cloneable {
    JsonElement getObject(String key, int childIndex);
    void setRoot(JsonElement rootElement);
    JsonElement getRoot();
    Provider clone();

}
