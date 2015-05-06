package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.provider.Provider;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A layout builder which can parse @data and @view blocks before passing it on to
 * {@link SimpleLayoutBuilder}
 */
public class DataAndViewParsingLayoutBuilder extends DataParsingLayoutBuilder {
    private final Provider viewProvider;

    DataAndViewParsingLayoutBuilder(Context context, Provider viewProvider) {
        super(context);
        this.viewProvider = viewProvider;
    }

    @Override
    protected ProteusView onUnknownViewEncountered(ParserContext context, String viewType,
                                                   ViewGroup parent, JsonObject viewJsonObject,
                                                   int childIndex) {
        JsonElement viewElement = viewProvider.getObject(viewType, childIndex);
        if (viewElement != null) {
            JsonObject viewLayoutObject = viewElement.getAsJsonObject();
            ProteusView createdView = buildImpl(context, parent, viewLayoutObject, null, childIndex);
            ParserContext newParserContext = getNewParserContext(context, viewLayoutObject, childIndex);
            onViewBuiltFromViewProvider(createdView, viewType, newParserContext, viewLayoutObject, parent, childIndex);
            return createdView;
        }
        return super.onUnknownViewEncountered(context, viewType, parent, viewJsonObject, childIndex);
    }

    private void onViewBuiltFromViewProvider(ProteusView createdView, String viewType, ParserContext parserContext, JsonObject viewLayoutObject, ViewGroup parent, int childIndex) {
        if (listener != null) {
            listener.onViewBuiltFromViewProvider(createdView, viewType, parserContext, viewLayoutObject, parent, childIndex);
        }
    }
}
