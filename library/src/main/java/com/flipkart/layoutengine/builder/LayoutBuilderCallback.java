package com.flipkart.layoutengine.builder;

import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by kiran.kumar on 18/05/14.
 */
public interface LayoutBuilderCallback {

    // called when the builder encounters an attribute key which is unhandled by its parser.
    public void onUnknownAttribute(ParserContext context, String attribute, JsonElement element, JsonObject object, View view, int index);

    // called when the builder encounters a view type which it cannot understand.
    public View onUnknownViewType(ParserContext context, String viewType, JsonObject object, ViewGroup parent, int index);

    //called when any click occurs on views
    public View onClickView(ParserContext context, String viewType, JsonObject object,View view , ViewGroup parent, int index);

}
