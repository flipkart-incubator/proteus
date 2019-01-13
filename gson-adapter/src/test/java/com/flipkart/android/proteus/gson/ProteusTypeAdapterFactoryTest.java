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

package com.flipkart.android.proteus.gson;

import android.content.Context;

import com.flipkart.android.proteus.Proteus;
import com.flipkart.android.proteus.ProteusBuilder;
import com.flipkart.android.proteus.value.Color;
import com.flipkart.android.proteus.value.ObjectValue;
import com.flipkart.android.proteus.value.Primitive;
import com.flipkart.android.proteus.value.Value;
import com.google.gson.TypeAdapter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * ProteusTypeAdapterFactoryTest
 *
 * @author adityasharat
 */
public class ProteusTypeAdapterFactoryTest {

  @Mock
  Context context = mock(Context.class);

  private TypeAdapter<Value> adapter;
  private Proteus proteus;

  @Before
  public void before() {
    ProteusTypeAdapterFactory factory = new ProteusTypeAdapterFactory(context);
    proteus = new ProteusBuilder().build();
    ProteusTypeAdapterFactory.PROTEUS_INSTANCE_HOLDER.setProteus(proteus);
    adapter = factory.COMPILED_VALUE_TYPE_ADAPTER;
  }

  @Test
  public void primitive() throws IOException {
    Primitive in = new Primitive("this is a string");
    String json = adapter.toJson(in);
    Value out = adapter.fromJson(json);
    assertThat(in, is(out));
  }

  @Test
  public void object() throws IOException {

    ObjectValue in = new ObjectValue();
    in.add("a", new Primitive(1));
    in.add("b", new Primitive(2));
    //in.add("c", new Array(new Value[]{new Primitive(3), Color.valueOf("#545454")}));

    ObjectValue nested = new ObjectValue();
    //nested.add("d", AttributeResource.valueOf(123));
    //nested.add("e", Binding.valueOf("@{a.b.c}", proteus.functions));
    //nested.add("f", Binding.valueOf("@{x.y.z}${number()}", proteus.functions));
    //nested.add("g", Binding.valueOf("date is @{a.b.c}${date('E, MM yyyy')}", proteus.functions));
    in.add("z", nested);

    String json = adapter.toJson(in);

    Value out = adapter.fromJson(json);

    assertThat(1, is(1));
  }

  @Test
  public void color_state_list() throws IOException {
    int[][] states = new int[][]{
      new int[]{1, 2, 3},
      new int[]{4, 5, 6}
    };
    int[] colors = new int[]{7, 8, 9};

    Value in = Color.StateList.valueOf(states, colors);

    String json = adapter.toJson(in);

    Value out = adapter.fromJson(json);

    assertThat(1, is(1));
  }

}