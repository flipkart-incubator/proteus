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

import com.flipkart.android.proteus.toolbox.Formatter;

import java.util.HashMap;
import java.util.Map;

/**
 * Proteus
 *
 * @author aditya.sharat
 */

public class Proteus {

    public final LayoutInflaterFactory factory;
    private final Map<String, Type> types;

    Proteus(Map<String, Type> types, Map<String, Formatter> formatters) {
        this.types = types;
        this.factory = new LayoutInflaterFactory(map(types), formatters);
    }

    public boolean has(String type) {
        return types.containsKey(type);
    }

    public int getAttributeId(String name, String type) {
        return types.get(type).getAttributeId(name);
    }

    static class Type {

        public final int id;
        public final String type;
        public final TypeParser parser;

        private final TypeParser.AttributeSet attributes;

        Type(int id, @NonNull String type, @NonNull TypeParser parser, @NonNull TypeParser.AttributeSet attributes) {
            this.id = id;
            this.type = type;
            this.parser = parser;
            this.attributes = attributes;
        }

        public int getAttributeId(String name) {
            return attributes.getAttributeId(name);
        }
    }

    private Map<String, TypeParser> map(Map<String, Type> types) {
        Map<String, TypeParser> parsers = new HashMap<>();
        for (Map.Entry<String, Type> entry : types.entrySet()) {
            parsers.put(entry.getKey(), entry.getValue().parser);
        }
        return parsers;
    }
}
