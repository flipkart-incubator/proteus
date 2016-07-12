package com.flipkart.proteus.parser;

import android.view.View;

import com.flipkart.proteus.view.ProteusView;
import com.google.gson.JsonElement;

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
    protected void prepareHandlers() {
        if (wrappedParser != null) {
            wrappedParser.prepareHandlers();
        }
    }

    @Override
    public boolean handleAttribute(V view, String attribute, JsonElement value) {
        boolean handled = super.handleAttribute(view, attribute, value);
        if (wrappedParser != null && !handled) {
            handled = wrappedParser.handleAttribute(view, attribute, value);
        }
        return handled;
    }

    @Override
    public boolean handleChildren(ProteusView view) {
        boolean handled = super.handleChildren(view);
        if (wrappedParser != null && !handled) {
            handled = wrappedParser.handleChildren(view);
        }
        return handled;
    }

    @Override
    public boolean addView(ProteusView parent, ProteusView view) {
        boolean handled = super.addView(parent, view);
        if (wrappedParser != null && !handled) {
            handled = wrappedParser.addView(parent, view);
        }
        return handled;
    }
}
