package com.flipkart.layoutengine.processor;


import android.view.View;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class DimensionAttributeProcessor<T extends View> extends AttributeProcessor<T> {

    /**
     * @param parserContext  ParserContext
     * @param attributeKey   Attribute Key
     * @param attributeValue Attribute Value
     * @param view           View
     * @param proteusView    ProteusView
     * @param parent         Parent ProteusView
     * @param layout         Layout
     * @param index          index
     */
    @Override
    public final void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue, T view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
        if(attributeValue != null && attributeValue.isJsonPrimitive())
        {
            float dimension = ParseHelper.parseDimension(attributeValue.getAsString(), view.getContext());
            setDimension(parserContext, dimension, view, attributeKey, attributeValue, proteusView, layout, index);
        }
    }

    /**
     * @param parserContext  ParserContext
     * @param key   Attribute Key
     * @param value Attribute Value
     * @param view           View
     * @param proteusView    ProteusView
     * @param layout         Layout
     * @param index          index
     */
    public abstract void setDimension(ParserContext parserContext, float dimension, T view, String key, JsonElement value, ProteusView proteusView, JsonObject layout, int index);
}
