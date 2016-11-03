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
import android.view.ViewGroup;

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
        Object layout = viewManager.getLayout();
        parser.setInput(layout);

        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "Parsing children for view with " + Utils.getLayoutIdentifier(parser));
        }

        if (layout == null) {
            return;
        }

        String children = isDataDriven(viewManager);

        if (children != null) {

            int length = 0;
            String dataPath = children.substring(1);
            viewManager.setDataPathForChildren(dataPath);

            Result result = Utils.readJson(dataPath, viewManager.getDataContext().getData(), viewManager.getDataContext().getIndex());
            JsonElement elementFromData = result.isSuccess() ? result.element : null;

            if (null != elementFromData) {
                length = ParseHelper.parseInt(elementFromData.getAsString());
            }

            // get the child type
            Object childLayout = getChildLayout(parser, layout, view);

            viewManager.setChildLayout(childLayout);

            for (int index = 0; index < length; index++) {
                ProteusView child = build((ViewGroup) view, parser.setInput(childLayout), viewManager.getDataContext().getData(), viewManager.getStyles(), index);
                if (child != null) {
                    handler.addView(view, child);
                }
            }
        }

        super.handleChildren(handler, parser, view);
    }

    @Nullable
    protected String isDataDriven(ProteusViewManager viewManager) {
        if (viewManager.getLayout() == null || viewManager.getDataContext() == null || viewManager.getDataContext().getData() == null) {
            return null;
        }

        Object layout = viewManager.getLayout();
        LayoutParser parser = viewManager.getLayoutParser();
        parser.setInput(layout);
        parser.peek();

        if (parser.isString(ProteusConstants.CHILD_TYPE) && parser.getString(ProteusConstants.CHILD_TYPE).charAt(0) == ProteusConstants.DATA_PREFIX) {
            return parser.getString(ProteusConstants.CHILD_TYPE);
        }
        return null;
    }

    @Override
    protected ProteusViewManager createViewManager(TypeHandler handler, View parent, LayoutParser layout, JsonObject data, Styles styles, int index) {
        ProteusViewManagerImpl viewManager = new ProteusViewManagerImpl();
        DataContext dataContext, parentDataContext = null;
        Map<String, String> scope = layout.getScope();

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

        viewManager.setLayout(layout);
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
        String stringValue = isDataPath(parser);
        /*if (stringValue != null) {
            parser = this.findAndReplaceValues(handler, view, attribute, stringValue, parser);
        }*/
        return super.handleAttribute(handler, view, attribute, parser);
    }

    private JsonElement findAndReplaceValues(TypeHandler handler, ProteusView view, String attribute, String stringValue, JsonElement value) {
        ProteusViewManager viewManager = view.getViewManager();
        DataContext dataContext = viewManager.getDataContext();

        if (ProteusConstants.isLoggingEnabled()) {
            //Log.d(TAG, "Find '" + stringValue + "' for " + attribute + " for view with " + Utils.getLayoutIdentifier(viewManager.getLayout()));
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

    public String isDataPath(LayoutParser element) {
        if (!element.isString()) {
            return null;
        }
        String attributeValue = element.getString();
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

    // TODO: possible NPE, re-factor required
    public Object getChildLayout(LayoutParser type, Object source, ProteusView view) {
        if (type.isObject()) {
            type.peek();
            return onLayoutRequired(type.getString(ProteusConstants.TYPE), view);
        }
        return null;
    }

    @Nullable
    protected Object onLayoutRequired(String type, ProteusView parent) {
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
