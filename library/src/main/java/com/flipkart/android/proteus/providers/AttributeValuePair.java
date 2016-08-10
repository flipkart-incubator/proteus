package com.flipkart.android.proteus.providers;

import com.google.gson.JsonElement;

public class AttributeValuePair {
    JsonElement value;
    String attribute;

    public AttributeValuePair(JsonElement value, String attribute) {
        this.value = value;
        this.attribute = attribute;
    }

    public JsonElement getValue() {
        return value;
    }

    public void setValue(JsonElement value) {
        this.value = value;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}