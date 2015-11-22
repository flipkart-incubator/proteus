package com.flipkart.layoutengine.builder;

import android.content.Context;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.toolbox.Utils;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * A layout builder which can parse @data and @view blocks before passing it on to
 * {@link SimpleLayoutBuilder}
 */
public class DataAndViewParsingLayoutBuilder extends DataParsingLayoutBuilder {

    private Map<String, JsonObject> viewProvider;

    protected DataAndViewParsingLayoutBuilder(Context context, Map<String, JsonObject> viewProvider) {
        super(context);
        this.viewProvider = viewProvider;
    }

    @Override
    protected ProteusView onUnknownViewEncountered(ParserContext context, String viewType,
                                                   ProteusView parent, JsonObject source,
                                                   int index) {
        JsonElement viewElement = null;
        if (viewProvider != null) {
            viewElement = viewProvider.get(viewType);
        }
        if (viewElement != null) {
            JsonObject layout = viewElement.getAsJsonObject();
            layout = Utils.mergeLayouts(layout, source);
            ProteusView createdView = buildImpl(context, parent, layout, index, parent.getStyles());
            onViewBuiltFromViewProvider(createdView, viewType, layout, parent, index);
            return createdView;
        }
        return super.onUnknownViewEncountered(context, viewType, parent, source, index);
    }

    private void onViewBuiltFromViewProvider(ProteusView view, String viewType,
                                             JsonObject layout, ProteusView parent, int childIndex) {
        if (listener != null) {
            listener.onViewBuiltFromViewProvider(view, viewType, layout, parent, childIndex);
        }
    }
}
