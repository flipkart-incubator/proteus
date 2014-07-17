package com.flipkart.layoutengine.parser;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by kirankumar on 16/07/14.
 */
public class WrappableParser<T extends View> extends Parser<T> {

    private final Parser<T> wrappedParser;

    public WrappableParser(Class viewClass, Parser<T> wrappedParser) {
        super(viewClass);
        this.wrappedParser = wrappedParser;
    }

    @Override
    protected void prepareHandlers(Activity activity) {
        if(wrappedParser!=null) {
            wrappedParser.prepareHandlers(activity);
        }
    }

    @Override
    public boolean handleAttribute(ParserContext context, String attribute, JsonElement element, T view) {
        boolean handled = super.handleAttribute(context, attribute, element, view);
        if(wrappedParser!=null && !handled)
        {
            handled = handled || wrappedParser.handleAttribute(context,attribute,element,view);
        }

        return handled;

    }
}
