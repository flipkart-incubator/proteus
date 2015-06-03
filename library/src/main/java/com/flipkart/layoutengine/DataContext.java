package com.flipkart.layoutengine;

import com.flipkart.layoutengine.provider.GsonProvider;
import com.google.gson.JsonObject;

/**
 * @author Aditya Sharat
 */
public class DataContext {
    private GsonProvider dataProvider;
    private JsonObject reverseScopeMap;
    private DataContext parent;

    public DataContext() {
    }

    public DataContext(GsonProvider dataProvider, JsonObject reverseScopeMap, DataContext parent) {
        this.dataProvider = dataProvider;
        this.reverseScopeMap = reverseScopeMap;
        this.parent = parent;
    }

    public JsonObject getReverseScopeMap() {
        return reverseScopeMap;
    }

    public void setReverseScopeMap(JsonObject reverseScopeMap) {
        this.reverseScopeMap = reverseScopeMap;
    }

    public GsonProvider getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(GsonProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public DataContext getParent() {
        return parent;
    }

    public void setParent(DataContext parent) {
        this.parent = parent;
    }
}
