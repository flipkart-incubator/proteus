package com.flipkart.layoutengine.builder;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.provider.Provider;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A layout builder which can parse @data blocks before passing it on to {@link SimpleLayoutBuilder}
 */
public class DataParsingLayoutBuilder extends SimpleLayoutBuilder {
    private final Provider dataProvider;
    private static final Character PREFIX = '@';


    DataParsingLayoutBuilder(Activity activity, Provider dataProvider) {
        super(activity);
        this.dataProvider = dataProvider;
    }

    @Override
    protected boolean handleAttribute(LayoutHandler handler, ParserContext context, String attribute, JsonElement element, View view) {
        element = getElementFromData(element, context.getDataProvider());
        return super.handleAttribute(handler, context, attribute, element, view);
    }

    @Override
    protected JsonArray parseChildren(LayoutHandler handler, ParserContext context, JsonElement childrenElement) {
        childrenElement = getElementFromData(childrenElement, context.getDataProvider());
        return super.parseChildren(handler, context, childrenElement);
    }

    @Override
    public View build(ViewGroup parent, JsonObject jsonObject) {
        return super.build(parent, jsonObject);
    }

    @Override
    protected ParserContext createParserContext() {
        ParserContext context = super.createParserContext();
        context.setDataProvider(dataProvider);
        return context;
    }

    @Override
    protected View buildImpl(ParserContext context, ViewGroup parent, JsonObject jsonObject) {
        JsonElement dataContextElement = jsonObject.get("dataContext");
        if(dataContextElement!=null)
        {
            ParserContext newContext = context.clone();
            Provider oldProvider = context.getDataProvider();
            if(oldProvider!=null) {
                Provider newProvider = oldProvider.clone();
                JsonElement newRoot = getElementFromData(dataContextElement,oldProvider);
                newProvider.setRoot(newRoot);
                newContext.setDataProvider(newProvider);
                context = newContext;
            }
            else
            {
                Log.e(TAG,"When dataContext is specified, data provider cannot be null");
            }

        }
        View view = super.buildImpl(context, parent, jsonObject);
        return view;
    }

    private JsonElement getElementFromData(JsonElement element, Provider dataProvider)
    {
        if(element.isJsonPrimitive())
        {
            String dataSourceKey = element.getAsString();
            if(dataSourceKey.charAt(0) == PREFIX) {
                element = dataProvider.getObject(dataSourceKey.substring(1));
            }
        }
        return element;
    }
}
