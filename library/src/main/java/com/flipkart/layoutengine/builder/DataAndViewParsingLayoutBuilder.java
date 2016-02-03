package com.flipkart.layoutengine.builder;

import android.view.View;

import com.flipkart.layoutengine.toolbox.Styles;
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

    protected DataAndViewParsingLayoutBuilder(Map<String, JsonObject> viewProvider) {
        super();
        this.viewProvider = viewProvider;
    }

    @Override
    protected ProteusView onUnknownViewEncountered(String type, View parent, JsonObject source, JsonObject data, int index, Styles styles) {
        JsonElement viewElement = null;
        if (viewProvider != null) {
            viewElement = viewProvider.get(type);
        }
        if (viewElement != null) {
            JsonObject layout = viewElement.getAsJsonObject();
            layout = Utils.mergeLayouts(layout, source);
            ProteusView createdView = build(parent, layout, data, index, styles);
            onViewBuiltFromViewProvider(createdView, type, parent, index);
            return createdView;
        }
        return super.onUnknownViewEncountered(type, parent, source, data, index, styles);
    }

    private void onViewBuiltFromViewProvider(ProteusView view, String type, View parent, int childIndex) {
        if (listener != null) {
            listener.onViewBuiltFromViewProvider(view, parent, type, childIndex);
        }
    }
}
