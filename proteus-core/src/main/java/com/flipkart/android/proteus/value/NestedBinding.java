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

    private NestedBinding(Value value) {
        super("", new Expression[0]);
        this.value = value;
    }

    /**
     * @param value
     * @return
     */
    @NonNull
    public static NestedBinding valueOf(@NonNull final Value value) {
        return new NestedBinding(value);
    }

    @NonNull
    public Value getValue() {
        return value;
    }

    @Override
    public Expression getExpression(int index) {
        throw new UnsupportedOperationException("Cannot call getExpression() on a NestedBinding");
    }

    @Override
    public Value evaluate(Value data, int index) {
        return evaluate(value, data, index);
    }

    private Value evaluate(Binding binding, Value data, int index) {
        return binding.getAsBinding().evaluate(data, index);
    }

    private Value evaluate(ObjectValue object, Value data, int index) {
        ObjectValue evaluated = new ObjectValue();
        String key;
        Value value;
        for (Map.Entry<String, Value> entry : object.entrySet()) {
            key = entry.getKey();
            value = evaluate(entry.getValue(), data, index);
            evaluated.add(key, value);
        }

        return evaluated;
    }

    private Value evaluate(Array array, Value data, int index) {
        Array evaluated = new Array(array.size());
        Iterator<Value> iterator = array.iterator();
        while (iterator.hasNext()) {
            evaluated.add(evaluate(iterator.next(), data, index));
        }
        return evaluated;
    }

    private Value evaluate(Value value, Value data, int index) {
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
