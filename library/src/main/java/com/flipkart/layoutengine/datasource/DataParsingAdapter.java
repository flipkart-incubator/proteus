package com.flipkart.layoutengine.datasource;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.builder.LayoutHandler;
import com.flipkart.layoutengine.datasource.DataSource;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.List;

/**
 * Adds support for parsing data blocks before passing it to the enclosing handler.
 */
public class DataParsingAdapter<E> implements LayoutHandler<E> {

    private final DataSource dataSource;
    private LayoutHandler<E> handler;
    private static final String PREFIX = "@";

    public DataParsingAdapter(DataSource dataSource, LayoutHandler<E> enclosingHandler) {
        this.dataSource = dataSource;
        this.handler = enclosingHandler;
    }

    @Override
    public E createView(Activity activity, ViewGroup parent, JsonObject object) {
        return handler.createView(activity,parent,object);
    }

    @Override
    public boolean handleAttribute(String attribute, JsonElement element, E view) {
        if(element.isJsonPrimitive())
        {
            String dataSourceKey = element.getAsString();
            if(dataSourceKey.startsWith(PREFIX)) {
               String data = dataSource.getString(dataSourceKey.substring(PREFIX.length()));
               element = new JsonPrimitive(data);
            }
        }

        return handler.handleAttribute(attribute,element,view);
    }

    @Override
    public boolean canAddChild() {
        return handler.canAddChild();
    }

    @Override
    public void setupView(ViewGroup parent, E view) {
        handler.setupView(parent,view);
    }

    @Override
    public void prepare(Activity activity) {
        handler.prepare(activity);
    }

    @Override
    public void addChildren(Activity activity, E parent, List<View> children) {
        handler.addChildren(activity,parent,children);
    }


}
