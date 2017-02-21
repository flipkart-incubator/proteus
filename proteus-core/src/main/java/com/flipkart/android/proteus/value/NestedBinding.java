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

import com.google.gson.JsonElement;

import java.util.Iterator;
import java.util.Map;

/**
 * NestedBinding
 *
 * @author adityasharat
 */

public class NestedBinding extends Binding {

    public static final String NESTED_BINDING_KEY = "@";

    private final Value value;

    NestedBinding(Value value) {
        super("", new Expression[0]);
        this.value = value;
    }

    /**
     * @param object
     * @return
     */
    @NonNull
    public static NestedBinding valueOf(@NonNull final ObjectValue object) {
        ObjectValue compiled = new ObjectValue();
        for (Map.Entry<String, Value> entry : object.entrySet()) {
            compiled.add(entry.getKey(), valueOf(entry.getValue()));
        }
        return new NestedBinding(compiled);
    }

    /**
     * @param array
     * @return
     */
    @NonNull
    public static NestedBinding valueOf(@NonNull final Array array) {
        Array compiled = new Array(array.size());
        Iterator<Value> iterator = array.iterator();
        while (iterator.hasNext()) {
            compiled.add(valueOf(iterator.next()));
        }
        return new NestedBinding(compiled);
    }

    /**
     * @param value
     * @return
     */
    @NonNull
    public static NestedBinding valueOf(@NonNull final Value value) {
        Value compiled = value;
        if (value.isPrimitive()) {
            compiled = valueOf(value.getAsString());
        } else if (value.isObject()) {
            compiled = valueOf(value.getAsObject());
        } else if (value.isArray()) {
            compiled = valueOf(value.getAsArray());
        }
        return new NestedBinding(compiled);
    }

    @Override
    public Expression getExpression(int index) {
        throw new UnsupportedOperationException("Cannot call getExpression() on a NestedBinding");
    }

    @Override
    public Value evaluate(JsonElement data, int index) {
        return evaluate(value, data, index);
    }

    private Value evaluate(Binding binding, JsonElement data, int index) {
        return binding.getAsBinding().evaluate(data, index);
    }

    private Value evaluate(ObjectValue object, JsonElement data, int index) {
        ObjectValue evaluated = new ObjectValue();
        String key;
        Value value;
        for (Map.Entry<String, Value> entry : object.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            if (value.isBinding()) {
                value = value.getAsBinding().evaluate(data, index);
            }
            evaluated.add(key, value);
        }

        return evaluated;
    }

    private Value evaluate(Array array, JsonElement data, int index) {
        Array evaluated = new Array(array.size());
        Iterator<Value> iterator = array.iterator();
        while (iterator.hasNext()) {
            evaluated.add(evaluate(iterator.next(), data, index));
        }
        return evaluated;
    }

    private Value evaluate(Value value, JsonElement data, int index) {
        Value evaluated = value;
        if (value.isBinding()) {
            evaluated = evaluate(value.getAsBinding(), data, index);
        } else if (value.isObject()) {
            evaluated = evaluate(value.getAsObject(), data, index);
        } else if (value.isArray()) {
            evaluated = evaluate(value.getAsArray(), data, index);
        }
        return evaluated;
    }
}
