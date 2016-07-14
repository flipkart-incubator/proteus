package com.flipkart.android.proteus.binding;

/**
 * @author kirankumar
 * @author aditya.sharat
 */
public class Binding {

    private String bindingName;
    private String attributeKey;
    private String attributeValue;
    private boolean hasRegEx;

    public Binding(String bindingName, String attributeKey, String attributeValue, boolean hasRegEx) {
        this.bindingName = bindingName;
        this.attributeKey = attributeKey;
        this.attributeValue = attributeValue;
        this.hasRegEx = hasRegEx;
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
