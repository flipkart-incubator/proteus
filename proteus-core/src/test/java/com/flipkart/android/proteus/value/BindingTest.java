/*
 * Copyright 2019 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.android.proteus.value;

import android.content.Context;

import com.flipkart.android.proteus.Proteus;
import com.flipkart.android.proteus.ProteusBuilder;
import com.flipkart.android.proteus.ProteusContext;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

/**
 * BindingTest
 *
 * @author aditya.sharat
 */
public class BindingTest {

  /*
   * {
   *   "a" : {
   *     "b": {
   *       "c": 10,
   *       "d": true
   *      }
   *   }
   *   "e": ["alpha", 2, false],
   *   "f": null
   * }
   *
   */
  public static ObjectValue data() {
    ObjectValue object = new ObjectValue();

    ObjectValue a = new ObjectValue();
    ObjectValue b = new ObjectValue();
    Primitive c = new Primitive(10);
    Primitive d = new Primitive(true);
    Array e = new Array();
    e.add(new Primitive("alpha"));
    e.add(new Primitive(2));
    e.add(new Primitive(false));

    b.add("c", c);
    b.add("d", d);
    a.add("b", b);
    object.add("a", a);
    object.add("e", e);
    object.add("f", Null.INSTANCE);
    object.add("g", new Array());

    return object;
  }

  public static ProteusContext context() {
    Proteus proteus = new ProteusBuilder().build();
    Context context = mock(Context.class);
    return proteus.createContextBuilder(context).build();
  }

  @Test
  public void is_binding_1() throws Exception {
    assertThat(Binding.isBindingValue("@{a.b.c}"), is(true));
  }

  @Test
  public void is_not_binding() throws Exception {
    assertThat(Binding.isBindingValue("a.b.c"), is(false));
    assertThat(Binding.isBindingValue("@{"), is(false));
    assertThat(Binding.isBindingValue("@}"), is(false));
  }

  @Test
  public void evaluate_not_binding() throws Exception {
    try {
      Binding.valueOf("abc", null, null);
    } catch (IllegalArgumentException e) {
      assertThat(true, is(true));
    }
  }

  @Test
  public void evaluate_binding() throws Exception {
    Binding binding = Binding.valueOf("@{a.b.c}", null, null);

    Value value = binding.evaluate(null, data(), 0);

    assertThat(value.getAsString(), is("10"));
  }

  @Test
  public void evaluate_function() throws Exception {
    ProteusContext context = context();
    Binding binding = Binding.valueOf("@{fn:add(1,1)}", context, context.getFunctionManager());

    Value value = binding.evaluate(null, data(), 0);

    assertThat(value.getAsString(), is("2.0"));
  }

  @Test
  public void to_string_1() throws Exception {
    String string = "@{a.b.c}";
    Binding binding = Binding.valueOf(string, null, null);

    assertThat(binding.toString(), is(string));
  }

  @Test
  public void to_string_2() throws Exception {
    ProteusContext context = context();
    String string = "@{fn:add('1','1')}";
    Binding binding = Binding.valueOf(string, context, context.getFunctionManager());

    assertThat(binding.toString(), is(string));
  }

  @Test
  public void copy() throws Exception {
    Binding binding = Binding.valueOf("@{a.b.c}", null, null);

    assertThat(binding, is(binding.copy()));
  }

  @Test
  public void evaluate_null_1() throws Exception {
    Binding binding = Binding.valueOf("@{a.b.c}", null, null);

    ObjectValue data = data();
    ((Binding.DataBinding) binding).assign(Null.INSTANCE, data, 0);

    Value value = binding.evaluate(null, data, 0);

    assertThat(value.toString(), is("NULL"));
  }

  @Test
  public void evaluate_null_2() throws Exception {
    Binding binding = Binding.valueOf("@{a.b.c.d}", null, null);

    Value value = binding.evaluate(null, data(), 0);

    assertThat(value.toString(), is("NULL"));
  }

  @Test
  public void evaluate_null_3() throws Exception {
    Binding binding = Binding.valueOf("@{a.b[0]}", null, null);

    Value value = binding.evaluate(null, data(), 0);

    assertThat(value.toString(), is("NULL"));
  }

  @Test
  public void evaluate_null_4() throws Exception {
    Binding binding = Binding.valueOf("@{e.a.b}", null, null);

    Value value = binding.evaluate(null, data(), 0);

    assertThat(value.toString(), is("NULL"));
  }

  @Test
  public void evaluate_null_5() throws Exception {
    Binding binding = Binding.valueOf("@{f.a.b}", null, null);

    Value value = binding.evaluate(null, data(), 0);

    assertThat(value.toString(), is("NULL"));
  }

  @Test
  public void evaluate_index() throws Exception {
    Binding binding = Binding.valueOf("@{$index}", null, null);

    Value value = binding.evaluate(null, data(), 0);

    assertThat(value.toString(), is("0"));
  }

  @Test
  public void evaluate_array_index() throws Exception {
    Binding binding = Binding.valueOf("@{e[$index]}", null, null);

    Value value = binding.evaluate(null, data(), 1);

    assertThat(value.toString(), is("2"));
  }

  @Test
  public void evaluate_array_invalid_index_1() throws Exception {
    Binding binding = Binding.valueOf("@{e[8]}", null, null);

    Value value = binding.evaluate(null, data(), 1);

    assertThat(value.toString(), is("NULL"));
  }

  @Test
  public void evaluate_array_invalid_index_2() throws Exception {
    Binding binding = Binding.valueOf("@{e[$index]}", null, null);

    Value value = binding.evaluate(null, data(), 8);

    assertThat(value.toString(), is("NULL"));
  }

  @Test
  public void evaluate_array_length() throws Exception {
    Binding binding = Binding.valueOf("@{e[$length]}", null, null);

    Value value = binding.evaluate(null, data(), 1);

    assertThat(value.toString(), is("3"));
  }

  @Test
  public void evaluate_array_last_1() throws Exception {
    Binding binding = Binding.valueOf("@{e[$last]}", null, null);

    Value value = binding.evaluate(null, data(), 1);

    assertThat(value.toString(), is("false"));
  }

  @Test
  public void evaluate_array_last_2() throws Exception {
    Binding binding = Binding.valueOf("@{g[$last]}", null, null);

    Value value = binding.evaluate(null, data(), 1);

    assertThat(value.toString(), is("NULL"));
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

  @Test
  public void assign_value_binding_13() throws Exception {
    Binding.DataBinding binding = Binding.DataBinding.valueOf("a.b[x]");
    ObjectValue data = data();
    Value value = new Primitive(1);

    binding.assign(value, data, 0);

    value = binding.evaluate(null, data, 0);

    assertThat(value.toString(), is("NULL"));

  }

  @Test
  public void assign_value_binding_14() throws Exception {
    Binding.DataBinding binding = Binding.DataBinding.valueOf("a.b[x].y");
    ObjectValue data = data();
    Value value = new Primitive(1);

    binding.assign(value, data, 0);

    value = binding.evaluate(null, data, 0);

    assertThat(value.toString(), is("NULL"));

  }

}