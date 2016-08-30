package com.flipkart.layoutengine.builder;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.provider.Provider;
import com.flipkart.layoutengine.toolbox.Result;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A layout builder which can parse view blocks before passing it on to {@link SimpleLayoutBuilder}
 */
public class ViewParsingLayoutBuilder extends SimpleLayoutBuilder {

    private Provider viewProvider;

    public ViewParsingLayoutBuilder(Activity activity, @Nullable IdGenerator idGenerator, Provider viewProvider) {
        super(activity, idGenerator);
        this.viewProvider = viewProvider;
    }

    @Override
    protected ProteusView onUnknownViewEncountered(ParserContext context, String viewType,
                                                   ProteusView parent, JsonObject layout, int childIndex) {
        JsonElement viewElement = null;
        if (viewProvider != null) {
            Result result = viewProvider.getObject(viewType, childIndex);
            viewElement = result.isSuccess() ? result.element : null;
        }
        if (viewElement != null) {
            JsonObject viewLayoutObject = viewElement.getAsJsonObject();
            return buildImpl(context, parent, viewLayoutObject, childIndex, parent.getStyles());
        }
        return super.onUnknownViewEncountered(context, viewType, parent, layout, childIndex);
    }
}
