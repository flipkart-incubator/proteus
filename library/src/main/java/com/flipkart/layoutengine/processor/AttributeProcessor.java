package com.flipkart.layoutengine.processor;

import com.flipkart.layoutengine.ParserContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by kirankumar on 01/12/14.
 */
public abstract class AttributeProcessor<E> {
    /**
     * @param parserContext
     * @param attributeKey
     * @param attributeValue
     * @param view
     * @param layout
     */
    public abstract void handle(ParserContext parserContext, String attributeKey,
                                JsonElement attributeValue, E view, JsonObject layout);

}
