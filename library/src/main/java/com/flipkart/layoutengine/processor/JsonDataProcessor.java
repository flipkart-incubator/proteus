package com.flipkart.layoutengine.processor;

import com.flipkart.layoutengine.ParserContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 *
 */
public abstract class JsonDataProcessor<E> extends AttributeProcessor<E> {
    @Override
    abstract public void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue, E view, JsonObject layout);
}
