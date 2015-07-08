package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.util.Log;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.exceptions.InvalidDataPathException;
import com.flipkart.layoutengine.exceptions.JsonNullException;
import com.flipkart.layoutengine.exceptions.NoSuchDataPathException;
import com.flipkart.layoutengine.provider.JsonProvider;
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

    public static final String TAG = Utils.getTagPrefix() + DataAndViewParsingLayoutBuilder.class.getSimpleName();
    private Provider viewProvider;

    protected DataAndViewParsingLayoutBuilder(Context context, JsonObject viewProvider) {
        super(context);
        this.viewProvider = new JsonProvider(viewProvider);
    }

    @Override
    protected ProteusView onUnknownViewEncountered(ParserContext context, String viewType,
                                                   ProteusView parent, JsonObject viewJsonObject,
                                                   int childIndex) {
        JsonElement viewElement = null;
        if (viewProvider != null) {
            try {
                viewElement = viewProvider.getObject(viewType, childIndex);
            } catch (InvalidDataPathException | NoSuchDataPathException | JsonNullException e) {
                Log.e(TAG, e.getMessage());
            }
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
            JsonElement viewProviderData = Utils.merge(viewProvider.getData(), newViewProvider);
            viewProvider.setData(viewProviderData);
        } else {
            viewProvider = new JsonProvider(newViewProvider);
        }
    }

    private void onViewBuiltFromViewProvider(ProteusView createdView, String viewType,
                                             ParserContext parserContext, JsonObject viewLayoutObject,
                                             ProteusView parent, int childIndex) {
        if (listener != null) {
            listener.onViewBuiltFromViewProvider(createdView, viewType, parserContext, viewLayoutObject,
                    parent, childIndex);
        }
    }
}
