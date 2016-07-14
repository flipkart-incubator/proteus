/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Flipkart Internet Pvt. Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.flipkart.android.proteus.parser;

import android.view.View;

import com.flipkart.android.proteus.view.ProteusView;
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
