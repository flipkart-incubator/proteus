package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A layout builder which can parse data bindings before passing it on to {@link SimpleLayoutBuilder}
 */
public class DataParsingLayoutBuilder extends SimpleLayoutBuilder {
    public static final String DATA_CONTEXT = "dataContext";

    DataParsingLayoutBuilder(Context context) {
        super(context);
    }

    @Override
    protected JsonArray parseChildren(LayoutHandler handler, ParserContext context,
                                      JsonElement childrenElement, int childIndex) {
        if (childrenElement.isJsonPrimitive()) {
            String attributeValue = childrenElement.getAsString();
            if (attributeValue != null && !"".equals(attributeValue)) {
                childrenElement = getElementFromData(attributeValue, context.getDataProvider(), childIndex);
            }
        }
        return super.parseChildren(handler, context, childrenElement, childIndex);
    }

    @Override
    public ProteusView build(View parent, JsonObject layout, JsonObject data) {
        return super.build(parent, layout, data);
    }

    @Override
    protected ParserContext createParserContext(JsonObject data) {
        ParserContext parserContext = super.createParserContext(data);
        if (data != null) {
            parserContext.setDataProvider(new GsonProvider(data));
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
                    parent,
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

    private JsonElement findAndReplaceValues(JsonElement jsonDataValue, ParserContext context,
                                             LayoutHandler<View> handler, String attributeName,
                                             ProteusView<View> associatedProteusView, ProteusView parent,
                                             int childIndex) {

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
                                context.getDataProvider(),
                                childIndex).getAsString());
                        bindingName = dataPath;
                    } else {

                        // has formatter
                        String dataPath = regexMatcher.group(1);
                        String formatterName = regexMatcher.group(2);

                        String formattedValue = Utils.format(getElementFromData(dataPath,
                                context.getDataProvider(),
                                childIndex).getAsString(), formatterName);
                        finalValue = finalValue.replace(matchedString, formattedValue);
                        bindingName = dataPath;
                    }
                    addBinding(associatedProteusView,
                            bindingName,
                            attributeName,
                            attributeValue,
                            context,
                            handler,
                            parent,
                            childIndex,
                            true);
                }
                finalValue = finalValue.substring(1);
                jsonDataValue = Utils.getStringAsJsonElement(finalValue);

            } else if (attributeValue.charAt(0) == DataParsingAdapter.DATA_PREFIX) {
                JsonElement elementFromData = getElementFromData(attributeValue.substring(1),
                        context.getDataProvider(), childIndex);
                if (elementFromData != null) {
                    jsonDataValue = elementFromData;
                }
                addBinding(associatedProteusView,
                        attributeValue.substring(1),
                        attributeName,
                        attributeValue,
                        context,
                        handler,
                        parent,
                        childIndex,
                        false);
            }
        }
        return jsonDataValue;
    }

    private void addBinding(ProteusView associatedProteusView, String bindingName, String attributeName,
                            String attributeValue, ParserContext context, LayoutHandler handler,
                            ProteusView parent, int childIndex, boolean hasRegEx) {
        // check if the view is in update mode
        // if not that means that the update flow
        // is running and we must not add more bindings
        // for they will be duplicates
        DataProteusView dataProteusView = (DataProteusView) associatedProteusView;
        if (!dataProteusView.isViewUpdating()) {
            JsonObject dataContext = context.getDataContext();
            if (dataContext != null) {
                String dataContextBindingAlias = bindingName.split(GsonProvider.DATA_PATH_DELIMITER)[0];
                JsonElement dataContextBindingPathElement = dataContext.get(dataContextBindingAlias);
                if (dataContextBindingPathElement != null) {
                    String dataContextBindingPath = dataContextBindingPathElement.getAsString();
                    dataContextBindingPath = dataContextBindingPath.replace(GsonProvider.CHILD_INDEX_REFERENCE, String.valueOf(childIndex));
                    bindingName = bindingName.replaceFirst(Pattern.quote(dataContextBindingAlias), dataContextBindingPath);
                }
            }

            Binding binding = new Binding(context,
                    handler,
                    bindingName,
                    attributeName,
                    attributeValue,
                    dataProteusView,
                    parent,
                    childIndex,
                    hasRegEx);

            dataProteusView.addBinding(binding);
        }
    }

    protected ParserContext getNewParserContext(final ParserContext oldContext,
                                                final JsonObject currentViewJsonObject,
                                                final int childIndex) {

        JsonElement dataContextElement = currentViewJsonObject.get(DATA_CONTEXT);

        if (dataContextElement != null) {
            ParserContext newContext = oldContext.clone();
            Provider oldProvider = oldContext.getDataProvider();
            JsonObject currentDataContext = dataContextElement.getAsJsonObject();

            if (oldProvider != null) {
                Provider newProvider = oldProvider.clone();
                JsonElement newRoot = constructNewRoot(currentDataContext, oldProvider, childIndex);
                newProvider.setRoot(newRoot);
                newContext.setDataProvider(newProvider);
                newContext.setDataContext(currentDataContext);
                return newContext;
            } else {
                Log.e(TAG, "When dataContext is specified, data provider cannot be null");
            }
        }
        return oldContext;
    }

    @Override
    protected ProteusView createProteusViewToReturn(View createdView, int index, ProteusView parent) {
        return new DataProteusView(new SimpleProteusView(createdView, index, parent));
    }

    @Override
    protected void prepareView(ProteusView proteusView, ParserContext parserContext) {
        ((DataProteusView) proteusView).setParserContext(parserContext);
    }

    private JsonElement constructNewRoot(JsonObject currentDataContext, Provider oldProvider, int childIndex) {
        JsonObject newRoot = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : currentDataContext.entrySet()) {
            JsonElement data = getElementFromData(entry.getValue().getAsString(), oldProvider, childIndex);
            newRoot.add(entry.getKey(), data);
        }
        return newRoot;
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
