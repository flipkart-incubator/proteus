package com.flipkart.layoutengine.parser;

import android.content.Context;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * @author kiran.kumar
 */
public interface LayoutHandler<E> {

    //create the view and return it.
    public E createView(ParserContext parserContext, Context context, ViewGroup parent, JsonObject object);

    //ask the handler to handle the attributes of the view it created
    public boolean handleAttribute(ParserContext context, String attribute, JsonObject jsonObject, JsonElement element, E view, int childIndex);

    //ask the handler to parse the value of 'children' and return an array of children elements.
    public JsonArray parseChildren(ParserContext context, JsonElement element, int childIndex);

    public boolean canAddChild();

    public void setupView(ViewGroup parent, E view);

    public void prepare(Context context);

    public void addChildren(Context context, E parent, List<ProteusView> children);
}
