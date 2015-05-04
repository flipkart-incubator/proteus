package com.flipkart.layoutengine.provider;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Adds support for parsing data blocks before passing it to the enclosing handler.
 */
public class DataParsingAdapter<E extends View> implements LayoutHandler<E> {

    private final Provider dataProvider;
    private LayoutHandler<E> handler;
    public static final Character PREFIX = '$';

    public DataParsingAdapter(Provider dataProvider, LayoutHandler<E> enclosingHandler) {
        this.dataProvider = dataProvider;
        this.handler = enclosingHandler;
    }

    @Override
    public E createView(ParserContext parserContext, Context context, ViewGroup parent, JsonObject object) {
        return handler.createView(parserContext, context,parent,object);
    }

    @Override
    public boolean handleAttribute(ParserContext context, String attribute, JsonObject jsonObject, JsonElement element, ProteusView<E> view, int index) {
        element = getElementFromData(element, index);
        return handler.handleAttribute(context,attribute, jsonObject, element, view, index);
    }

    private JsonElement getElementFromData(JsonElement element, int index)
    {
        if(element.isJsonPrimitive())
        {
            String dataSourceKey = element.getAsString();
            if(dataSourceKey.charAt(0) == PREFIX) {
                element = dataProvider.getObject(dataSourceKey.substring(1),index);
            }
        }
        return element;
    }

    @Override
    public boolean canAddChild() {
        return handler.canAddChild();
    }

    @Override
    public void setupView(ViewGroup parent, E view) {
        handler.setupView(parent,view);
    }

    @Override
    public void prepare(Context context) {
        handler.prepare(context);
    }

    @Override
    public void addChildren(Context context, ProteusView<View> parent, List<ProteusView> children) {
        handler.addChildren(context,parent,children);
    }

    @Override
    public JsonArray parseChildren(ParserContext context, JsonElement element, int index) {
        element = getElementFromData(element, index);
        return handler.parseChildren(context, element, index);
    }
}
