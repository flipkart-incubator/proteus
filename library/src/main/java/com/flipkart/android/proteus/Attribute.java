package com.flipkart.android.proteus;

/**
 * Attribute
 *
 * @author aditya.sharat
 */

public class Attribute {
    public final int attribute;
    public final Value value;

    public Attribute(int attribute, Value value) {
        this.attribute = attribute;
        this.value = value;
    }

    protected Layout.Attribute copy() {
        return new Layout.Attribute(attribute, value.copy());
    }
}
