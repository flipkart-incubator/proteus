package com.flipkart.layoutengine.builder;

import android.content.Context;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.provider.GsonProvider;
import com.flipkart.layoutengine.provider.Provider;
import com.flipkart.layoutengine.toolbox.Utils;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A layout builder which can parse @data and @view blocks before passing it on to
 * {@link SimpleLayoutBuilder}
 */
public class DataAndViewParsingLayoutBuilder extends DataParsingLayoutBuilder {
    private Provider viewProvider;

    protected DataAndViewParsingLayoutBuilder(Context context, Provider viewProvider) {
        super(context);
        this.viewProvider = viewProvider;
    }

    @Override
    protected ProteusView onUnknownViewEncountered(ParserContext context, String viewType,
                                                   ProteusView parent, JsonObject viewJsonObject,
                                                   int childIndex) {
        JsonElement viewElement = null;
        if (viewProvider != null) {
            viewElement = viewProvider.getObject(viewType, childIndex);
        }
        if (viewElement != null) {
            JsonObject viewLayoutObject = viewElement.getAsJsonObject();
            ProteusView createdView = buildImpl(context, parent, viewLayoutObject, null, childIndex);
            ParserContext newParserContext = getNewParserContext(context, viewLayoutObject, childIndex);
            onViewBuiltFromViewProvider(createdView, viewType, newParserContext, viewLayoutObject, parent, childIndex);
            return createdView;
        }
        return super.onUnknownViewEncountered(context, viewType, parent, viewJsonObject, childIndex);
    }

    public void updateLayoutProvider(JsonObject newViewProvider) {
        if (viewProvider != null && viewProvider.getData() != null) {
            JsonObject viewProviderData = Utils.merge(viewProvider.getData().getAsJsonObject(), newViewProvider);
            viewProvider.setData(viewProviderData);
        } else {
            viewProvider = new GsonProvider(newViewProvider);
        }
    }

    private void onViewBuiltFromViewProvider(ProteusView createdView, String viewType,
                                             ParserContext parserContext, JsonObject viewLayoutObject,
                                             ProteusView parent, int childIndex) {
        if (listener != null) {
            listener.onViewBuiltFromViewProvider(createdView, viewType, parserContext, viewLayoutObject, parent, childIndex);
        }
    }
}
