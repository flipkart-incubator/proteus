package com.flipkart.layoutengine.processor;


import android.view.View;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonObject;

public abstract class DimensionAttributeProcessor<T extends View> extends StringAttributeProcessor<T> {

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
    public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
        int dimension = ParseHelper.parseDimension(attributeValue, view.getContext());
        setDimension(view, attributeKey, dimension);
    }

    public abstract void setDimension(T view, String attributeKey, int dimension);
}
