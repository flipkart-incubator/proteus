package com.flipkart.layoutengine.builder;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
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

    protected static final String TAG = SimpleLayoutBuilder.class.getSimpleName();
    private HashMap<String,LayoutHandler> viewClassMap = new HashMap<String, LayoutHandler>();
    private LayoutBuilderCallback listener;

    /**
     * Package private constructor so that no client can access it without the factory class
     */
    SimpleLayoutBuilder() {}

    /**
     * Registers a {@link LayoutHandler} for the specified view type. All the attributes will pass through {@link LayoutHandler#handleAttribute(com.flipkart.layoutengine.ParserContext, String, com.google.gson.JsonElement, Object)} and expect to be handled.
     * @param viewType The string value for "view" attribute.
     * @param handler The handler which should handle this view.
     */
    public void registerHandler(String viewType,LayoutHandler handler)
    {
        handler.prepare(activity);
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
        return buildImpl(createParserContext(), parent,jsonObject);
    }


    protected ParserContext createParserContext()
    {
        return new ParserContext();
    }

    protected View buildImpl(ParserContext context, ViewGroup parent, JsonObject jsonObject)
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
            return onUnknownViewEncountered(context,viewType,parent,jsonObject);
        }

        JsonElement childrenElement = jsonObject.get("children");
        JsonArray children = null;
        if(childrenElement!=null) {
            children = parseChildren(handler,context,childrenElement);
            jsonObject.remove("children");
        }

        JsonElement childViewElement = jsonObject.get("childView");
        View self = createView(context, parent, handler, jsonObject);
        handler.setupView(parent,self);
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {

            boolean handled = handleAttribute(handler,context,entry.getKey(),entry.getValue(),self);

            if(!handled)
            {
                onUnknownAttributeEncountered(context, entry.getKey(), entry.getValue(), jsonObject, self);
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
                View childView = buildImpl(context, selfViewGroup, childObject);
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

    protected JsonArray parseChildren(LayoutHandler handler, ParserContext context, JsonElement childrenElement)
    {
        return handler.parseChildren(context,childrenElement);
    }

    protected boolean handleAttribute(LayoutHandler handler, ParserContext context, String attribute, JsonElement element, View view)
    {
        return handler.handleAttribute(context, attribute, element, view);
    }

    protected void onUnknownAttributeEncountered(ParserContext context, String attribute, JsonElement element, JsonObject object, View view)
    {
        if(listener!=null)
        {
            listener.onUnknownAttribute(context,attribute,element, object, view);
        }
    }

    protected View onUnknownViewEncountered(ParserContext context, String viewType, ViewGroup parent, JsonObject jsonObject) {

        if(listener!=null)
        {
            return listener.onUnknownViewType(context,viewType,jsonObject,parent);
        }
        return null;
    }



    protected View createView(ParserContext context, ViewGroup parent, LayoutHandler<View> handler, JsonObject object)
    {
        View view = handler.createView(context, activity, parent, object);
        return view;
    }




    public LayoutBuilderCallback getListener() {
        return listener;
    }

    public void setListener(LayoutBuilderCallback listener) {
        this.listener = listener;
    }
}
