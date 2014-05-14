package com.flipkart.layoutengine.builder;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.parser.custom.FrameLayoutParser;
import com.flipkart.layoutengine.parser.custom.ImageViewParser;
import com.flipkart.layoutengine.parser.custom.LinearLayoutParser;
import com.flipkart.layoutengine.parser.custom.TextViewParser;
import com.flipkart.layoutengine.parser.custom.ViewPagerParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by kiran.kumar on 09/05/14.
 */
public class LayoutBuilder {

    private static HashMap<String,LayoutHandler> viewClassMap = new HashMap<String, LayoutHandler>();

    static {
        viewClassMap.put("container.linear",new LinearLayoutParser());
        viewClassMap.put("container.absolute",new FrameLayoutParser());
        viewClassMap.put("image",new ImageViewParser());
        viewClassMap.put("text", new TextViewParser());
        viewClassMap.put("pager",new ViewPagerParser());
    }

    private Activity activity;

    public LayoutBuilder(Activity activity) {
        this.activity = activity;
    }



    public View build(ViewGroup parent, JsonObject jsonObject)
    {
        String viewType = jsonObject.get("view").getAsString();
        LayoutHandler<View> handler = viewClassMap.get(viewType);
        handler.prepare(activity);
        View self = createView(parent, handler, jsonObject);
        JsonArray children = jsonObject.getAsJsonArray("children");
        if(children!=null && children.size()>0) {
            ViewGroup selfViewGroup = (ViewGroup) self;
            List<View> childrenToAdd = new ArrayList<View>();
            for (int i = 0; i < children.size(); i++) {

                JsonObject childObject = children.get(i).getAsJsonObject();
                View childView = build(selfViewGroup,childObject);
                childrenToAdd.add(childView);

            }
            handler.addChildren(activity,selfViewGroup,childrenToAdd);
        }
        return self;


    }

    public View createView(ViewGroup parent, LayoutHandler<View> handler, JsonObject object)
    {
        View view = handler.parse(activity, parent, object);
        return view;
    }


}
