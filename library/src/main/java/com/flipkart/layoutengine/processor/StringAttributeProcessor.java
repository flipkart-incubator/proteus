package com.flipkart.layoutengine.processor;

import com.flipkart.layoutengine.ParserContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by kirankumar on 20/11/14.
 */
public abstract class StringAttributeProcessor<E> extends AttributeProcessor<E> {
    /**
     * @param parserContext
     * @param attributeKey   Attribute Key
     * @param attributeValue Attribute Value
     * @param view           View
     * @param layout         Layout
     */
    @Override
    public void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue, E view, JsonObject layout) {
        if (attributeValue.isJsonPrimitive()) {
            handle(parserContext, attributeKey, attributeValue.getAsString(), view, layout);
        } else {
            handle(parserContext, attributeKey, attributeValue.toString(), view, layout);
        }
    }

    /**
     * @param parserContext
     * @param attributeKey   Attribute Key
     * @param attributeValue Attribute Value
     * @param view           View
     * @param layout         Layout
     */
    public abstract void handle(ParserContext parserContext, String attributeKey, String attributeValue, E view, JsonObject layout);

}
