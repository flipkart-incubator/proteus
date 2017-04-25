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

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * BindingTest
 *
 * @author aditya.sharat
 */
public class BindingTest {

    public static ObjectValue data() {
        ObjectValue object = new ObjectValue();

        ObjectValue a = new ObjectValue();
        ObjectValue b = new ObjectValue();
        Primitive c = new Primitive(10);
        Primitive d = new Primitive(true);

        b.add("c", c);
        b.add("d", d);
        a.add("b", b);
        object.add("a", a);

        return object;
    }

    @Test
    public void evaluate_simple() throws Exception {
        Binding binding = Binding.valueOf("@{a.b.c}", null, null);

        Value value = binding.evaluate(null, data(), 0);

        assertThat(value.getAsString(), is("10"));
    }

    @Test
    public void assign_value_binding_1() throws Exception {
        Binding.DataBinding binding = Binding.DataBinding.valueOf("a.b.c");
        ObjectValue data = data();
        Value value = new Primitive(1);

        binding.assign(value, data, 0);

        value = binding.evaluate(null, data, 0);

        assertThat(value.getAsString(), is("1"));

    }

    @Test
    public void assign_value_binding_2() throws Exception {
        Binding.DataBinding binding = Binding.DataBinding.valueOf("a.b.c.d");
        ObjectValue data = data();
        Value value = new Primitive(1);

        binding.assign(value, data, 0);

        value = binding.evaluate(null, data, 0);

        assertThat(value.getAsString(), is("1"));

    }

    @Test
    public void assign_value_binding_3() throws Exception {
        Binding.DataBinding binding = Binding.DataBinding.valueOf("a.x.y");
        ObjectValue data = data();
        Value value = new Primitive(1);

        binding.assign(value, data, 0);

        value = binding.evaluate(null, data, 0);

        assertThat(value.getAsString(), is("1"));

    }

    @Test
    public void assign_value_binding_4() throws Exception {
        Binding.DataBinding binding = Binding.DataBinding.valueOf("a.b[0]");
        ObjectValue data = data();
        Value value = new Primitive(1);

        binding.assign(value, data, 0);

        value = binding.evaluate(null, data, 0);

        assertThat(value.getAsString(), is("1"));

    }

    @Test
    public void assign_value_binding_5() throws Exception {
        Binding.DataBinding binding = Binding.DataBinding.valueOf("a.b[3]");
        ObjectValue data = data();
        Value value = new Primitive(1);

        binding.assign(value, data, 0);

        value = binding.evaluate(null, data, 0);

        assertThat(value.getAsString(), is("1"));

    }

    @Test
    public void assign_value_binding_6() throws Exception {
        Binding.DataBinding binding = Binding.DataBinding.valueOf("a.b[0].value");
        ObjectValue data = data();
        Value value = new Primitive(1);

        binding.assign(value, data, 0);

        value = binding.evaluate(null, data, 0);

        assertThat(value.getAsString(), is("1"));

    }

    @Test
    public void assign_value_binding_7() throws Exception {
        Binding.DataBinding binding = Binding.DataBinding.valueOf("a.b[5].value");
        ObjectValue data = data();
        Value value = new Primitive(1);

        binding.assign(value, data, 0);

        value = binding.evaluate(null, data, 0);

        assertThat(value.getAsString(), is("1"));

    }

    @Test
    public void assign_value_binding_8() throws Exception {
        Binding.DataBinding binding = Binding.DataBinding.valueOf("a.b[0]");
        ObjectValue data = data();
        ObjectValue value = new ObjectValue();
        value.add("x", new Primitive(true));
        value.add("y", new Primitive(10));

        binding.assign(value, data, 0);

        Value result = Binding.DataBinding.valueOf("a.b[0].x").evaluate(null, data, 0);

        assertThat(result.getAsString(), is("true"));

    }

    @Test
    public void assign_value_binding_9() throws Exception {
        Binding.DataBinding binding = Binding.DataBinding.valueOf("a.b[0].value");
        ObjectValue data = data();
        ObjectValue value = new ObjectValue();
        value.add("x", new Primitive(true));
        value.add("y", new Primitive(10));

        binding.assign(value, data, 0);

        Value result = Binding.DataBinding.valueOf("a.b[0].value.x").evaluate(null, data, 0);

        assertThat(result.getAsString(), is("true"));

    }

    @Test
    public void assign_value_binding_10() throws Exception {
        Binding.DataBinding binding = Binding.DataBinding.valueOf("a.b[0][1]");
        ObjectValue data = data();
        Value value = new Primitive(true);

        binding.assign(value, data, 0);

        Value result = Binding.DataBinding.valueOf("a.b[0][1]").evaluate(null, data, 0);

        assertThat(result.getAsString(), is("true"));

    }

    @Test
    public void assign_value_binding_11() throws Exception {
        Binding.DataBinding binding = Binding.DataBinding.valueOf("a.b[0][3].value");
        ObjectValue data = data();
        ObjectValue value = new ObjectValue();
        value.add("x", new Primitive(true));
        value.add("y", new Primitive(10));

        binding.assign(value, data, 0);

        Value result = Binding.DataBinding.valueOf("a.b[0][3].value.y").evaluate(null, data, 0);

        assertThat(result.getAsString(), is("10"));

    }

    @Test
    public void assign_value_binding_12() throws Exception {
        Binding.DataBinding binding = Binding.DataBinding.valueOf("a.b.f");
        ObjectValue data = data();
        ObjectValue value = new ObjectValue();
        value.add("x", new Primitive(true));
        value.add("y", new Primitive(10));

        binding.assign(value, data, 0);

        Value result = Binding.DataBinding.valueOf("a.b.f.y").evaluate(null, data, 0);

        assertThat(result.getAsString(), is("10"));

        result = Binding.DataBinding.valueOf("a.b.c").evaluate(null, data, 0);

        assertThat(result.getAsString(), is("10"));

    }

}