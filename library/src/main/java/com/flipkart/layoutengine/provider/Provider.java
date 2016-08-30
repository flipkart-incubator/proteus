package com.flipkart.layoutengine.provider;

import com.flipkart.layoutengine.exceptions.InvalidDataPathException;
import com.flipkart.layoutengine.exceptions.JsonNullException;
import com.flipkart.layoutengine.exceptions.NoSuchDataPathException;
import com.flipkart.layoutengine.toolbox.Result;
import com.google.gson.JsonElement;

/**
 * Created by kirankumar on 20/06/14.
 */
public interface Provider extends Cloneable {

    Result getObject(String key, int childIndex);

    JsonElement getData();

    void setData(JsonElement rootElement);

    Provider clone();

}
