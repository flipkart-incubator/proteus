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

package com.flipkart.android.proteus.builder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.Attribute;
import com.flipkart.android.proteus.Layout;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.parser.TypeParser;
import com.flipkart.android.proteus.toolbox.BitmapLoader;
import com.flipkart.android.proteus.toolbox.IdGenerator;
import com.flipkart.android.proteus.toolbox.LayoutInflaterCallback;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Scope;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.view.ProteusView;
import com.flipkart.android.proteus.view.manager.ProteusViewManager;
import com.flipkart.android.proteus.view.manager.ProteusViewManagerImpl;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * A layout builder which can parse json to construct an android view out of it. It uses the
 * registered parsers to convert the json string to a view and then assign attributes.
 */
public class SimpleLayoutInflater implements ProteusLayoutInflater {

    private static final String TAG = "SimpleLayoutInflater";

    @Nullable
    protected LayoutInflaterCallback listener;
    private HashMap<String, TypeParser> parsers = new HashMap<>();
    @Nullable
    private BitmapLoader bitmapLoader;

    private boolean isSynchronousRendering = false;

    private IdGenerator idGenerator;

    protected SimpleLayoutInflater(@NonNull IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public void registerParser(String type, TypeParser parser) {
        parser.prepare();
        parsers.put(type, parser);
    }

    @Override
    public void unregisterParser(String type) {
        parsers.remove(type);
    }

    @Override
    @Nullable
    public TypeParser getParser(String type) {
        return parsers.get(type);
    }

    @Override
    @Nullable
    public ProteusView build(ViewGroup parent, Layout layout, JsonObject data, Styles styles, int index) {

        TypeParser parser = parsers.get(layout.type);
        if (parser == null) {
            return onUnknownViewEncountered(layout.type, parent, layout, data, styles, index);
        }

        /**
         * View creation.
         */
        final ProteusView view;

        onBeforeCreateView(parser, parent, layout, data, styles, index);
        view = createView(parser, parent, layout, data, styles, index);
        onAfterCreateView(parser, view, parent, layout, data, styles, index);

        ProteusViewManager viewManager = createViewManager(parser, parent, layout, data, styles, index);
        viewManager.setView((View) view);
        view.setViewManager(viewManager);

        /**
         * Parsing each attribute and setting it on the view.
         */
        if (layout.attributes != null) {
            Iterator<Attribute> iterator = layout.attributes.iterator();
            Attribute attribute;
            while (iterator.hasNext()) {
                attribute = iterator.next();
                boolean handled = handleAttribute(parser, view, attribute.id, attribute.value);
                if (!handled) {
                    onUnknownAttributeEncountered(view, attribute.id, attribute.value);
                }
            }
        }

        return view;
    }

    protected void onBeforeCreateView(TypeParser parser, ViewGroup parent, Layout layout, JsonObject data, Styles styles, int index) {
        parser.onBeforeCreateView(parent, layout, data, styles, index);
    }

    protected ProteusView createView(TypeParser parser, ViewGroup parent, Layout layout, JsonObject data, Styles styles, int index) {
        return parser.createView(parent, layout, data, styles, index);
    }

    protected void onAfterCreateView(TypeParser parser, ProteusView view, ViewGroup parent, Layout layout, JsonObject data, Styles styles, int index) {
        //noinspection unchecked
        parser.onAfterCreateView(parent, (View) view, layout, data, styles, index);
    }

    protected ProteusViewManager createViewManager(TypeParser parser, View parent, Layout layout, JsonObject data, Styles styles, int index) {
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

    public boolean handleAttribute(TypeParser parser, ProteusView view, int attribute, Value value) {
        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "Handle '" + attribute + "' : " + value);
        }
        //noinspection unchecked
        return parser.handleAttribute((View) view, attribute, value);
    }

    protected void onUnknownAttributeEncountered(ProteusView view, int attribute, Value value) {
        if (listener != null) {
            listener.onUnknownAttribute(view, attribute, value);
        }
    }

    @Nullable
    protected ProteusView onUnknownViewEncountered(String type, ViewGroup parent, Layout layout, JsonObject data, Styles styles, int index) {
        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "No TypeParser for: " + type);
        }
        if (listener != null) {
            return listener.onUnknownViewType(type, parent, layout, data, styles, index);
        }
        return null;
    }

    @Override
    public int getUniqueViewId(String id) {
        return idGenerator.getUnique(id);
    }

    @Override
    public int getAttributeId(String attribute, String type) {
        TypeParser parser = parsers.get(type);
        if (null == parser) {
            return -1;
        }
        return parser.getAttributeId(attribute);
    }

    @Nullable
    @Override
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    @Nullable
    @Override
    public LayoutInflaterCallback getListener() {
        return listener;
    }

    @Override
    public void setListener(@Nullable LayoutInflaterCallback listener) {
        this.listener = listener;
    }

    @Override
    public BitmapLoader getNetworkDrawableHelper() {
        return bitmapLoader;
    }

    @Override
    public void setBitmapLoader(@Nullable BitmapLoader bitmapLoader) {
        this.bitmapLoader = bitmapLoader;
    }

    @Override
    public boolean isSynchronousRendering() {
        return isSynchronousRendering;
    }

    @Override
    public void setSynchronousRendering(boolean isSynchronousRendering) {
        this.isSynchronousRendering = isSynchronousRendering;
    }
}
