package com.flipkart.android.proteus;

/**
 * Attribute
 *
 * @author aditya.sharat
 */

public class Attribute {
    public final int id;
    public final Value value;

    public Attribute(int id, Value value) {
        this.id = id;
        this.value = value;
    }

    protected Attribute copy() {
        return new Attribute(id, value.copy());
    }
}
