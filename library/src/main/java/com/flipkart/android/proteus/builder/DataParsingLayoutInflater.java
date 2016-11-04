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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.flipkart.android.proteus.LayoutParser;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.parser.TypeHandler;
import com.flipkart.android.proteus.toolbox.Binding;
import com.flipkart.android.proteus.toolbox.DataContext;
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
 * A layout builder which can parse data bindings before passing it on to {@link SimpleLayoutInflater}
 */
public class DataParsingLayoutInflater extends SimpleLayoutInflater {

    private static final String TAG = "ProteusLayoutInflater";
    private Map<String, Formatter> formatter = new HashMap<>();

    protected DataParsingLayoutInflater(@NonNull IdGenerator idGenerator) {
        super(idGenerator);
    }

    @Override
    protected void handleChildren(TypeHandler handler, LayoutParser parser, ProteusView view) {

        ProteusViewManager viewManager = view.getViewManager();

        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "Parsing children for view with " + Utils.getLayoutIdentifier(parser));
        }

        String children = hasDataDrivenChildren(viewManager);

        if (children != null) {

            int length = 0;
            String dataPath = children.substring(1);
            viewManager.setDataPathForChildren(dataPath);

            Result result = Utils.readJson(dataPath, viewManager.getDataContext().getData(), viewManager.getDataContext().getIndex());
            JsonElement data = result.isSuccess() ? result.element : null;

            if (null != data) {
                length = ParseHelper.parseInt(data.getAsString());
            }

            // get the child layout parser
            LayoutParser childLayoutParser = getChildLayoutParser(parser);
            childLayoutParser.addAttribute(ProteusConstants.CHILDREN, length);
            viewManager.setChildLayoutParser(childLayoutParser);
        }

        super.handleChildren(handler, parser, view);
    }

    @Nullable
    protected String hasDataDrivenChildren(ProteusViewManager viewManager) {
        if (viewManager.getDataContext() == null || viewManager.getDataContext().getData() == null) {
            return null;
        }

        LayoutParser parser = viewManager.getLayoutParser();
        if (parser.isString(ProteusConstants.CHILDREN)) {
            return parser.getString(ProteusConstants.CHILDREN);
        }
        return null;
    }

    @Override
    protected ProteusViewManager createViewManager(TypeHandler handler, View parent, LayoutParser parser, JsonObject data, Styles styles, int index) {
        ProteusViewManagerImpl viewManager = new ProteusViewManagerImpl();
        DataContext dataContext, parentDataContext = null;
        Map<String, String> scope = parser.getScope();

        if (parent instanceof ProteusView) {
            parentDataContext = ((ProteusView) parent).getViewManager().getDataContext();
        }

        if (scope == null) {
            if (parentDataContext != null) {
                dataContext = new DataContext(parentDataContext);
            } else {
                dataContext = new DataContext();
                dataContext.setData(data);
                dataContext.setIndex(index);
            }
        } else {
            if (parentDataContext != null) {
                dataContext = parentDataContext.createChildDataContext(scope, index);
            } else {
                dataContext = new DataContext();
                dataContext.setData(data);
                dataContext = dataContext.createChildDataContext(scope, index);
            }
        }

        viewManager.setLayoutParser(parser);
        viewManager.setDataContext(dataContext);
        viewManager.setStyles(styles);
        viewManager.setProteusLayoutInflater(this);
        viewManager.setTypeHandler(handler);

        return viewManager;
    }

    @Override
    public boolean handleAttribute(TypeHandler handler, ProteusView view, String attribute, LayoutParser parser) {

        if (ProteusConstants.DATA_CONTEXT.equals(attribute)) {
            return true;
        }
        String dataPath = isDataPath(parser);
        if (dataPath != null) {
            parser = this.findAndReturnValue(view, handler, parser, attribute, dataPath);
        }
        return super.handleAttribute(handler, view, attribute, parser);
    }

    private LayoutParser findAndReturnValue(ProteusView view, TypeHandler handler, LayoutParser parser, String key, String value) {

        ProteusViewManager viewManager = view.getViewManager();

        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "Find '" + value + "' for " + key + " for view with " + Utils.getLayoutIdentifier(viewManager.getLayoutParser()));
        }

        char firstChar = TextUtils.isEmpty(value) ? 0 : value.charAt(0);
        String dataPath;
        Result result;
        switch (firstChar) {
            case ProteusConstants.DATA_PREFIX:
                JsonElement elementFromData;
                dataPath = value.substring(1);
                result = Utils.readJson(dataPath, viewManager.getDataContext().getData(), viewManager.getDataContext().getIndex());

                if (result.isSuccess()) {
                    elementFromData = result.element;
                } else {
                    elementFromData = new JsonPrimitive(ProteusConstants.DATA_NULL);
                }

                if (elementFromData != null) {
                    parser = parser.getValueParser(elementFromData);
                }
                addBinding(viewManager, dataPath, key, value, false);
                break;
            case ProteusConstants.REGEX_PREFIX:
                Matcher regexMatcher = ProteusConstants.REGEX_PATTERN.matcher(value);
                String finalValue = value;
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
                    addBinding(viewManager, bindingName, key, value, true);
                }

                // remove the REGEX_PREFIX
                finalValue = finalValue.substring(1);

                // return as a JsonPrimitive
                parser = parser.getValueParser(new JsonPrimitive(finalValue));

                break;
        }

        return parser;
    }

    @Nullable
    public String isDataPath(LayoutParser parser) {
        if (!parser.isString()) {
            return null;
        }
        String attributeValue = parser.getString();
        if (attributeValue != null && !"".equals(attributeValue) && (attributeValue.charAt(0) == ProteusConstants.DATA_PREFIX || attributeValue.charAt(0) == ProteusConstants.REGEX_PREFIX)) {
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

    public LayoutParser getChildLayoutParser(LayoutParser parent) {
        if (parent.isLayout(ProteusConstants.CHILD_TYPE)) {
            return parent.peek(ProteusConstants.CHILD_TYPE).clone();
        } else {
            throw new IllegalStateException("child type is not a layout : " + parent.toString());
        }
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
