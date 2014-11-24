package com.flipkart.layoutengine.processor;

import com.flipkart.layoutengine.ParserContext;

/**
 * Created by kirankumar on 20/11/14.
 */
public abstract class AttributeProcessor<E> {
    public abstract void handle(ParserContext parserContext, String attributeKey, String attributeValue, E view);
}
