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

import com.flipkart.android.proteus.LayoutParser;
import com.flipkart.android.proteus.parser.TypeParser;
import com.flipkart.android.proteus.toolbox.BitmapLoader;
import com.flipkart.android.proteus.toolbox.DataContext;
import com.flipkart.android.proteus.toolbox.IdGenerator;
import com.flipkart.android.proteus.toolbox.LayoutBuilderCallback;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.toolbox.Utils;
import com.flipkart.android.proteus.view.ProteusView;
import com.flipkart.android.proteus.view.manager.ProteusViewManager;
import com.flipkart.android.proteus.view.manager.ProteusViewManagerImpl;
import com.google.gson.JsonObject;

import java.util.HashMap;

/**
 * A layout builder which can parse json to construct an android view out of it. It uses the
 * registered handlers to convert the json string to a view and then assign attributes.
 */
public class SimpleLayoutInflater implements ProteusLayoutInflater {

    private static final String TAG = "SimpleLayoutInflater";

    @Nullable
    protected LayoutBuilderCallback listener;
    private HashMap<String, TypeParser> layoutHandlers = new HashMap<>();
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
        layoutHandlers.put(type, parser);
    }

    @Override
    public void unregisterParser(String type) {
        layoutHandlers.remove(type);
    }

    @Override
    @Nullable
    public TypeParser getParser(String type) {
        return layoutHandlers.get(type);
    }

    @Override
    @Nullable
    public ProteusView build(ViewGroup parent, LayoutParser parser, JsonObject data, Styles styles, int index) {

        if (!parser.isLayout()) {
            throw new IllegalArgumentException("parser did not return a layout: " + parser.toString());
        }

        String type = parser.getType();

        TypeParser handler = layoutHandlers.get(type);
        if (handler == null) {
            return onUnknownViewEncountered(type, parent, parser, data, index, styles);
        }

        /**
         * View creation.
         */
        final ProteusView view;

        onBeforeCreateView(handler, parent, parser, data, index, styles);
        view = createView(handler, parent, parser, data, index, styles);
        onAfterCreateView(handler, view, parent, parser, data, styles, index);

        ProteusViewManager viewManager = createViewManager(handler, parent, parser, data, styles, index);
        viewManager.setView((View) view);
        view.setViewManager(viewManager);

        /**
         * Parsing each attribute and setting it on the view.
         */
        while (parser.hasNext()) {
            parser.next();
            boolean handled = handleAttribute(handler, view, parser.getName(), parser);

            if (!handled) {
                onUnknownAttributeEncountered(view, parser.getName(), parser);
            }
        }

        return view;
    }

    @Override
    public LayoutParser minify(LayoutParser in) {
        if (!in.isLayout()) {
            throw new IllegalArgumentException("parser did not return a layout: " + in.toString());
        }

        String type = in.getType();
        TypeParser handler = layoutHandlers.get(type);

        if (null == handler) {
            return in;
        }

        LayoutParser out = in.create();
        out.setType(type);
        String name;
        boolean handled;

        while (in.hasNext()) {
            in.next();
            name = in.getName();
            handled = minifyAttribute(handler, out, name, in.getAttribute(name));
            if (!handled) {
                out.addAttribute(name, in.getAttribute(name));
            }
        }

        out.reset();
        return out;
    }

    protected void onBeforeCreateView(TypeParser handler, ViewGroup parent, LayoutParser parser, JsonObject data, int index, Styles styles) {
        handler.onBeforeCreateView(parent, parser, data, styles, index);
    }

    protected ProteusView createView(TypeParser handler, ViewGroup parent, LayoutParser parser, JsonObject data, int index, Styles styles) {
        return handler.createView(parent, parser, data, styles, index);
    }

    protected void onAfterCreateView(TypeParser handler, ProteusView view, ViewGroup parent, LayoutParser parser, JsonObject data, Styles styles, int index) {
        //noinspection unchecked
        handler.onAfterCreateView(parent, (View) view, parser, data, styles, index);
    }

    protected ProteusViewManager createViewManager(TypeParser handler, View parent, LayoutParser parser, JsonObject data, Styles styles, int index) {
        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "ProteusView created with " + Utils.getLayoutIdentifier(parser));
        }

        ProteusViewManagerImpl viewManager = new ProteusViewManagerImpl();

        DataContext dataContext = new DataContext();
        dataContext.setData(data);
        dataContext.setIndex(index);

        viewManager.setDataContext(dataContext);
        viewManager.setLayoutParser(parser.clone());
        viewManager.setStyles(styles);
        viewManager.setProteusLayoutInflater(this);
        viewManager.setTypeParser(handler);

        return viewManager;
    }

    public boolean handleAttribute(TypeParser handler, ProteusView view, String attribute, LayoutParser parser) {
        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "Handle '" + attribute + "' : " + parser.toString() + " for view with " + Utils.getLayoutIdentifier(parser));
        }
        //noinspection unchecked
        return handler.handleAttribute((View) view, attribute, parser);
    }

    @Override
    public boolean minifyAttribute(TypeParser handler, LayoutParser out, String attribute, LayoutParser value) {
        return handler.minify(this, out, attribute, value);
    }

    protected void onUnknownAttributeEncountered(ProteusView view, String attribute, LayoutParser parser) {
        if (listener != null) {
            listener.onUnknownAttribute(view, attribute, parser);
        }
    }

    @Nullable
    protected ProteusView onUnknownViewEncountered(String type, ViewGroup parent, LayoutParser layout, JsonObject data, int index, Styles styles) {
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

    @Nullable
    @Override
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    @Nullable
    @Override
    public LayoutBuilderCallback getListener() {
        return listener;
    }

    @Override
    public void setListener(@Nullable LayoutBuilderCallback listener) {
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
