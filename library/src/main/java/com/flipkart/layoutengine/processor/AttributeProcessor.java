package com.flipkart.layoutengine.processor;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author kirankumar
 * @author aditya.sharat
 */
public abstract class AttributeProcessor<E> {
    /**
     * @param parserContext  ParserContext
     * @param attributeValue Attribute Value
     * @param view           View
     * @param proteusView    ProteusView
     * @param parent         Parent ProteusView
     * @param layout         Layout
     * @param index
     */
    public abstract void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue,
                                E view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index);

}
