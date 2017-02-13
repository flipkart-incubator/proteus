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
import android.view.View;

import com.flipkart.android.proteus.manager.ProteusViewManager;
import com.flipkart.android.proteus.manager.ProteusViewManagerImpl;
import com.flipkart.android.proteus.toolbox.Binding;
import com.flipkart.android.proteus.toolbox.Formatter;
import com.flipkart.android.proteus.toolbox.IdGenerator;
import com.flipkart.android.proteus.toolbox.Scope;
import com.flipkart.android.proteus.toolbox.Styles;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * A layout builder which can parse data bindings before passing it on to {@link SimpleLayoutInflater}
 */
public class DataParsingLayoutInflater extends SimpleLayoutInflater {

    private static final String TAG = "ProteusLayoutInflater";

    protected DataParsingLayoutInflater(@NonNull Map<String, ViewTypeParser> parsers, @NonNull Map<String, Formatter> formatter,
                                        @NonNull IdGenerator idGenerator, @Nullable ImageLoader loader, @Nullable Callback callback) {
        super(parsers, formatter, idGenerator, loader, callback);
    }

    @Override
    protected ProteusViewManager createViewManager(ViewTypeParser parser, View parent, Layout layout, JsonObject data, Styles styles, Callback callback, ImageLoader loader, int index) {
        ProteusViewManagerImpl viewManager = new ProteusViewManagerImpl();
        Scope scope, parentScope = null;
        Map<String, String> map = layout.scope;

        if (parent instanceof ProteusView) {
            parentScope = ((ProteusView) parent).getViewManager().getScope();
        }

        if (map == null) {
            if (parentScope != null) {
                scope = new Scope(parentScope);
            } else {
                scope = new Scope();
                scope.setData(data);
                scope.setIndex(index);
            }
        } else {
            if (parentScope != null) {
                scope = parentScope.createChildScope(map, index);
            } else {
                scope = new Scope();
                scope.setData(data);
                scope = scope.createChildScope(map, index);
            }
        }

        viewManager.setLayout(layout);
        viewManager.setScope(scope);
        viewManager.setStyles(styles);
        viewManager.setInflater(this);
        viewManager.setInflaterCallback(callback);
        viewManager.setImageLoader(loader);
        viewManager.setTypeParser(parser);

        return viewManager;
    }

    @Override
    public boolean handleAttribute(ViewTypeParser parser, ProteusView view, int attribute, Value value) {
        if (value.isBinding()) {
            addBinding(view.getViewManager(), "", attribute, "", true);
        }
        return super.handleAttribute(parser, view, attribute, value);
    }

    private void addBinding(ProteusViewManager viewManager, String bindingName, int attributeId, String attributeValue, boolean hasRegEx) {
        // check if the view is in update mode if not that means that the update flow
        // is running and we must not add more bindings for they will be duplicates
        if (!viewManager.isViewUpdating()) {
            Binding binding = new Binding(bindingName, attributeId, attributeValue, hasRegEx);
            viewManager.addBinding(binding);
        }
    }

    private String format(JsonElement toFormat, String formatterName) {
        Formatter formatter = this.formatter.get(formatterName);
        if (formatter == null) {
            formatter = Formatter.NOOP;
        }
        return formatter.format(toFormat);
    }
}
