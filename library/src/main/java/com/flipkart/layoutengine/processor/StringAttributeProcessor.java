package com.flipkart.layoutengine.processor;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.builder.LayoutBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by kirankumar on 20/11/14.
 */
public abstract class StringAttributeProcessor<E> extends AttributeProcessor<E> {
    /**
     *  @param parserContext
     * @param layoutBuilder
     * @param attributeKey
     * @param attributeValue
     * @param view
     * @param layout
     */
    @Override
    public void handle(ParserContext parserContext, LayoutBuilder layoutBuilder, String attributeKey, JsonElement attributeValue, E view, JsonObject layout) {
        if (attributeValue.isJsonPrimitive()) {
            handle(parserContext, attributeKey, attributeValue.getAsString(), view);
        } else {
            handle(parserContext, attributeKey, attributeValue.toString(), view);
        }
    }

    /**
     * @param parserContext
     * @param attributeKey
     * @param attributeValue
     * @param view
     */
    public abstract void handle(ParserContext parserContext, String attributeKey, String attributeValue, E view);

}
