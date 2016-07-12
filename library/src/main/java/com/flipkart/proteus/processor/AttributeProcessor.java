package com.flipkart.proteus.processor;

import com.google.gson.JsonElement;

/**
 * @author kirankumar
 * @author aditya.sharat
 */
public abstract class AttributeProcessor<V> {

    public abstract void handle(String key, JsonElement value, V view);

}
