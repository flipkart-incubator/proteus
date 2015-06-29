package com.flipkart.layoutengine.provider;

import com.flipkart.layoutengine.exceptions.InvalidDataPathException;
import com.flipkart.layoutengine.exceptions.JsonNullException;
import com.flipkart.layoutengine.exceptions.NoSuchDataPathException;
import com.google.gson.JsonElement;

/**
 * Created by kirankumar on 20/06/14.
 */
public interface Provider extends Cloneable {
    JsonElement getObject(String key, int childIndex) throws InvalidDataPathException, NoSuchDataPathException, JsonNullException;
    void setData(JsonElement rootElement);
    JsonElement getData();
    Provider clone();

}
