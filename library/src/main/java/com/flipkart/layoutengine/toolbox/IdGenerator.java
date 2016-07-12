package com.flipkart.layoutengine.toolbox;

import android.os.Parcelable;

/**
 * IdGeneratorImpl
 * <p>
 * Simulates the R class. Useful to given unique ID for use in {@link android.view.View#setId(int)} method.
 * An ID which doesn't conflict with aapt's ID is ensured. Please ensure that all dynamic ID call go through
 * this class to ensure uniqueness with other dynamic IDs.
 * </p>
 *
 * @author aditya.sharat
 */
public interface IdGenerator extends Parcelable {
    /**
     * Generates and returns a unique id, for the given key.
     * If key exists, returns old value.
     * Ensure that all
     *
     * @param id the value for which the ID is returns.
     * @return a unique ID integer for use with {@link android.view.View#setId(int)}.
     */
    int getUnique(String id);
}
