package com.flipkart.layoutengine.builder;

import android.os.Parcelable;

public interface IdGenerator extends Parcelable {
    /**
     * Generates and returns a unique id, for the given key.
     * If key exists, returns old value.
     * Ensure that all
     *
     * @param idKey
     * @return a unique ID integer for use with {@link android.view.View#setId(int)}.
     */
    int getUnique(String idKey);
}
