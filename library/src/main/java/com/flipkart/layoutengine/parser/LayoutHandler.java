package com.flipkart.layoutengine.parser;

import android.content.Context;
import android.view.View;
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
public interface LayoutHandler<E extends View> {

    //create the view and return it.
    E createView(ParserContext parserContext, Context context, ViewGroup parent, JsonObject object);

    //ask the handler to handle the attributes of the view it created
    boolean handleAttribute(ParserContext context, String attribute, JsonObject jsonObject, JsonElement element, ProteusView<E> view, int childIndex);

    //ask the handler to parse the value of 'children' and return an array of children elements.
    JsonArray parseChildren(ParserContext context, JsonElement element, int childIndex);

    boolean canAddChild();

    void setupView(ViewGroup parent, E view);

    void prepare(Context context);

    void addChildren(Context context, ProteusView<View> parent, List<ProteusView> children);
}
