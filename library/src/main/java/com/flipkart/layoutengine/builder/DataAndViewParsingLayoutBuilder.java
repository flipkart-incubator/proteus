package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.provider.Provider;
import com.flipkart.layoutengine.toolbox.NetworkDrawableDownloadHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A layout builder which can parse @data and @view blocks before passing it on to {@link SimpleLayoutBuilder}
 */
public class DataAndViewParsingLayoutBuilder extends DataParsingLayoutBuilder {
    private final Provider viewProvider;

    DataAndViewParsingLayoutBuilder(Context context, Provider dataProvider, Provider viewProvider) {
        super(context,dataProvider);
        this.viewProvider = viewProvider;
    }


    @Override
    protected View onUnknownViewEncountered(ParserContext context, String viewType, ViewGroup parent, JsonObject jsonObject, int childIndex) {
        JsonElement jsonElement = viewProvider.getObject(viewType, childIndex);
        if(jsonElement!=null)
        {
            return buildImpl(context, parent,jsonElement.getAsJsonObject(), null , childIndex);
        }
        else {
            return super.onUnknownViewEncountered(context, viewType, parent, jsonObject, childIndex);
        }
    }
}
