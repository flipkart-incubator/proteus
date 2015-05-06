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
        return new DataProteusView(super.build(parent, layout, data));
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
            String attributeValue = jsonDataValue.getAsString();
            JsonElement elementFromData = getElementFromData(jsonDataValue, context.getDataProvider(), childIndex);
            if (elementFromData != null) {
                jsonDataValue = elementFromData;

                // check if the view is in update mode
                // if not that means that the update flow
                // is running and we must not add more bindings
                // for they will be duplicates
                DataProteusView dataProteusView = (DataProteusView) associatedProteusView;
                if (attributeValue.charAt(0) == PREFIX && !dataProteusView.isViewUpdating()) {
                    Binding binding = new Binding(context,
                            handler,
                            attributeValue,
                            attributeName,
                            attributeValue,
                            associatedProteusView,
                            parent,
                            childIndex);

                    dataProteusView.addBinding(binding);
                }
            }
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

    protected ParserContext getNewParserContext(final ParserContext oldContext,
                                                final JsonObject currentViewJsonObject,
                                                final int childIndex) {
        JsonElement dataContextElement = currentViewJsonObject.get(DATA_CONTEXT);
        if (dataContextElement != null) {
            ParserContext newContext = oldContext.clone();
            Provider oldProvider = oldContext.getDataProvider();
            if (oldProvider != null) {
                Provider newProvider = oldProvider.clone();
                JsonElement newRoot = getElementFromData(dataContextElement, oldProvider, childIndex);
                newProvider.setRoot(newRoot);
                newContext.setDataProvider(newProvider);
                newContext.setDataContext(dataContextElement.getAsString());
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
