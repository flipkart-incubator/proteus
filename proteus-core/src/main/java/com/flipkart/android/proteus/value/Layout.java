/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 *
 * Copyright (c) 2017 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.flipkart.android.proteus.value;

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
    public final Map<String, Value> data;

    @Nullable
    public final ObjectValue extras;

    public Layout(@NonNull String type, @Nullable List<Attribute> attributes, @Nullable Map<String, Value> data, ObjectValue extras) {
        this.type = type;
        this.attributes = attributes;
        this.data = data;
        this.extras = extras;
    }

    @Override
    public Layout copy() {
        List<Attribute> attributes = null;
        if (this.attributes != null) {
            attributes = new ArrayList<>(this.attributes.size());
            for (Attribute attribute : this.attributes) {
                attributes.add(attribute.copy());
            }
        }

        return new Layout(type, attributes, data, extras);
    }

    public Layout merge(Layout include) {
        return this;
    }

    /**
     * Attribute
     *
     * @author aditya.sharat
     */
    public static class Attribute {

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
}
