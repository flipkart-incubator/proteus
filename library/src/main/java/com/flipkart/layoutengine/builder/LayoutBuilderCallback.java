package com.flipkart.layoutengine.builder;

import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by kiran.kumar on 18/05/14.
 */
public interface LayoutBuilderCallback {

    // called when the builder encounters an attribute key which is unhandled by its parser.
    public void onUnknownAttribute(String attribute, JsonElement element, JsonObject object, View view);

    // called when the builder encounters a view type which it cannot understand.
    public void onUnknownViewType(String viewType, JsonObject object, ViewGroup parent);
}
