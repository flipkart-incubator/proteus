/*
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.flipkart.android.proteus.DataContext;
import com.flipkart.android.proteus.binding.Binding;
import com.flipkart.android.proteus.parser.LayoutHandler;
import com.flipkart.android.proteus.toolbox.Formatter;
import com.flipkart.android.proteus.toolbox.IdGenerator;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Result;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.toolbox.Utils;
import com.flipkart.android.proteus.view.ProteusView;
import com.flipkart.android.proteus.view.manager.ProteusViewManager;
import com.flipkart.android.proteus.view.manager.ProteusViewManagerImpl;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * A layout builder which can parse data bindings before passing it on to {@link SimpleLayoutBuilder}
 */
public class DataParsingLayoutBuilder extends SimpleLayoutBuilder {

    private static final String TAG = "LayoutBuilder";
    private Map<String, Formatter> formatter = new HashMap<>();

    protected DataParsingLayoutBuilder(@NonNull IdGenerator idGenerator) {
        super(idGenerator);
    }

    @Override
    protected ProteusViewManager createViewManager(LayoutHandler handler, View parent, JsonObject layout, JsonObject data, int index, Styles styles) {
        ProteusViewManagerImpl viewManager = new ProteusViewManagerImpl();
        DataContext dataContext, parentDataContext = null;
        JsonElement scope = layout.get(ProteusConstants.DATA_CONTEXT);

        if (parent instanceof ProteusView) {
            parentDataContext = ((ProteusView) parent).getViewManager().getDataContext();
        }

        if (scope == null || scope.isJsonNull()) {
            if (parentDataContext != null) {
                dataContext = new DataContext(parentDataContext);
            } else {
                dataContext = new DataContext();
                dataContext.setData(data);
                dataContext.setIndex(index);
            }
        } else {
            if (parentDataContext != null) {
                dataContext = parentDataContext.createChildDataContext(scope.getAsJsonObject(), index);
            } else {
                dataContext = new DataContext();
                dataContext.setData(data);
                dataContext = dataContext.createChildDataContext(scope.getAsJsonObject(), index);
            }
        }

        viewManager.setLayout(layout);
        viewManager.setDataContext(dataContext);
        viewManager.setStyles(styles);
        viewManager.setLayoutBuilder(this);
        viewManager.setLayoutHandler(handler);

        return viewManager;
    }

    @Override
    public boolean handleAttribute(LayoutHandler handler, ProteusView view, String attribute, JsonElement value) {

        if (ProteusConstants.DATA_CONTEXT.equals(attribute)) {
            return true;
        }
        String stringValue = isDataPath(value);
        if (stringValue != null) {
            value = this.findAndReplaceValues(handler, view, attribute, stringValue, value);
        }
        return super.handleAttribute(handler, view, attribute, value);
    }

    private JsonElement findAndReplaceValues(LayoutHandler handler, ProteusView view, String attribute, String stringValue, JsonElement value) {
        ProteusViewManager viewManager = view.getViewManager();
        DataContext dataContext = viewManager.getDataContext();

        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "Find '" + stringValue + "' for " + attribute + " for view with " + Utils.getLayoutIdentifier(viewManager.getLayout()));
        }

        char firstChar = TextUtils.isEmpty(stringValue) ? 0 : stringValue.charAt(0);
        String dataPath;
        Result result;
        switch (firstChar) {
            case ProteusConstants.DATA_PREFIX:
                JsonElement elementFromData;
                dataPath = stringValue.substring(1);
                result = Utils.readJson(dataPath, viewManager.getDataContext().getData(), viewManager.getDataContext().getIndex());

                if (result.isSuccess()) {
                    elementFromData = result.element;
                } else {
                    elementFromData = new JsonPrimitive(ProteusConstants.DATA_NULL);
                }

                if (elementFromData != null) {
                    value = elementFromData;
                }
                addBinding(viewManager, dataPath, attribute, stringValue, false);
                break;
            case ProteusConstants.REGEX_PREFIX:
                Matcher regexMatcher = ProteusConstants.REGEX_PATTERN.matcher(stringValue);
                String finalValue = stringValue;
                while (regexMatcher.find()) {
                    String matchedString = regexMatcher.group(0);
                    String bindingName;
                    if (regexMatcher.group(3) != null) {

                        // has NO formatter
                        dataPath = regexMatcher.group(3);
                        result = Utils.readJson(dataPath, viewManager.getDataContext().getData(), viewManager.getDataContext().getIndex());
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
                        result = Utils.readJson(dataPath, viewManager.getDataContext().getData(), viewManager.getDataContext().getIndex());
                        if (result.isSuccess() && null != result.element) {
                            formattedValue = format(result.element, formatterName);
                        } else {
                            formattedValue = dataPath;
                        }

                        finalValue = finalValue.replace(matchedString, formattedValue != null ? formattedValue : "");
                        bindingName = dataPath;
                    }
                    addBinding(viewManager, bindingName, attribute, stringValue, true);
                }

                // remove the REGEX_PREFIX
                finalValue = finalValue.substring(1);

                // return as a JsonPrimitive
                value = new JsonPrimitive(finalValue);

                break;
        }

        return value;
    }

    public String isDataPath(JsonElement element) {
        if (!element.isJsonPrimitive()) {
            return null;
        }
        String attributeValue = element.getAsString();
        if (attributeValue != null && !"".equals(attributeValue) &&
                (attributeValue.charAt(0) == ProteusConstants.DATA_PREFIX || attributeValue.charAt(0) == ProteusConstants.REGEX_PREFIX)) {
            return attributeValue;
        }
        return null;
    }

    private void addBinding(ProteusViewManager viewManager, String bindingName, String attributeName, String attributeValue, boolean hasRegEx) {
        // check if the view is in update mode if not that means that the update flow
        // is running and we must not add more bindings for they will be duplicates
        if (!viewManager.isViewUpdating()) {
            Binding binding = new Binding(bindingName, attributeName, attributeValue, hasRegEx);
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

    @Nullable
    protected JsonObject onLayoutRequired(String type, ProteusView parent) {
        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "Fetching child layout: " + type);
        }
        if (listener != null) {
            return listener.onLayoutRequired(type, parent);
        }
        return null;
    }

    public void registerFormatter(Formatter formatter) {
        this.formatter.put(formatter.getName(), formatter);
    }

    public void unregisterFormatter(String formatterName) {
        if (Formatter.NOOP.getName().equals(formatterName)) {
            return;
        }
        this.formatter.remove(formatterName);
    }

    public Formatter getFormatter(String formatterName) {
        return this.formatter.get(formatterName);
    }
}
