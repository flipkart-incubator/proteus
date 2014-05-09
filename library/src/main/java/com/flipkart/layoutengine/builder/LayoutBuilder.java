package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.parser.LinearLayoutParser;
import com.flipkart.layoutengine.parser.TextViewParser;
import com.flipkart.layoutengine.parser.ViewParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;


/**
 * Created by kiran.kumar on 09/05/14.
 */
public class LayoutBuilder {

    private static HashMap<String,LayoutHandler> viewClassMap = new HashMap<String, LayoutHandler>();

    static {
        viewClassMap.put("linear",new LinearLayoutParser());
        viewClassMap.put("text", new TextViewParser());
    }

    private Context context;

    public LayoutBuilder(Context context) {
        this.context = context;
    }



    public View build(JsonObject jsonObject)
    {
        View self = createView(jsonObject);
        JsonArray children = jsonObject.getAsJsonArray("children");
        if(children!=null && children.size()>0) {
            ViewGroup selfViewGroup = (ViewGroup) self;
            for (int i = 0; i < children.size(); i++) {

                JsonObject child = children.get(i).getAsJsonObject();
                View childView = build(child);
                selfViewGroup.addView(childView);

            }
        }
        return self;


    }

    public View createView(JsonObject object)
    {
        String viewType = object.get("view").getAsString();
        LayoutHandler<View> handler = viewClassMap.get(viewType);
        View view = handler.parse(context,object);
        return view;
    }


}
