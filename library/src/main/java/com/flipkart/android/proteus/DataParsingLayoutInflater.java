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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.flipkart.android.proteus.manager.ProteusViewManager;
import com.flipkart.android.proteus.manager.ProteusViewManagerImpl;
import com.flipkart.android.proteus.toolbox.Binding;
import com.flipkart.android.proteus.toolbox.Formatter;
import com.flipkart.android.proteus.toolbox.IdGenerator;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Result;
import com.flipkart.android.proteus.toolbox.Scope;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.toolbox.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Map;
import java.util.regex.Matcher;

/**
 * A layout builder which can parse data bindings before passing it on to {@link SimpleLayoutInflater}
 */
public class DataParsingLayoutInflater extends SimpleLayoutInflater {

    private static final String TAG = "ProteusLayoutInflater";

    protected DataParsingLayoutInflater(Map<String, ViewTypeParser> parsers, Map<String, Formatter> formatter, @NonNull IdGenerator idGenerator) {
        super(parsers, formatter, idGenerator);
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
        viewManager.setProteusLayoutInflater(this);
        viewManager.setInflaterCallback(callback);
        viewManager.setImageLoader(loader);
        viewManager.setTypeParser(parser);

        return viewManager;
    }

    @Override
    public boolean handleAttribute(ViewTypeParser parser, ProteusView view, int attribute, Value value) {
        String dataPath = isDataPath(value);
        if (dataPath != null) {
            value = this.findAndReturnValue(view, parser, value, dataPath, attribute);
        }
        return super.handleAttribute(parser, view, attribute, value);
    }

    private Value findAndReturnValue(ProteusView view, ViewTypeParser handler, Value value, String dataPath, int attribute) {

        ProteusViewManager viewManager = view.getViewManager();

        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "Find '" + dataPath + "' for " + attribute + " for view with " + viewManager.getLayout());
        }

        char firstChar = TextUtils.isEmpty(dataPath) ? 0 : dataPath.charAt(0);
        Result result;
        switch (firstChar) {
            case ProteusConstants.DATA_PREFIX:
                JsonElement elementFromData;
                dataPath = dataPath.substring(1);
                result = Utils.readJson(dataPath, viewManager.getScope().getData(), viewManager.getScope().getIndex());

                if (result.isSuccess()) {
                    elementFromData = result.element;
                } else {
                    elementFromData = new JsonPrimitive(ProteusConstants.DATA_NULL);
                }

                if (elementFromData != null) {
                    value = value.create(elementFromData);
                }
                addBinding(viewManager, dataPath, attribute, dataPath, false);
                break;
            case ProteusConstants.REGEX_PREFIX:
                Matcher regexMatcher = ProteusConstants.REGEX_PATTERN.matcher(dataPath);
                String finalValue = dataPath;
                while (regexMatcher.find()) {
                    String matchedString = regexMatcher.group(0);
                    String bindingName;
                    if (regexMatcher.group(3) != null) {

                        // has NO formatter
                        dataPath = regexMatcher.group(3);
                        result = Utils.readJson(dataPath, viewManager.getScope().getData(), viewManager.getScope().getIndex());
                        if (result.isSuccess() && null != result.element) {
                            finalValue = finalValue.replace(matchedString, result.element.getAsString());
                        } else {
                            finalValue = dataPath;
                        }
                        bindingName = dataPath;
                    } else {

                        // has formatter
                        dataPath = regexMatcher.group(1);
                        String formatterName = regexMatcher.group(2);

                        String formattedValue;
                        result = Utils.readJson(dataPath, viewManager.getScope().getData(), viewManager.getScope().getIndex());
                        if (result.isSuccess() && null != result.element) {
                            formattedValue = format(result.element, formatterName);
                        } else {
                            formattedValue = dataPath;
                        }

                        finalValue = finalValue.replace(matchedString, formattedValue != null ? formattedValue : "");
                        bindingName = dataPath;
                    }
                    addBinding(viewManager, bindingName, attribute, dataPath, true);
                }

                // remove the REGEX_PREFIX
                finalValue = finalValue.substring(1);

                // return as a Value
                value = value.create(finalValue);

                break;
        }

        return value;
    }

    @Nullable
    public String isDataPath(Value value) {
        if (!value.isPrimitive()) {
            return null;
        }
        String attributeValue = value.getAsString();
        if (attributeValue != null && !"".equals(attributeValue) && (attributeValue.charAt(0) == ProteusConstants.DATA_PREFIX || attributeValue.charAt(0) == ProteusConstants.REGEX_PREFIX)) {
            return attributeValue;
        }
        return null;
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
