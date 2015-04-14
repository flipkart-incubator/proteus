package com.flipkart.layoutengine.processor;

import com.flipkart.layoutengine.ParserContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 *
 */
public abstract class JsonDataProcessor<E> extends AttributeProcessor<E> {
    @Override
    public void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue, E view) {
        handleData(parserContext, attributeKey, attributeValue, view);
    }

    public abstract void handleData(ParserContext parserContext, String attributeKey, Object attributeData, E view);
}
