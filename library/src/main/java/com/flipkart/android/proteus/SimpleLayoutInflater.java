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
import android.view.ViewGroup;

import com.flipkart.android.proteus.manager.ProteusViewManager;
import com.flipkart.android.proteus.toolbox.IdGenerator;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.Value;
import com.google.gson.JsonObject;

import java.util.Iterator;

/**
 * A layout builder which can parse json to construct an android view out of it. It uses the
 * registered parsers to convert the json string to a view and then assign attributes.
 */
public class SimpleLayoutInflater implements ProteusLayoutInflater {

    private static final String TAG = "SimpleLayoutInflater";

    @NonNull
    protected final ProteusContext context;

    @NonNull
    final IdGenerator idGenerator;

    SimpleLayoutInflater(@NonNull ProteusContext context, IdGenerator idGenerator) {
        this.context = context;
        this.idGenerator = idGenerator;
    }

    @Override
    @Nullable
    public ViewTypeParser getParser(String type) {
        return context.getParser(type);
    }

    @Override
    public ProteusView inflate(Layout layout, JsonObject data, ViewGroup parent, int dataIndex) {
        ViewTypeParser parser = getParser(layout.type);
        if (parser == null) {
            return onUnknownViewEncountered(context, layout.type, layout, data, dataIndex);
        }

        /**
         * View creation.
         */
        final ProteusView view;

        view = createView(parser, layout, data, parent, dataIndex);
        onAfterCreateView(parser, view, parent, dataIndex);

        ProteusViewManager viewManager = createViewManager(parser, view, layout, data, parent, dataIndex);
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

    @Override
    public ProteusView inflate(Layout layout, JsonObject data, int dataIndex) {
        return inflate(layout, data, null, dataIndex);
    }

    @Override
    public ProteusView inflate(Layout layout, JsonObject data) {
        return inflate(layout, data, null, -1);
    }

    protected ProteusView createView(@NonNull ViewTypeParser parser, @NonNull Layout layout,
                                     @NonNull JsonObject data, @Nullable ViewGroup parent, int dataIndex) {
        return parser.createView(context, layout, data, parent, dataIndex);
    }

    protected void onAfterCreateView(@NonNull ViewTypeParser parser, @NonNull ProteusView view,
                                     @Nullable ViewGroup parent, int index) {
        parser.onAfterCreateView(view, parent, index);
    }

    protected ProteusViewManager createViewManager(ViewTypeParser parser, ProteusView view, Layout layout, JsonObject data, ViewGroup parent, int dataIndex) {
        return parser.createViewManager(context, view, layout, data, parent, dataIndex);
    }

    public boolean handleAttribute(ViewTypeParser parser, ProteusView view, int attribute, Value value) {
        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "Handle '" + attribute + "' : " + value);
        }
        //noinspection unchecked
        return parser.handleAttribute(view.getAsView(), attribute, value);
    }

    @Nullable
    protected ProteusView onUnknownViewEncountered(ProteusContext context, String type, Layout layout, JsonObject data, int dataIndex) {
        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "No ViewTypeParser for: " + type);
        }
        if (context.getCallback() != null) {
            return context.getCallback().onUnknownViewType(context, type, layout, data, dataIndex);
        }
        return null;
    }

    @Override
    public int getUniqueViewId(String id) {
        return idGenerator.getUnique(id);
    }

    @NonNull
    @Override
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }
}
