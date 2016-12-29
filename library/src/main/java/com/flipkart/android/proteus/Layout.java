package com.flipkart.android.proteus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Layout
 *
 * @author aditya.sharat
 */

public class Layout extends Value {

    @NonNull
    public final String type;

    @Nullable
    public final List<Attribute> attributes;

    public Layout(@NonNull String type, @Nullable List<Attribute> attributes) {
        this.type = type;
        this.attributes = attributes;
    }

    @Override
    Layout copy() {
        List<Attribute> attributesCopy = null;
        if (attributes != null) {
            attributesCopy = new ArrayList<>(attributes.size());
        }
        return new Layout(type, attributesCopy);
    }
}
