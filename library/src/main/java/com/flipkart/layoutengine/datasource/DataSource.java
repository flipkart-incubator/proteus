package com.flipkart.layoutengine.datasource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by kirankumar on 20/06/14.
 */
public interface DataSource {
    public JsonElement getObject(String key);
}
