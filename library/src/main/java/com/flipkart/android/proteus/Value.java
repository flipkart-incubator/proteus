package com.flipkart.android.proteus;

/**
 * Value
 *
 * @author aditya.sharat
 */

public abstract class Value {

    /**
     * Returns a deep copy of this value. Immutable elements
     * like primitives and nulls are not copied.
     */
    abstract Value copy();

    /**
     * provides check for verifying if this value is an array or not.
     *
     * @return true if this value is of type {@link Array}, false otherwise.
     */
    public boolean isArray() {
        return this instanceof Array;
    }

    /**
     * provides check for verifying if this value is a object or not.
     *
     * @return true if this value is of type {@link Object}, false otherwise.
     */
    public boolean isObject() {
        return this instanceof Object;
    }

    /**
     * provides check for verifying if this value is a primitive or not.
     *
     * @return true if this value is of type {@link Primitive}, false otherwise.
     */
    public boolean isPrimitive() {
        return this instanceof Primitive;
    }

    /**
     * provides check for verifying if this value represents a null value or not.
     *
     * @return true if this value is of type {@link Null}, false otherwise.
     * @since 1.2
     */
    public boolean isNull() {
        return this instanceof Null;
    }

    /**
     * convenience method to get this value as a {@link Object}. If the value is of some
     * other type, a {@link IllegalStateException} will result. Hence it is best to use this method
     * after ensuring that this value is of the desired type by calling {@link #isObject()}
     * first.
     *
     * @return get this value as a {@link Object}.
     * @throws IllegalStateException if the value is of another type.
     */
    public Object getAsObject() {
        if (isObject()) {
            return (Object) this;
        }
        throw new IllegalStateException("Not an Object: " + this);
    }

    /**
     * convenience method to get this value as a {@link Array}. If the value is of some
     * other type, a {@link IllegalStateException} will result. Hence it is best to use this method
     * after ensuring that this value is of the desired type by calling {@link #isArray()}
     * first.
     *
     * @return get this value as a {@link Array}.
     * @throws IllegalStateException if the value is of another type.
     */
    public Array getAsArray() {
        if (isArray()) {
            return (Array) this;
        }
        throw new IllegalStateException("This is not a Array.");
    }

    /**
     * convenience method to get this value as a {@link Primitive}. If the value is of some
     * other type, a {@link IllegalStateException} will result. Hence it is best to use this method
     * after ensuring that this value is of the desired type by calling {@link #isPrimitive()}
     * first.
     *
     * @return get this value as a {@link Primitive}.
     * @throws IllegalStateException if the value is of another type.
     */
    public Primitive getAsPrimitive() {
        if (isPrimitive()) {
            return (Primitive) this;
        }
        throw new IllegalStateException("This is not a Primitive.");
    }

    /**
     * convenience method to get this value as a {@link Null}. If the value is of some
     * other type, a {@link IllegalStateException} will result. Hence it is best to use this method
     * after ensuring that this value is of the desired type by calling {@link #isNull()}
     * first.
     *
     * @return get this value as a {@link Null}.
     * @throws IllegalStateException if the value is of another type.
     * @since 1.2
     */
    public Null getAsNull() {
        if (isNull()) {
            return (Null) this;
        }
        throw new IllegalStateException("This is not a Null.");
    }

    /**
     * convenience method to get this value as a boolean value.
     *
     * @return get this value as a primitive boolean value.
     * @throws ClassCastException if the value is of not a {@link Primitive} and is not a valid
     *                            boolean value.
     */
    public boolean getAsBoolean() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this value as a string value.
     *
     * @return get this value as a string value.
     * @throws ClassCastException if the value is of not a {@link Primitive} and is not a valid
     *                            string value.
     */
    public String getAsString() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this value as a primitive double value.
     *
     * @return get this value as a primitive double value.
     * @throws ClassCastException if the value is of not a {@link Primitive} and is not a valid
     *                            double value.
     */
    public double getAsDouble() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this value as a primitive float value.
     *
     * @return get this value as a primitive float value.
     * @throws ClassCastException if the value is of not a {@link Primitive} and is not a valid
     *                            float value.
     */
    public float getAsFloat() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this value as a primitive long value.
     *
     * @return get this value as a primitive long value.
     * @throws ClassCastException if the value is of not a {@link Primitive} and is not a valid
     *                            long value.
     */
    public long getAsLong() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this value as a primitive integer value.
     *
     * @return get this value as a primitive integer value.
     * @throws ClassCastException if the value is of not a {@link Primitive} and is not a valid
     *                            integer value.
     */
    public int getAsInt() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this value as a primitive character value.
     *
     * @return get this value as a primitive char value.
     * @throws ClassCastException if the value is of not a {@link Primitive} and is not a valid
     *                            char value.
     */
    public char getAsCharacter() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

}
