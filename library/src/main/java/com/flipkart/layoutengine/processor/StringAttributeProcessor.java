package com.flipkart.layoutengine.processor;

import com.flipkart.layoutengine.ParserContext;
import com.google.gson.JsonElement;

/**
 * Created by kirankumar on 20/11/14.
 */
public abstract class StringAttributeProcessor<E> extends AttributeProcessor<E> {
    @Override
    public void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue, E view) {
       handle(parserContext,attributeKey,attributeValue.getAsString(),view);
    }
    public abstract void handle(ParserContext parserContext, String attributeKey, String attributeValue, E view);

}
