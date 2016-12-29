package com.flipkart.android.proteus;

/**
 * Null
 *
 * @author aditya.sharat
 */

public class Null extends Value {
    /**
     * singleton for JsonNull
     *
     * @since 1.8
     */
    public static final Null INSTANCE = new Null();

    @Override
    Null copy() {
        return INSTANCE;
    }

    /**
     * All instances of Null have the same hash code
     * since they are indistinguishable
     */
    @Override
    public int hashCode() {
        return Null.class.hashCode();
    }

    /**
     * All instances of JsonNull are the same
     */
    @Override
    public boolean equals(java.lang.Object other) {
        return this == other || other instanceof Null;
    }
}
