package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.flipkart.layoutengine.DataContext;
import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.binding.Binding;
import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.provider.DataParsingAdapter;
import com.flipkart.layoutengine.provider.GsonProvider;
import com.flipkart.layoutengine.provider.Provider;
import com.flipkart.layoutengine.toolbox.Formatters;
import com.flipkart.layoutengine.toolbox.Utils;
import com.flipkart.layoutengine.view.DataProteusView;
import com.flipkart.layoutengine.view.ProteusView;
import com.flipkart.layoutengine.view.SimpleProteusView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * A layout builder which can parse data bindings before passing it on to {@link SimpleLayoutBuilder}
 */
public class DataParsingLayoutBuilder extends SimpleLayoutBuilder {
    public static final String DATA_CONTEXT = "dataContext";

    protected DataParsingLayoutBuilder(Context context) {
        super(context);
    }

    @Override
    protected JsonArray parseChildren(LayoutHandler handler, ParserContext parserContext,
                                      JsonElement childrenElement, int childIndex) {
        if (childrenElement.isJsonPrimitive()) {
            String attributeValue = childrenElement.getAsString();
            if (attributeValue != null && !"".equals(attributeValue)) {
                childrenElement = getElementFromData(attributeValue, parserContext.getDataContext().getDataProvider(), childIndex);
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
            parserContext.getDataContext().setDataProvider(new GsonProvider(data));
        }
        return parserContext;
    }

    @Override
    protected ProteusView buildImpl(ParserContext context, ProteusView parent, JsonObject currentViewJsonObject,
                                    View existingView, int childIndex) {
        context = getNewParserContext(context, currentViewJsonObject, childIndex);
        return super.buildImpl(context, parent, currentViewJsonObject, existingView, childIndex);
    }

    @Override
    public boolean handleAttribute(LayoutHandler<View> handler, ParserContext context,
                                   String attributeName, JsonObject jsonObject,
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
                    childIndex);
        }
        return super.handleAttribute(handler,
                context,
                attributeName,
                jsonObject,
                jsonDataValue,
                associatedProteusView,
                parent,
                childIndex);
    }

    private JsonElement findAndReplaceValues(JsonElement jsonDataValue, ParserContext parserContext,
                                             LayoutHandler<View> handler, String attributeName,
                                             ProteusView<View> associatedProteusView, int childIndex) {

        String attributeValue = jsonDataValue.getAsString();

        if (attributeValue != null && !"".equals(attributeValue) &&
                (attributeValue.charAt(0) == DataParsingAdapter.DATA_PREFIX ||
                        attributeValue.charAt(0) == DataParsingAdapter.REGEX_PREFIX)) {

            if (attributeValue.charAt(0) == DataParsingAdapter.REGEX_PREFIX) {
                Matcher regexMatcher = DataParsingAdapter.REGEX_PATTERN.matcher(attributeValue);
                String finalValue = attributeValue;

                while (regexMatcher.find()) {
                    String matchedString = regexMatcher.group(0);
                    String bindingName;
                    if (regexMatcher.group(3) != null) {

                        // has NO formatter
                        String dataPath = regexMatcher.group(3);
                        finalValue = finalValue.replace(matchedString, getElementFromData(
                                dataPath,
                                parserContext.getDataContext().getDataProvider(),
                                childIndex).getAsString());
                        bindingName = dataPath;
                    } else {

                        // has formatter
                        String dataPath = regexMatcher.group(1);
                        String formatterName = regexMatcher.group(2);

                        String formattedValue = Utils.format(getElementFromData(dataPath,
                                parserContext.getDataContext().getDataProvider(),
                                childIndex).getAsString(), formatterName);
                        finalValue = finalValue.replace(matchedString, formattedValue);
                        bindingName = dataPath;
                    }
                    addBinding(associatedProteusView,
                            bindingName,
                            attributeName,
                            attributeValue,
                            parserContext,
                            handler,
                            childIndex,
                            true);
                }
                finalValue = finalValue.substring(1);
                jsonDataValue = Utils.getStringAsJsonElement(finalValue);

            } else if (attributeValue.charAt(0) == DataParsingAdapter.DATA_PREFIX) {
                JsonElement elementFromData = getElementFromData(attributeValue.substring(1),
                        parserContext.getDataContext().getDataProvider(), childIndex);
                if (elementFromData != null) {
                    jsonDataValue = elementFromData;
                }
                addBinding(associatedProteusView,
                        attributeValue.substring(1),
                        attributeName,
                        attributeValue,
                        parserContext,
                        handler,
                        childIndex,
                        false);
            }
        }
        return jsonDataValue;
    }

    private void addBinding(ProteusView associatedProteusView, String bindingName, String attributeName,
                            String attributeValue, ParserContext context, LayoutHandler handler,
                            int childIndex, boolean hasRegEx) {
        // check if the view is in update mode
        // if not that means that the update flow
        // is running and we must not add more bindings
        // for they will be duplicates
        DataProteusView dataProteusView = (DataProteusView) associatedProteusView;
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
            Log.e(TAG, "When scope is specified, data provider cannot be null");
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
        GsonProvider oldDataProvider = oldDataContext.getDataProvider();
        JsonObject oldData = oldDataProvider.getData().getAsJsonObject();

        if (oldData == null) {
            oldData = new JsonObject();
        }

        for (Map.Entry<String, JsonElement> entry : currentScope.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getAsString();
            JsonElement data = getElementFromData(value, oldDataProvider, childIndex);
            newData.add(key, data);
            newScope.put(key, value);
            String unaliasedValue = value.replace(GsonProvider.CHILD_INDEX_REFERENCE, String.valueOf(childIndex));
            newReverseScope.put(unaliasedValue, key);
        }
        if (oldScope != null) {
            newScope.putAll(oldScope);
        }

        if (oldReverseScope != null) {
            newReverseScope.putAll(oldReverseScope);
        }

        Utils.addElements(newData, oldData.entrySet(), false);

        return new DataContext(new GsonProvider(newData), newScope, newReverseScope, oldDataContext);
    }

    protected JsonElement getElementFromData(String element, Provider dataProvider, int childIndex) {
        return Utils.getElementFromData(element, dataProvider, childIndex);
    }

    public void registerFormatter(Formatters.Formatter formatter) {
        Formatters.add(formatter);
    }

    public void unregisterFormatter(String formatterName) {
        Formatters.remove(formatterName);
    }
}
