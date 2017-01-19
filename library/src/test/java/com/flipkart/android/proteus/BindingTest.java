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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * BindingTest
 *
 * @author aditya.sharat
 */
public class BindingTest {

    public static JsonObject data() {
        JsonObject object = new JsonObject();

        JsonObject a = new JsonObject();
        JsonObject b = new JsonObject();
        JsonElement c = new JsonPrimitive(10);
        JsonElement d = new JsonPrimitive(true);

        b.add("c", c);
        b.add("d", d);
        a.add("b", b);
        object.add("a", a);

        return object;
    }

    @Test
    public void valueOf_cache() throws Exception {
        Binding binding1 = Binding.valueOf("@{a.b.c}");
        Binding binding2 = Binding.valueOf("@{a.b.c}");

        assertThat(binding1, is(binding2));
    }

    @Test
    public void evaluate_single() throws Exception {
        Binding binding = Binding.valueOf("@{a.b.c}");

        Value value = binding.evaluate(data(), 0);

        assertThat(value.getAsString(), is("10"));

    }

    @Test
    public void evaluate_multiple() throws Exception {
        Binding binding = Binding.valueOf("@{a.b.c} and @{a.b.d}");

        Value value = binding.evaluate(data(), 0);

        assertThat(value.getAsString(), is("10 and true"));

    }

}