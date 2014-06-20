package com.flipkart.layoutengine.parser.data;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.builder.LayoutHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Adds support for parsing data blocks before passing it to the enclosing handler.
 */
public class DataParsingAdapter<E> implements LayoutHandler<E> {

    private LayoutHandler<E> handler;

    public DataParsingAdapter(LayoutHandler<E> enclosingHandler) {
        this.handler = enclosingHandler;
    }

    @Override
    public E createView(Activity activity, ViewGroup parent, JsonObject object) {
        return handler.createView(activity,parent,object);
    }

    @Override
    public boolean handleAttribute(String attribute, JsonElement element, E view) {
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
