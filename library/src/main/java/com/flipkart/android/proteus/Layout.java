package com.flipkart.android.proteus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Nullable
    public final Map<String, String> scope;

    public Layout(@NonNull String type, @Nullable List<Attribute> attributes, @Nullable Map<String, String> scope) {
        this.type = type;
        this.attributes = attributes;
        this.scope = scope;
    }

    @Override
    Layout copy() {
        List<Attribute> attributes = null;
        if (this.attributes != null) {
            attributes = new ArrayList<>(this.attributes.size());
            for (Attribute attribute : this.attributes) {
                attributes.add(attribute.copy());
            }
        }
        return new Layout(type, attributes, scope);
    }
}
