package com.flipkart.layoutengine.builder;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.util.List;
import java.util.Map;

/**
 * Created by kiran.kumar on 11/05/14.
 */
public interface LayoutHandler<E> {

    //create the view and return it.
    public E createView(ParserContext context, Activity activity, ViewGroup parent, JsonObject object);

    //ask the handler to handle the attributes of the view it created
    public boolean handleAttribute(ParserContext context, String attribute, JsonElement element, E view);

    //ask the handler to parse the value of 'children' and return an array of children elements.
    public JsonArray parseChildren(ParserContext context, JsonElement element);


    public boolean canAddChild();
    public void setupView(ViewGroup parent, E view);
    public void prepare(Activity activity);
    public void addChildren(Activity activity, E parent, List<View> children);
}
