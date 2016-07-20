package com.flipkart.layoutengine.parser;

import android.content.Context;
import android.view.View;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author kirankumar
 */
public class WrappableParser<V extends View> extends Parser<V> {

    private final Parser<V> wrappedParser;

    public WrappableParser(Class viewClass, Parser<V> wrappedParser) {
        //noinspection unchecked
        super(viewClass);
        this.wrappedParser = wrappedParser;
    }

    @Override
    public void prepare(Context context) {
        if (wrappedParser != null) {
            wrappedParser.prepare(context);
        }
        super.prepare(context);
    }

    @Override
    public boolean handleAttribute(ParserContext context, String attribute, JsonElement element,
                                   JsonObject layout, V view, ProteusView proteusView, ProteusView parent, int childIndex) {

        boolean handled = super.handleAttribute(context, attribute, element, layout, view, proteusView, parent, childIndex);
        if (wrappedParser != null && !handled) {
            handled = wrappedParser.handleAttribute(context, attribute, element, layout, view, proteusView, parent, childIndex);
        }
        return handled;
    }
}
