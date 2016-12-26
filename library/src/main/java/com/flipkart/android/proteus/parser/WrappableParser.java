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

import com.flipkart.android.proteus.LayoutParser;
import com.flipkart.android.proteus.builder.ProteusLayoutInflater;
import com.flipkart.android.proteus.view.ProteusView;

/**
 * @author kirankumar
 */
public abstract class WrappableParser<V extends View> extends BaseTypeParser<V> {

    private final BaseTypeParser<V> wrappedParser;

    public WrappableParser(BaseTypeParser<V> wrappedParser) {
        this.wrappedParser = wrappedParser;
    }

    @Override
    public void prepare() {
        if (wrappedParser != null) {
            wrappedParser.prepare();
            offset = wrappedParser.getOffset() - wrappedParser.getAttributeProcessorCount();
        }
        super.prepare();
    }

    @Override
    public boolean handleAttribute(V view, String attribute, LayoutParser parser) {
        boolean handled = super.handleAttribute(view, attribute, parser);
        if (!handled && wrappedParser != null) {
            handled = wrappedParser.handleAttribute(view, attribute, parser);
        }
        return handled;
    }

    @Override
    public boolean minify(ProteusLayoutInflater layoutInflater, LayoutParser out, String attribute, LayoutParser value) {
        boolean handled = super.minify(layoutInflater, out, attribute, value);
        if (!handled && null != wrappedParser) {
            handled = wrappedParser.minify(layoutInflater, out, attribute, value);
        }
        return handled;
    }

    @Override
    public boolean handleChildren(ProteusView view, LayoutParser children) {
        boolean handled = super.handleChildren(view, children);
        if (wrappedParser != null && !handled) {
            handled = wrappedParser.handleChildren(view, children);
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
