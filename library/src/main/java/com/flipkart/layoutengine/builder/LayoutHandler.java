package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Created by kiran.kumar on 11/05/14.
 */
public interface LayoutHandler<E> {

    //create the view and return it.
    public E createView(ParserContext parserContext, Context context, ViewGroup parent, JsonObject object);

    //ask the handler to handle the attributes of the view it created
    public boolean handleAttribute(ParserContext context, String attribute, JsonObject jsonObject, JsonElement element, E view, int index);

    //ask the handler to parse the value of 'children' and return an array of children elements.
    public JsonArray parseChildren(ParserContext context, JsonElement element, int index);


    public boolean canAddChild();
    public void setupView(ViewGroup parent, E view);
    public void prepare(Context context);
    public void addChildren(Context context, E parent, List<View> children);
}
