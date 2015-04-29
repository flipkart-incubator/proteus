package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.binding.Binding;
import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.provider.Provider;
import com.flipkart.layoutengine.view.DataProteusView;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A layout builder which can parse @data blocks before passing it on to {@link SimpleLayoutBuilder}
 */
public class DataParsingLayoutBuilder extends SimpleLayoutBuilder {
    private final Provider dataProvider;
    private Map<String, Binding> bindings = new HashMap<>();

    DataParsingLayoutBuilder(Context context, Provider dataProvider) {
        super(context);
        this.dataProvider = dataProvider;
    }

    @Override
    protected JsonArray parseChildren(LayoutHandler handler, ParserContext context, JsonElement childrenElement, int childIndex) {
        childrenElement = getElementFromData(childrenElement, context.getDataProvider(), childIndex);
        return super.parseChildren(handler, context, childrenElement, childIndex);
    }

    @Override
    public ProteusView build(ViewGroup parent, JsonObject layout, JsonObject data) {
        return new DataProteusView(super.build(parent, layout, data).getView(), this.bindings);
    }

    @Override
    protected ProteusView buildImpl(ParserContext context, ViewGroup parent, JsonObject jsonObject, View existingView, int childIndex) {
        JsonElement dataContextElement = jsonObject.get("dataContext");
        if (dataContextElement != null) {
            ParserContext newContext = context.clone();
            Provider oldProvider = context.getDataProvider();
            if (oldProvider != null) {
                Provider newProvider = oldProvider.clone();
                JsonElement newRoot = getElementFromData(dataContextElement, oldProvider, childIndex);
                newProvider.setRoot(newRoot);
                newContext.setDataProvider(newProvider);
                context = newContext;
            } else {
                Log.e(TAG, "When dataContext is specified, data provider cannot be null");
            }
        }
        return super.buildImpl(context, parent, jsonObject, existingView, childIndex);
    }

    @Override
    public boolean handleAttribute(LayoutHandler<View> handler, ParserContext context, String attributeName, JsonObject jsonObject, JsonElement jsonDataValue, View createdView, ViewGroup parent, int childIndex) {
        if (jsonDataValue.isJsonPrimitive()) {
            String attributeValue = jsonDataValue.getAsString();
            JsonElement elementFromData = getElementFromData(jsonDataValue, context.getDataProvider(), childIndex);
            if (elementFromData != null) {
                Binding binding = new Binding(context, handler, attributeName, attributeValue, createdView, parent, childIndex);
                jsonDataValue = elementFromData;
                bindings.put(attributeValue, binding);
            }
        }
        return super.handleAttribute(handler, context, attributeName, jsonObject, jsonDataValue, createdView, parent, childIndex);
    }

    @Override
    protected ParserContext createParserContext(JsonObject data) {
        ParserContext context = super.createParserContext(data);
        if (data == null) {
            context.setDataProvider(dataProvider);
        }
        return context;
    }
}
