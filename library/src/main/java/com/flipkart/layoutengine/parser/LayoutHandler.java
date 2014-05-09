package com.flipkart.layoutengine.parser;

import android.content.Context;
import android.view.View;

import com.google.gson.JsonObject;

/**
 * Created by kiran.kumar on 11/05/14.
 */
public interface LayoutHandler<E> {

    public E parse(Context context, JsonObject object);
    public boolean canAddChild();
    public void addChild(Context context, View view);
}
