package com.flipkart.layoutengine.builder;

import android.app.Activity;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.exceptions.InvalidDataPathException;
import com.flipkart.layoutengine.exceptions.JsonNullException;
import com.flipkart.layoutengine.exceptions.NoSuchDataPathException;
import com.flipkart.layoutengine.provider.Provider;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A layout builder which can parse view blocks before passing it on to {@link SimpleLayoutBuilder}
 */
public class ViewParsingLayoutBuilder extends SimpleLayoutBuilder {

    private Provider viewProvider;
    private Logger logger = LoggerFactory.getLogger(ViewParsingLayoutBuilder.class);

    public ViewParsingLayoutBuilder(Activity activity, Provider viewProvider) {
        super(activity);
        this.viewProvider = viewProvider;
    }

    @Override
    protected ProteusView onUnknownViewEncountered(ParserContext context, String viewType,
                                                   ProteusView parent, JsonObject layout, int childIndex) {
        JsonElement viewElement = null;
        if (viewProvider != null) {
            try {
                viewElement = viewProvider.getObject(viewType, childIndex);
            } catch (InvalidDataPathException | NoSuchDataPathException | JsonNullException e) {
                if(logger.isErrorEnabled()) {
                    logger.error(e.getMessage());
                }
            }
        }
        if (viewElement != null) {
            JsonObject viewLayoutObject = viewElement.getAsJsonObject();
            return buildImpl(context, parent, viewLayoutObject, childIndex, parent.getStyles());
        }
        return super.onUnknownViewEncountered(context, viewType, parent, layout, childIndex);
    }
}
