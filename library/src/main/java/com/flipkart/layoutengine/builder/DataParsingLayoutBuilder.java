package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.binding.Binding;
import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.provider.DataParsingAdapter;
import com.flipkart.layoutengine.provider.Provider;
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
    private Map<String, Binding> bindings = new HashMap<String, Binding>();


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
        return super.build(parent, layout, data);
    }

    @Override
    protected ParserContext createParserContext(JsonObject data) {
        ParserContext context = super.createParserContext(data);
        if (data == null) {
            context.setDataProvider(dataProvider);
        }
        return context;
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
        ProteusView view = super.buildImpl(context, parent, jsonObject, existingView, childIndex);
        return view;
    }

}
