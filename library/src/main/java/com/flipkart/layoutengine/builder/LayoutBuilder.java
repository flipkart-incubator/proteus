package com.flipkart.layoutengine.builder;

import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.parser.LayoutHandler;
import com.google.gson.JsonObject;

/**
 * Created by kirankumar on 06/01/15.
 */
public interface LayoutBuilder {
    void registerHandler(String viewType, LayoutHandler handler);

    void unregisterHandler(String viewType);

    void unregisterAllHandlers();

    LayoutHandler getHandler(String viewType);

    View build(ViewGroup parent, JsonObject jsonObject);

    LayoutBuilderCallback getListener();

    void setListener(LayoutBuilderCallback listener);

    boolean isSynchronousRendering();

    void setSynchronousRendering(boolean isSynchronousRendering);
}
