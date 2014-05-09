package com.flipkart.layoutengine.parser;

import android.content.Context;
import android.view.View;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.w3c.dom.Attr;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kiran.kumar on 11/05/14.
 */
public abstract class BaseParser<T extends View> implements LayoutHandler<T> {

    private final Class<T> viewClass;
    private Map<String,AttributeProcessor> handlers = new HashMap<String, AttributeProcessor>();

    public BaseParser(Class<T> viewClass) {
        this.viewClass = viewClass; prepareHandlers();
    }

    protected void addHandler(String key, AttributeProcessor handler)
    {
        handlers.put(key,handler);
    }

    @Override
    public T parse(Context context, JsonObject object) {
        T v = null;
        try {
            v = this.viewClass.getDeclaredConstructor(Context.class).newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Map.Entry<String,JsonElement> entry : object.entrySet())
        {
            handleNewAttribute(entry.getKey(),entry.getValue(),v);
        }
        return v;
    }

    private void handleNewAttribute(String key, JsonElement value, View view) {
        AttributeProcessor attributeProcessor = handlers.get(key);
        if(attributeProcessor!=null) {
            attributeProcessor.handle(value.getAsString(), view);
        }
    }

    @Override
    public boolean canAddChild() {
        return false;
    }

    @Override
    public void addChild(Context context, View view) {

    }

    protected abstract void prepareHandlers();


    protected static abstract class AttributeProcessor<E> {
        public abstract void handle(String attributeValue,E view);

    }
}
