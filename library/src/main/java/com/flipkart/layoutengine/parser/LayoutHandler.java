package com.flipkart.layoutengine.parser;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;

import java.util.List;

/**
 * Created by kiran.kumar on 11/05/14.
 */
public interface LayoutHandler<E> {

    public E parse(Activity activity, ViewGroup parent, JsonObject object);
    public boolean canAddChild();
    public void prepare(Activity activity);
    public void addChildren(Activity activity, E parent, List<View> children);
}
