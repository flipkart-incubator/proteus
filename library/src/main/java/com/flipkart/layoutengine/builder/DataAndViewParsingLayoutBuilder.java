package com.flipkart.layoutengine.builder;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.provider.DataParsingAdapter;
import com.flipkart.layoutengine.provider.Provider;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A layout builder which can parse @data blocks before passing it on to {@link com.flipkart.layoutengine.builder.SimpleLayoutBuilder}
 */
public class DataAndViewParsingLayoutBuilder extends DataParsingLayoutBuilder {
    private final Provider viewProvider;

    DataAndViewParsingLayoutBuilder(Activity activity, Provider dataProvider, Provider viewProvider) {
        super(activity,dataProvider);
        this.viewProvider = viewProvider;
    }


    @Override
    protected View onUnknownViewEncountered(ParserContext context, String viewType, ViewGroup parent, JsonObject jsonObject) {
        JsonElement jsonElement = viewProvider.getObject(viewType);
        if(jsonElement!=null)
        {
            return buildImpl(context, parent,jsonElement.getAsJsonObject());
        }
        else {
            return super.onUnknownViewEncountered(context, viewType, parent, jsonObject);
        }
    }
}
