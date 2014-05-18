package com.flipkart.layoutengine.builder;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.parser.ViewParser;
import com.flipkart.layoutengine.parser.custom.FrameLayoutParser;
import com.flipkart.layoutengine.parser.custom.HorizontalScrollViewParser;
import com.flipkart.layoutengine.parser.custom.ImageViewParser;
import com.flipkart.layoutengine.parser.custom.LinearLayoutParser;
import com.flipkart.layoutengine.parser.custom.ScrollViewParser;
import com.flipkart.layoutengine.parser.custom.TextViewParser;
import com.flipkart.layoutengine.parser.custom.ViewPagerParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by kiran.kumar on 09/05/14.
 */
public class LayoutBuilder {

    private static HashMap<String,LayoutHandler> viewClassMap = new HashMap<String, LayoutHandler>();
    private LayoutBuilderCallback listener;

    static {
        viewClassMap.put("container.linear",new LinearLayoutParser());
        viewClassMap.put("container.absolute",new FrameLayoutParser());
        viewClassMap.put("container.verticalscroll",new ScrollViewParser());
        viewClassMap.put("container.horizontalscroll",new HorizontalScrollViewParser());
        viewClassMap.put("image",new ImageViewParser());
        viewClassMap.put("text", new TextViewParser());
        viewClassMap.put("pager",new ViewPagerParser());
        viewClassMap.put("view",new ViewParser(View.class));
    }

    private Activity activity;

    public LayoutBuilder(Activity activity) {
        this.activity = activity;
    }



    public View build(ViewGroup parent, JsonObject jsonObject)
    {
        String viewType = jsonObject.get("view").getAsString();
        jsonObject.remove("view");

        JsonArray children = jsonObject.getAsJsonArray("children");
        jsonObject.remove("children");

        LayoutHandler<View> handler = viewClassMap.get(viewType);
        if(handler == null)
        {
            if(listener!=null)
            {
                listener.onUnknownViewType(viewType,jsonObject,parent);
                return null;
            }
        }
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
                View childView = build(selfViewGroup,childObject);
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
