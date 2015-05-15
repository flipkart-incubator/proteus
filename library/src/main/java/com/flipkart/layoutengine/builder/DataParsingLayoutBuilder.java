package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.binding.Binding;
import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.provider.DataParsingAdapter;
import com.flipkart.layoutengine.provider.Provider;
import com.flipkart.layoutengine.toolbox.Utils;
import com.flipkart.layoutengine.view.DataProteusView;
import com.flipkart.layoutengine.view.ProteusView;
import com.flipkart.layoutengine.view.SimpleProteusView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A layout builder which can parse data bindings before passing it on to {@link SimpleLayoutBuilder}
 */
public class DataParsingLayoutBuilder extends SimpleLayoutBuilder {
    private static final Character PREFIX = DataParsingAdapter.PREFIX;
    protected static final String DATA_CONTEXT = "dataContext";

    DataParsingLayoutBuilder(Context context) {
        super(context);
    }

    @Override
    protected JsonArray parseChildren(LayoutHandler handler, ParserContext context,
                                      JsonElement childrenElement, int childIndex) {
        childrenElement = getElementFromData(childrenElement, context.getDataProvider(), childIndex);
        return super.parseChildren(handler, context, childrenElement, childIndex);
    }

    @Override
    public ProteusView build(ViewGroup parent, JsonObject layout, JsonObject data) {
        return super.build(parent, layout, data);
    }

    @Override
    protected ProteusView buildImpl(ParserContext context, ViewGroup parent, JsonObject currentViewJsonObject,
                                    View existingView, int childIndex) {
        context = getNewParserContext(context, currentViewJsonObject, childIndex);
        return super.buildImpl(context, parent, currentViewJsonObject, existingView, childIndex);
    }

    @Override
    public boolean handleAttribute(LayoutHandler<View> handler, ParserContext context,
                                   String attributeName, JsonObject jsonObject,
                                   JsonElement jsonDataValue, ProteusView<View> associatedProteusView,
                                   ViewGroup parent, int childIndex) {
        if (jsonDataValue.isJsonPrimitive()) {
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
                                             ProteusView<View> associatedProteusView, ViewGroup parent,
                                             int childIndex) {

        String attributeValue = jsonDataValue.getAsString();
        if (attributeValue != null && !attributeValue.equals("")) {
            JsonElement elementFromData = getElementFromData(jsonDataValue, context.getDataProvider(), childIndex);
            if (elementFromData != null) {
                jsonDataValue = elementFromData;

                // check if the view is in update mode
                // if not that means that the update flow
                // is running and we must not add more bindings
                // for they will be duplicates
                DataProteusView dataProteusView = (DataProteusView) associatedProteusView;
                if (attributeValue.charAt(0) == PREFIX && !dataProteusView.isViewUpdating()) {
                    String bindindingName = attributeValue;
                    String dataContext = context.getDataContext();
                    if (dataContext != null) {
                        bindindingName = dataContext + "." + bindindingName.substring(1);
                    }

                    Binding binding = new Binding(context,
                            handler,
                            bindindingName,
                            attributeName,
                            attributeValue,
                            associatedProteusView,
                            parent,
                            childIndex);

                    dataProteusView.addBinding(binding);
                }
            }
        }
        return jsonDataValue;
    }

    public void s() {
        String attr = "hello ${person.name}. How is ${person.children[0].name}?";

        Pattern regex = Pattern.compile("(\\$\\{+.?\\})");
        Matcher regexMatcher = regex.matcher(attr);

        while (regexMatcher.find()) {
            String dataPath = regexMatcher.group();
            System.out.println(dataPath);
        }
    }

    protected ParserContext getNewParserContext(final ParserContext oldContext,
                                                final JsonObject currentViewJsonObject,
                                                final int childIndex) {
        JsonElement dataContextElement = currentViewJsonObject.get(DATA_CONTEXT);

        if (dataContextElement != null) {
            currentViewJsonObject.remove(DATA_CONTEXT);
            ParserContext newContext = oldContext.clone();
            Provider oldProvider = oldContext.getDataProvider();
            if (oldProvider != null) {
                Provider newProvider = oldProvider.clone();
                JsonElement newRoot = getElementFromData(dataContextElement, oldProvider, childIndex);
                newProvider.setRoot(newRoot);
                newContext.setDataProvider(newProvider);
                String currentDataContext = dataContextElement.getAsString();
                String oldDataContext = oldContext.getDataContext();

                if (currentDataContext != null && oldDataContext != null) {
                    currentDataContext = currentDataContext.substring(1);
                    newContext.setDataContext(oldDataContext + "." + currentDataContext);
                } else if (currentDataContext != null) {
                    newContext.setDataContext(currentDataContext);
                } else {
                    newContext.setDataContext(oldDataContext);
                }

                return newContext;

            } else {
                Log.e(TAG, "When dataContext is specified, data provider cannot be null");
            }
        }
        return oldContext;
    }

    @Override
    protected ProteusView createProteusViewToReturn(View createdView) {
        return new DataProteusView(new SimpleProteusView(createdView));
    }

    @Override
    protected void prepareView(ProteusView proteusView, Provider dataProvider) {
        ((DataProteusView) proteusView).setDataProvider(dataProvider);
    }

    protected JsonElement getElementFromData(JsonElement element, Provider dataProvider, int childIndex) {
        return Utils.getElementFromData(PREFIX, element, dataProvider, childIndex);
    }
}
