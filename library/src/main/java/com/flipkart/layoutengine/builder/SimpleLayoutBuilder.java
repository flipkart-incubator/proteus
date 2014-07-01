package com.flipkart.layoutengine.builder;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A layout builder which can parse json to construct an android view out of it. It uses the registered handlers to convert the json string to a view and then assign attributes.
 */
public class SimpleLayoutBuilder {

    private static final String TAG = SimpleLayoutBuilder.class.getSimpleName();
    private HashMap<String,LayoutHandler> viewClassMap = new HashMap<String, LayoutHandler>();
    private LayoutBuilderCallback listener;

    /**
     * Package private constructor so that no client can access it without the factory class
     */
    SimpleLayoutBuilder() {

    }

    /**
     * Registers a {@link LayoutHandler} for the specified view type. All the attributes will pass through {@link LayoutHandler#handleAttribute(String, com.google.gson.JsonElement, Object)} and expect to be handled.
     * @param viewType The string value for "view" attribute.
     * @param handler The handler which should handle this view.
     */
    public void registerHandler(String viewType,LayoutHandler handler)
    {
        viewClassMap.put(viewType,handler);
    }

    /**
     * Unregisters the specified view type.
     * @param viewType The string value for "view" attribute.
     */
    public void unregisterHandler(String viewType)
    {
        viewClassMap.remove(viewType);
    }

    /**
     * Unregisters all handlers.
     */
    public void unregisterAllHandlers()
    {
        viewClassMap.clear();
    }



    private Activity activity;

    public SimpleLayoutBuilder(Activity activity) {
        this.activity = activity;
    }

    public View build(ViewGroup parent, JsonObject jsonObject)
    {
        return buildImpl(parent,jsonObject);
    }

    private View buildImpl(ViewGroup parent, JsonObject jsonObject)
    {
        JsonElement viewTypeElement = jsonObject.get("view");
        String viewType = null;
        if(viewTypeElement!=null) {
            viewType = viewTypeElement.getAsString();
            jsonObject.remove("view");
        }
        else
        {
            Log.e(TAG,"view cannot be null");
            return null;
        }

        LayoutHandler<View> handler = viewClassMap.get(viewType);
        if(handler == null)
        {
            if(listener!=null)
            {
                listener.onUnknownViewType(viewType,jsonObject,parent);
                return null;
            }
        }

        JsonElement childrenElement = jsonObject.get("children");
        JsonArray children = null;
        if(childrenElement!=null) {
            children = handler.parseChildren(childrenElement);
            jsonObject.remove("children");
        }

        JsonElement childViewElement = jsonObject.get("childView");
        handler.prepare(activity);
        View self = createView(parent, handler, jsonObject);
        handler.setupView(parent,self);
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {

            boolean handled = handler.handleAttribute(entry.getKey(), entry.getValue(),self);

            if(!handled)
            {
                if(listener!=null)
                {
                    listener.onUnknownAttribute(entry.getKey(),entry.getValue(),jsonObject,self);
                }
            }
        }

        if(children!=null && children.size()>0) {
            ViewGroup selfViewGroup = (ViewGroup) self;
            List<View> childrenToAdd = new ArrayList<View>();
            for (int i = 0; i < children.size(); i++) {

                JsonObject childObject = children.get(i).getAsJsonObject();
                if(childViewElement!=null)
                {
                    // propagate the value of 'childView' to the recursive calls
                    childObject.add("view",childViewElement);
                }
                View childView = buildImpl(selfViewGroup, childObject);
                if(childView!=null) {
                    childrenToAdd.add(childView);
                }

            }
            if(childrenToAdd.size()>0) {
                handler.addChildren(activity, selfViewGroup, childrenToAdd);
            }
        }
        return self;


    }

    public View createView(ViewGroup parent, LayoutHandler<View> handler, JsonObject object)
    {
        View view = handler.createView(activity, parent, object);
        return view;
    }


    public LayoutBuilderCallback getListener() {
        return listener;
    }

    public void setListener(LayoutBuilderCallback listener) {
        this.listener = listener;
    }
}
