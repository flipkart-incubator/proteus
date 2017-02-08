/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 *
 * Copyright (c) 2017 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.flipkart.android.proteus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.manager.ProteusViewManager;
import com.flipkart.android.proteus.manager.ProteusViewManagerImpl;
import com.flipkart.android.proteus.toolbox.BitmapLoader;
import com.flipkart.android.proteus.toolbox.Formatter;
import com.flipkart.android.proteus.toolbox.IdGenerator;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Scope;
import com.flipkart.android.proteus.toolbox.Styles;
import com.google.gson.JsonObject;

import java.util.Iterator;
import java.util.Map;

/**
 * A layout builder which can parse json to construct an android view out of it. It uses the
 * registered parsers to convert the json string to a view and then assign attributes.
 */
public class SimpleLayoutInflater implements ProteusLayoutInflater {

    private static final String TAG = "SimpleLayoutInflater";

    protected final Map<String, ViewTypeParser> parsers;
    protected final Map<String, Formatter> formatter;

    @Nullable
    protected Callback callback;

    @Nullable
    private BitmapLoader bitmapLoader;

    private IdGenerator idGenerator;

    protected SimpleLayoutInflater(Map<String, ViewTypeParser> parsers, Map<String, Formatter> formatter, @NonNull IdGenerator idGenerator) {
        this.parsers = parsers;
        this.formatter = formatter;
        this.idGenerator = idGenerator;
    }

    @Override
    @Nullable
    public ViewTypeParser getParser(String type) {
        return parsers.get(type);
    }

    @Override
    @Nullable
    public ProteusView inflate(ViewGroup parent, Layout layout, JsonObject data, Styles styles, int index) {
        ViewTypeParser parser = parsers.get(layout.type);
        if (parser == null) {
            return onUnknownViewEncountered(layout.type, parent, layout, data, styles, index);
        }

        /**
         * View creation.
         */
        final ProteusView view;

        view = createView(parser, parent, layout, data, styles, index);
        onAfterCreateView(parser, view, parent, layout, data, styles, index);

        ProteusViewManager viewManager = createViewManager(parser, parent, layout, data, styles, index);
        viewManager.setView(view.getAsView());
        view.setViewManager(viewManager);

        /**
         * Parsing each attribute and setting it on the view.
         */
        if (layout.attributes != null) {
            Iterator<Layout.Attribute> iterator = layout.attributes.iterator();
            Layout.Attribute attribute;
            while (iterator.hasNext()) {
                attribute = iterator.next();
                handleAttribute(parser, view, attribute.id, attribute.value);
            }
        }

        return view;
    }

    protected ProteusView createView(ViewTypeParser parser, ViewGroup parent, Layout layout, JsonObject data, Styles styles, int index) {
        return parser.createView(this, parent, layout, data, styles, index);
    }

    protected void onAfterCreateView(ViewTypeParser parser, ProteusView view, ViewGroup parent, Layout layout, JsonObject data, Styles styles, int index) {
        //noinspection unchecked
        parser.onAfterCreateView(this, parent, view, layout, data, styles, index);
    }

    protected ProteusViewManager createViewManager(ViewTypeParser parser, View parent, Layout layout, JsonObject data, Styles styles, int index) {
        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "ProteusView created with " + layout);
        }

        ProteusViewManagerImpl viewManager = new ProteusViewManagerImpl();

        Scope scope = new Scope();
        scope.setData(data);
        scope.setIndex(index);

        viewManager.setScope(scope);
        viewManager.setLayout(layout);
        viewManager.setStyles(styles);
        viewManager.setProteusLayoutInflater(this);
        viewManager.setTypeParser(parser);

        return viewManager;
    }

    public boolean handleAttribute(ViewTypeParser parser, ProteusView view, int attribute, Value value) {
        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "Handle '" + attribute + "' : " + value);
        }
        //noinspection unchecked
        return parser.handleAttribute(view.getAsView(), attribute, value);
    }

    @Nullable
    protected ProteusView onUnknownViewEncountered(String type, ViewGroup parent, Layout layout, JsonObject data, Styles styles, int index) {
        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "No ViewTypeParser for: " + type);
        }
        if (callback != null) {
            return callback.onUnknownViewType(type, parent, layout, data, styles, index);
        }
        return null;
    }

    @Override
    @Nullable
    public Layout onIncludeLayout(String type, Layout include) {
        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "No ViewTypeParser for: " + type);
        }
        if (callback != null) {
            return callback.onLayoutRequired(type, include);
        }
        return null;
    }

    @Override
    public int getUniqueViewId(String id) {
        return idGenerator.getUnique(id);
    }

    @Nullable
    @Override
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    @Nullable
    @Override
    public Callback getCallback() {
        return callback;
    }

    @Override
    public void setCallback(@Nullable Callback listener) {
        this.callback = listener;
    }

    public Formatter getFormatter(String name) {
        return this.formatter.get(name);
    }

    @Override
    public BitmapLoader getBitmapLoader() {
        return bitmapLoader;
    }

    @Override
    public void setBitmapLoader(@Nullable BitmapLoader bitmapLoader) {
        this.bitmapLoader = bitmapLoader;
    }

}
