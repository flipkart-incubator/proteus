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

package com.flipkart.android.proteus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;

import com.flipkart.android.proteus.toolbox.Formatter;

import java.util.HashMap;
import java.util.Map;

/**
 * Proteus
 *
 * @author aditya.sharat
 */

public final class Proteus {

    @NonNull
    private final LayoutInflaterFactory factory;
    @NonNull
    private final Map<String, Type> types;

    Proteus(@NonNull Map<String, Type> types, @NonNull Map<String, Formatter> formatters) {
        this.types = types;
        this.factory = new LayoutInflaterFactory(map(types), formatters);
    }

    public boolean has(@NonNull @Size(min = 1) String type) {
        return types.containsKey(type);
    }

    @Nullable
    public ViewTypeParser.AttributeSet.Attribute getAttributeId(@NonNull @Size(min = 1) String name, @NonNull @Size(min = 1) String type) {
        return types.get(type).getAttributeId(name);
    }

    private Map<String, ViewTypeParser> map(Map<String, Type> types) {
        Map<String, ViewTypeParser> parsers = new HashMap<>(types.size());
        for (Map.Entry<String, Type> entry : types.entrySet()) {
            parsers.put(entry.getKey(), entry.getValue().parser);
        }
        return parsers;
    }

    @NonNull
    public LayoutInflaterFactory getFactory() {
        return factory;
    }

    static class Type {

        public final int id;
        public final String type;
        public final ViewTypeParser parser;

        private final ViewTypeParser.AttributeSet attributes;

        Type(int id, @NonNull String type, @NonNull ViewTypeParser parser, @NonNull ViewTypeParser.AttributeSet attributes) {
            this.id = id;
            this.type = type;
            this.parser = parser;
            this.attributes = attributes;
        }

        @Nullable
        public ViewTypeParser.AttributeSet.Attribute getAttributeId(String name) {
            return attributes.getAttribute(name);
        }
    }
}
