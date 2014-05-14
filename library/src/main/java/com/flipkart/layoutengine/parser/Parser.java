package com.flipkart.layoutengine.parser;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class will help parsing by introducing handlers. Any subclass can use addHandler() method  to specify a callback for an attribute.
 * This class also creates an instance of the view with the first constructor.
 */
public abstract class Parser<T extends View> implements LayoutHandler<T> {

    private final Class<T> viewClass;
    private Map<String, AttributeProcessor> handlers = new HashMap<String, AttributeProcessor>();

    public Parser(Class<T> viewClass) {
        this.viewClass = viewClass;
    }

    @Override
    public void prepare(Activity activity) {
        prepareHandlers(activity);
    }

    protected void addHandler(String key, AttributeProcessor<T> handler) {
        handlers.put(key, handler);
    }

    @Override
    public T parse(Activity activity, ViewGroup parent, JsonObject object) {
        T v = null;
        try {
            v = this.viewClass.getDeclaredConstructor(Context.class).newInstance(activity);
            ViewGroup.LayoutParams layoutParams = generateDefaultLayoutParams(parent, object);
                v.setLayoutParams(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            handleNewAttribute(entry.getKey(), entry.getValue(), v);
        }
        return v;
    }

    protected abstract ViewGroup.LayoutParams generateDefaultLayoutParams(ViewGroup parent, JsonObject object);


    private void handleNewAttribute(String key, JsonElement value, View view) {
        AttributeProcessor attributeProcessor = handlers.get(key);
        if (attributeProcessor != null) {
            attributeProcessor.handle(value.getAsString(), view);
        }
    }

    @Override
    public boolean canAddChild() {
        return false;
    }

    protected abstract void prepareHandlers(Activity activity);


    protected static abstract class AttributeProcessor<E> {
        public abstract void handle(String attributeValue, E view);

    }

    /**
     * This is a base implementation which calls addChild() on the parent.
     *
     * @param activity Android context
     * @param parent   The view group on whom the child will be added.
     * @param children The List of Child views which have to be added.
     */
    @Override
    public void addChildren(Activity activity, T parent, List<View> children) {
        for (View child : children) {
            ((ViewGroup) parent).addView(child);
        }
    }


}
