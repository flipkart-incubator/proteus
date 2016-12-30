package com.flipkart.android.proteus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Array
 *
 * @author aditya.sharat
 */

public class Array extends Value {

    private final List<Value> values;

    /**
     * Creates an empty Array.
     */
    public Array() {
        values = new ArrayList<>();
    }

    @Override
    Array copy() {
        Array result = new Array();
        for (Value value : values) {
            result.add(value.copy());
        }
        return result;
    }

    /**
     * Adds the specified boolean to self.
     *
     * @param bool the boolean that needs to be added to the array.
     */
    public void add(Boolean bool) {
        values.add(bool == null ? Null.INSTANCE : new Primitive(bool));
    }

    /**
     * Adds the specified character to self.
     *
     * @param character the character that needs to be added to the array.
     */
    public void add(Character character) {
        values.add(character == null ? Null.INSTANCE : new Primitive(character));
    }

    /**
     * Adds the specified number to self.
     *
     * @param number the number that needs to be added to the array.
     */
    public void add(Number number) {
        values.add(number == null ? Null.INSTANCE : new Primitive(number));
    }

    /**
     * Adds the specified string to self.
     *
     * @param string the string that needs to be added to the array.
     */
    public void add(String string) {
        values.add(string == null ? Null.INSTANCE : new Primitive(string));
    }

    /**
     * Adds the specified value to self.
     *
     * @param value the value that needs to be added to the array.
     */
    public void add(Value value) {
        if (value == null) {
            value = Null.INSTANCE;
        }
        values.add(value);
    }

    /**
     * Adds all the values of the specified array to self.
     *
     * @param array the array whose values need to be added to the array.
     */
    public void addAll(Array array) {
        values.addAll(array.values);
    }

    /**
     * Replaces the value at the specified position in this array with the specified value.
     * value can be null.
     *
     * @param index index of the value to replace
     * @param value value to be stored at the specified position
     * @return the value previously at the specified position
     * @throws IndexOutOfBoundsException if the specified index is outside the array bounds
     */
    public Value set(int index, Value value) {
        return values.set(index, value);
    }

    /**
     * Removes the first occurrence of the specified value from this array, if it is present.
     * If the array does not contain the value, it is unchanged.
     *
     * @param value value to be removed from this array, if present
     * @return true if this array contained the specified value, false otherwise
     * @since 2.3
     */
    public boolean remove(Value value) {
        return values.remove(value);
    }

    /**
     * Removes the value at the specified position in this array. Shifts any subsequent values
     * to the left (subtracts one from their indices). Returns the value that was removed from
     * the array.
     *
     * @param index index the index of the value to be removed
     * @return the value previously at the specified position
     * @throws IndexOutOfBoundsException if the specified index is outside the array bounds
     * @since 2.3
     */
    public Value remove(int index) {
        return values.remove(index);
    }

    /**
     * Returns true if this array contains the specified value.
     *
     * @param value whose presence in this array is to be tested
     * @return true if this array contains the specified value.
     * @since 2.3
     */
    public boolean contains(Value value) {
        return values.contains(value);
    }

    /**
     * Returns the number of values in the array.
     *
     * @return the number of values in the array.
     */
    public int size() {
        return values.size();
    }

    /**
     * Returns an iterator to navigate the values of the array. Since the array is an ordered list,
     * the iterator navigates the values in the order they were inserted.
     *
     * @return an iterator to navigate the values of the array.
     */
    public Iterator<Value> iterator() {
        return values.iterator();
    }

    /**
     * Returns the ith value of the array.
     *
     * @param i the index of the value that is being sought.
     * @return the value present at the ith index.
     * @throws IndexOutOfBoundsException if i is negative or greater than or equal to the
     *                                   {@link #size()} of the array.
     */
    public Value get(int i) {
        return values.get(i);
    }

    /**
     * convenience method to get this array as a {@link String} if it contains a single value.
     *
     * @return get this value as a String if it is single value array.
     * @throws ClassCastException    if the value in the array is of not a {@link Primitive} and
     *                               is not a valid String.
     * @throws IllegalStateException if the array has more than one value.
     */
    @Override
    public String getAsString() {
        if (values.size() == 1) {
            return values.get(0).getAsString();
        }
        throw new IllegalStateException();
    }

    @Override
    public boolean equals(java.lang.Object o) {
        return (o == this) || (o instanceof Array && ((Array) o).values.equals(values));
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }
}
