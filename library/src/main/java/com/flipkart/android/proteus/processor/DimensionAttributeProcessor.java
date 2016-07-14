package com.flipkart.android.proteus.processor;


import android.view.View;

import com.flipkart.android.proteus.parser.ParseHelper;
import com.google.gson.JsonElement;

public abstract class DimensionAttributeProcessor<T extends View> extends AttributeProcessor<T> {

    /**
     * @param view View
     */
    @Override
    public final void handle(String key, JsonElement value, T view) {
        if (value != null && value.isJsonPrimitive()) {
            float dimension = ParseHelper.parseDimension(value.getAsString(), view.getContext());
            setDimension(dimension, view, key, value);
        }
    }

    /**
     * @param view  View
     * @param key   Attribute Key
     * @param value Attribute Value
     */
    public abstract void setDimension(float dimension, T view, String key, JsonElement value);
}
