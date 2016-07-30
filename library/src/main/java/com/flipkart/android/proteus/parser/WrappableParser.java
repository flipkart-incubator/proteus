/*
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.android.proteus.parser;

import android.view.View;

import com.flipkart.android.proteus.view.ProteusView;
import com.google.gson.JsonElement;

/**
 * @author kirankumar
 */
public abstract class WrappableParser<V extends View> extends Parser<V> {

    private final Parser<V> wrappedParser;

    public WrappableParser(Parser<V> wrappedParser) {
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
