package com.flipkart.layoutengine.provider;

import com.google.gson.JsonElement;

/**
 * Created by kirankumar on 20/06/14.
 */
public interface Provider extends Cloneable {
    public JsonElement getObject(String key, int index);
    public void setRoot(JsonElement rootElement);
    public Provider clone();

}
