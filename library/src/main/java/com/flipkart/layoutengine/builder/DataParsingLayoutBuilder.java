package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.flipkart.layoutengine.DataContext;
import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.binding.Binding;
import com.flipkart.layoutengine.exceptions.InvalidDataPathException;
import com.flipkart.layoutengine.exceptions.JsonNullException;
import com.flipkart.layoutengine.exceptions.NoSuchDataPathException;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.provider.ProteusConstants;
import com.flipkart.layoutengine.provider.JsonProvider;
import com.flipkart.layoutengine.toolbox.Formatter;
import com.flipkart.layoutengine.toolbox.Utils;
import com.flipkart.layoutengine.view.DataProteusView;
import com.flipkart.layoutengine.view.ProteusView;
import com.flipkart.layoutengine.view.SimpleProteusView;
import com.google.gson.JsonArray;
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
    public static final String TAG = Utils.getTagPrefix() + DataParsingLayoutBuilder.class.getSimpleName();
    public static final String DATA_CONTEXT = "dataContext";
    public static final String DATA_VISIBILITY = "data";

    private Map<String, Formatter> formatter = new HashMap<>();

    protected DataParsingLayoutBuilder(Context context) {
        super(context);
    }

    @Override
    protected JsonArray parseChildren(LayoutHandler handler, ParserContext parserContext,
                                      JsonElement childrenElement, int childIndex) {
        if (childrenElement.isJsonPrimitive()) {
            String attributeValue = childrenElement.getAsString();
            if (attributeValue != null && !"".equals(attributeValue)) {
                try {
                    childrenElement = Utils.getElementFromData(attributeValue,
                            parserContext.getDataContext().getDataProvider(),
                            childIndex);
                } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException e) {
                    Log.e(TAG + "#parseChildren()", e.getMessage());
                    childrenElement = new JsonArray();
                }
            }
        }
        return super.parseChildren(handler, parserContext, childrenElement, childIndex);
    }

    @Override
    public ProteusView build(View parent, JsonObject layout, JsonObject data, int childIndex) {
        return super.build(parent, layout, data, childIndex);
    }

    @Override
    protected ParserContext createParserContext(JsonObject data) {
        ParserContext parserContext = super.createParserContext(data);
        if (data != null) {
            parserContext.getDataContext().setDataProvider(new JsonProvider(data));
        }
        return parserContext;
    }

    @Override
    protected ProteusView buildImpl(ParserContext context, final ProteusView parent,
                                    final JsonObject currentViewJsonObject, View existingView,
                                    final int childIndex) {
        context = getNewParserContext(context, currentViewJsonObject, childIndex);
        return super.buildImpl(context, parent, currentViewJsonObject, existingView, childIndex);
    }

    @Override
    public boolean handleAttribute(LayoutHandler handler, ParserContext context,
                                   String attributeName, JsonObject viewJsonObject,
                                   JsonElement jsonDataValue, ProteusView associatedProteusView,
                                   ProteusView parent, int childIndex) {
        if (jsonDataValue.isJsonPrimitive()) {
            if (DATA_CONTEXT.equals(attributeName)) {
                return true;
            }
            jsonDataValue = this.findAndReplaceValues(jsonDataValue,
                    context,
                    handler,
                    attributeName,
                    associatedProteusView,
                    viewJsonObject,
                    childIndex);
        }
        return super.handleAttribute(handler,
                context,
                attributeName,
                viewJsonObject,
                jsonDataValue,
                associatedProteusView,
                parent,
                childIndex);
    }

    private JsonElement findAndReplaceValues(JsonElement jsonDataValue, ParserContext parserContext,
                                             LayoutHandler handler, String attributeName,
                                             ProteusView proteusView, JsonObject viewJsonObject,
                                             int childIndex) {

        boolean failed = false;
        String attributeValue = jsonDataValue.getAsString();
        DataProteusView associatedProteusView = (DataProteusView) proteusView;

        if (attributeValue != null && !"".equals(attributeValue) &&
                (attributeValue.charAt(0) == ProteusConstants.DATA_PREFIX ||
                        attributeValue.charAt(0) == ProteusConstants.REGEX_PREFIX)) {

            if (attributeValue.charAt(0) == ProteusConstants.REGEX_PREFIX) {
                Matcher regexMatcher = ProteusConstants.REGEX_PATTERN.matcher(attributeValue);
                String finalValue = attributeValue;

                while (regexMatcher.find()) {
                    String matchedString = regexMatcher.group(0);
                    String bindingName;
                    if (regexMatcher.group(3) != null) {

                        // has NO formatter
                        String dataPath = regexMatcher.group(3);
                        try {
                            finalValue = finalValue.replace(matchedString, Utils.getElementFromData(
                                    dataPath,
                                    parserContext.getDataContext().getDataProvider(),
                                    parserContext.getDataContext().getIndex()).getAsString());
                        } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException e) {
                            Log.e(TAG + "#findAndReplaceValues()", e.getMessage());
                            e.printStackTrace();
                            finalValue = dataPath;
                            failed = true;
                        }
                        bindingName = dataPath;
                    } else {

                        // has formatter
                        String dataPath = regexMatcher.group(1);
                        String formatterName = regexMatcher.group(2);

                        String formattedValue = null;
                        try {
                            formattedValue = format(Utils.getElementFromData(dataPath,
                                            parserContext.getDataContext().getDataProvider(),
                                            parserContext.getDataContext().getIndex()).getAsString(),
                                    formatterName);
                        } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException e) {
                            Log.e(TAG + "#findAndReplaceValues()", e.getMessage());
                            e.printStackTrace();
                            formattedValue = dataPath;
                            failed = true;
                        }
                        finalValue = finalValue.replace(matchedString, formattedValue);
                        bindingName = dataPath;
                    }
                    addBinding(associatedProteusView,
                            bindingName,
                            attributeName,
                            attributeValue,
                            handler,
                            true);
                }

                // remove the REGEX_PREFIX
                finalValue = finalValue.substring(1);

                // return as a JSONPrimitive
                jsonDataValue = Utils.getStringAsJsonElement(finalValue);

            } else if (attributeValue.charAt(0) == ProteusConstants.DATA_PREFIX) {
                JsonElement elementFromData = null;
                try {
                    elementFromData = Utils.getElementFromData(attributeValue.substring(1),
                            parserContext.getDataContext().getDataProvider(),
                            childIndex);
                } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException e) {
                    Log.e(TAG + "#findAndReplaceValues()", e.getMessage());
                    e.printStackTrace();
                    failed = true;
                    elementFromData = new JsonPrimitive(0);
                }

                if (elementFromData != null) {
                    jsonDataValue = elementFromData;
                }
                addBinding(associatedProteusView,
                        attributeValue.substring(1),
                        attributeName,
                        attributeValue,
                        handler,
                        false);
            }
        }

        if (associatedProteusView.getView() != null) {
            if (failed) {
                if (viewJsonObject != null && !viewJsonObject.isJsonNull()
                        && viewJsonObject.get(Attributes.View.Visibility.getName()) != null) {
                    String visibility = viewJsonObject.get(Attributes.View.Visibility.getName()).getAsString();
                    if (DATA_VISIBILITY.equals(visibility)) {
                        associatedProteusView.getView().setVisibility(View.INVISIBLE);
                    }
                } else {
                    associatedProteusView.getView().setVisibility(View.GONE);
                }
            } else if (associatedProteusView.isViewUpdating()) {
                associatedProteusView.getView().setVisibility(View.VISIBLE);
            }
        }

        return jsonDataValue;
    }

    private void addBinding(DataProteusView dataProteusView, String bindingName, String attributeName,
                            String attributeValue, LayoutHandler handler, boolean hasRegEx) {
        // check if the view is in update mode
        // if not that means that the update flow
        // is running and we must not add more bindings
        // for they will be duplicates
        if (!dataProteusView.isViewUpdating()) {
            Binding binding = new Binding(handler,
                    bindingName,
                    attributeName,
                    attributeValue,
                    hasRegEx);

            dataProteusView.addBinding(binding);
        }
    }

    protected ParserContext getNewParserContext(final ParserContext oldParserContext,
                                                final JsonObject currentViewJsonObject,
                                                final int childIndex) {

        JsonElement scopeElement = currentViewJsonObject.get(DATA_CONTEXT);

        if (scopeElement == null) {
            return oldParserContext;
        }

        DataContext oldDataContext = oldParserContext.getDataContext();

        if (oldDataContext.getDataProvider() == null) {
            Log.e(TAG + "#getNewParserContext()", "When scope is specified, data provider cannot be null");
            return oldParserContext;
        }

        DataContext newDataContext = getNewDataContext(scopeElement.getAsJsonObject(), oldDataContext, childIndex);
        ParserContext newParserContext = oldParserContext.clone();
        newParserContext.setDataContext(newDataContext);

        return newParserContext;
    }

    @Override
    protected ProteusView createProteusViewToReturn(View createdView, int index, ProteusView parent) {
        return new DataProteusView(new SimpleProteusView(createdView, index, parent));
    }

    @Override
    protected void prepareView(ProteusView proteusView, ParserContext parserContext) {
        ((DataProteusView) proteusView).setParserContext(parserContext);
    }

    private DataContext getNewDataContext(JsonObject currentScope, DataContext oldDataContext, int childIndex) {
        Map<String, String> newScope = new HashMap<>();
        JsonObject newData = new JsonObject();
        Map<String, String> oldScope = oldDataContext.getScope();
        Map<String, String> oldReverseScope = oldDataContext.getReverseScopeMap();
        Map<String, String> newReverseScope = new HashMap<>();
        JsonProvider oldDataProvider = oldDataContext.getDataProvider();
        JsonObject oldData = oldDataProvider.getData().getAsJsonObject();

        if (oldData == null) {
            oldData = new JsonObject();
        }

        for (Map.Entry<String, JsonElement> entry : currentScope.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getAsString();
            JsonElement data;
            try {
                data = Utils.getElementFromData(value, oldDataProvider, childIndex);
            } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException e) {
                Log.e(TAG + "#getNewDataContext()", e.getMessage());
                data = entry.getValue();
            }

            newData.add(key, data);
            newScope.put(key, value);
            String unaliasedValue = value.replace(ProteusConstants.CHILD_INDEX_REFERENCE,
                    String.valueOf(childIndex));
            newReverseScope.put(unaliasedValue, key);
        }
        if (oldScope != null) {
            newScope.putAll(oldScope);
        }

        if (oldReverseScope != null) {
            newReverseScope.putAll(oldReverseScope);
        }

        Utils.addElements(newData, oldData.entrySet(), false);

        return new DataContext(new JsonProvider(newData), newScope, newReverseScope, oldDataContext, childIndex);
    }

    private String format(String toFormat, String formatterName) {
        Formatter formatter = this.formatter.get(formatterName);
        if (formatter == null) {
            formatter = Formatter.NOOP;
        }

        return formatter.format(toFormat);
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
}
