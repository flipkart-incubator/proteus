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
    private static final Character PREFIX = DataParsingAdapter.PREFIX;
    private Map<String,Binding> bindings = new HashMap<String,Binding>();


    DataParsingLayoutBuilder(Context context, Provider dataProvider) {
        super(context);
        this.dataProvider = dataProvider;
    }



    @Override
    protected boolean handleAttribute(LayoutHandler handler, ParserContext context, String attributeKey, JsonElement element, View view, ViewGroup parent) {
        if(element.isJsonPrimitive()) {
            String attributeValue = element.getAsString();
            JsonElement elementFromData = getElementFromData(element, context.getDataProvider());
            if (elementFromData != null) {
                Binding binding = new Binding(context, attributeKey, attributeValue, view, parent);
                element = elementFromData;
                bindings.put(attributeValue, binding);
            }
        }
        return super.handleAttribute(handler, context, attributeKey, element, view, parent);

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
    protected View buildImpl(ParserContext context, ViewGroup parent, JsonObject jsonObject, View existingView) {
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
        View view = super.buildImpl(context, parent, jsonObject, existingView);
        return view;
    }


    private JsonElement getElementFromData(JsonElement element, Provider dataProvider)
    {
        if(element.isJsonPrimitive())
        {
            String dataSourceKey = element.getAsString();
            if(dataSourceKey.charAt(0) == PREFIX) {
                JsonElement tempElement = dataProvider.getObject(dataSourceKey.substring(1));
                if(tempElement !=null)
                {
                    element = tempElement;
                }
                else
                {
                    Log.e(TAG,"Got null element for "+dataSourceKey);
                }
            }
        }
        return element;
    }
}
