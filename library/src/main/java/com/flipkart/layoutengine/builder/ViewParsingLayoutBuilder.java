package com.flipkart.layoutengine.builder;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.provider.Provider;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A layout builder which can parse view blocks before passing it on to {@link SimpleLayoutBuilder}
 */
public class ViewParsingLayoutBuilder extends SimpleLayoutBuilder {

    private Provider viewProvider;

    public ViewParsingLayoutBuilder(Activity activity, Provider viewProvider) {
        super(activity);
        this.viewProvider = viewProvider;
    }

    @Override
    protected View onUnknownViewEncountered(ParserContext context, String viewType, ViewGroup parent, JsonObject jsonObject, int index) {
        JsonElement jsonElement = viewProvider.getObject(viewType, index);
        if(jsonElement!=null)
        {
            return buildImpl(context, parent,jsonElement.getAsJsonObject(), null , index);
        }
        else {
            return super.onUnknownViewEncountered(context, viewType, parent, jsonObject, index);
        }
    }
}
