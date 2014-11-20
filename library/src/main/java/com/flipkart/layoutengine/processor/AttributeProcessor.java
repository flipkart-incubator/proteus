package com.flipkart.layoutengine.processor;

/**
 * Created by kirankumar on 20/11/14.
 */
public abstract class AttributeProcessor<E> {
    public abstract void handle(String attributeKey,String attributeValue, E view);
}
