package com.flipkart.layoutengine.binding;

import com.flipkart.layoutengine.parser.LayoutHandler;

/**
 * @author kirankumar
 */
public class Binding {
    private LayoutHandler layoutHandler;
    private String bindingName;
    private String attributeKey;
    private String attributeValue;
    private boolean hasRegEx;

    public Binding(LayoutHandler layoutHandler, String bindingName, String attributeKey,
                   String attributeValue, boolean hasRegEx) {

        this.layoutHandler = layoutHandler;
        this.bindingName = bindingName;
        this.attributeKey = attributeKey;
        this.attributeValue = attributeValue;
        this.hasRegEx = hasRegEx;
    }

    public LayoutHandler getLayoutHandler() {
        return this.layoutHandler;
    }

    public String getAttributeKey() {
        return attributeKey;
    }

    public void setAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getBindingName() {
        return bindingName;
    }

    public boolean hasRegEx() {
        return hasRegEx;
    }

    public void hasRegEx(boolean hasRegEx) {
        this.hasRegEx = hasRegEx;
    }
}
