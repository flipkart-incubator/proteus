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
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * DimensionTest
 *
 * @author aditya.sharat
 */
public class DimensionTest {

  private static Context context() {
    DisplayMetrics metric = new DisplayMetrics();
    Context context = mock(Context.class);
    Resources resources = mock(Resources.class);
    when(context.getResources()).thenReturn(resources);
    when(resources.getDisplayMetrics()).thenReturn(metric);
    return context;
  }

  @Test
  public void valueOf_Null() throws Exception {
    Dimension d = Dimension.valueOf(null);
    assertThat(d.value, is(0d));
    assertThat(d.unit, is(Dimension.DIMENSION_UNIT_PX));
  }

  @Test
  public void valueOf_MatchParent() throws Exception {
    Dimension d = Dimension.valueOf("match_parent");
    assertThat((int) d.value, is(ViewGroup.LayoutParams.MATCH_PARENT));
    assertThat(d.unit, is(Dimension.DIMENSION_UNIT_ENUM));
  }

  @Test
  public void valueOf_WrapContent() throws Exception {
    Dimension d;
    d = Dimension.valueOf("wrap_content");
    assertThat((int) d.value, is(ViewGroup.LayoutParams.WRAP_CONTENT));
    assertThat(d.unit, is(Dimension.DIMENSION_UNIT_ENUM));
  }

  @Test
  public void valueOf_Unit_Invalid() throws Exception {
    Dimension d = Dimension.valueOf("abcd");
    assertThat(d.value, is(0d));
    assertThat(d.unit, is(Dimension.DIMENSION_UNIT_PX));

    d = Dimension.valueOf("16q");
    assertThat(d.value, is(0d));
    assertThat(d.unit, is(Dimension.DIMENSION_UNIT_PX));

    d = Dimension.valueOf("16");
    assertThat(d.value, is(0d));
    assertThat(d.unit, is(Dimension.DIMENSION_UNIT_PX));

    d = Dimension.valueOf("1");
    assertThat(d.value, is(0d));
    assertThat(d.unit, is(Dimension.DIMENSION_UNIT_PX));
  }

  @Test
  public void valueOf_Unit_None() throws Exception {
    Dimension d = Dimension.valueOf("16");
    assertThat(d.value, is(0d));
    assertThat(d.unit, is(Dimension.DIMENSION_UNIT_PX));
  }

  @Test
  public void valueOf_Unit_PX() throws Exception {
    Dimension d = Dimension.valueOf("16px");
    assertThat(d.value, is(16d));
    assertThat(d.unit, is(Dimension.DIMENSION_UNIT_PX));
  }

  @Test
  public void valueOf_Unit_DP() throws Exception {
    Dimension d = Dimension.valueOf("16dp");
    assertThat(d.value, is(16d));
    assertThat(d.unit, is(Dimension.DIMENSION_UNIT_DP));
  }

  @Test
  public void valueOf_Unit_SP() throws Exception {
    Dimension d = Dimension.valueOf("16sp");
    assertThat(d.value, is(16d));
    assertThat(d.unit, is(Dimension.DIMENSION_UNIT_SP));
  }

  @Test
  public void valueOf_Unit_PT() throws Exception {
    Dimension d = Dimension.valueOf("16pt");
    assertThat(d.value, is(16d));
    assertThat(d.unit, is(Dimension.DIMENSION_UNIT_PT));
  }

  @Test
  public void valueOf_Unit_IN() throws Exception {
    Dimension d = Dimension.valueOf("16in");
    assertThat(d.value, is(16d));
    assertThat(d.unit, is(Dimension.DIMENSION_UNIT_IN));
  }

  @Test
  public void valueOf_Unit_MM() throws Exception {
    Dimension d = Dimension.valueOf("16mm");
    assertThat(d.value, is(16d));
    assertThat(d.unit, is(Dimension.DIMENSION_UNIT_MM));
  }

  @Test
  public void valueOf_Unit_Negative() throws Exception {
    Dimension d = Dimension.valueOf("-16dp");
    assertThat(d.value, is(-16d));
    assertThat(d.unit, is(Dimension.DIMENSION_UNIT_DP));
  }

  @Test
  public void copy() throws Exception {
    Dimension d = Dimension.valueOf("24dp");
    assertThat(d, is(d.copy()));
  }

  @Test
  public void cache() throws Exception {
    Dimension d1 = Dimension.valueOf("16dp");
    Dimension d2 = Dimension.valueOf("16dp");

    assertThat(d1, is(d2));
  }

  @Test
  public void to_string() throws Exception {
    String string = "16dp";
    Dimension dimension = Dimension.valueOf(string);

    assertThat(dimension.toString(), is(string));

    string = "16.5dp";
    dimension = Dimension.valueOf(string);

    assertThat(dimension.toString(), is(string));

    string = "match_parent";
    dimension = Dimension.valueOf(string);

    assertThat(dimension.toString(), is(string));
  }

  @Test
  public void apply() throws Exception {
    Dimension dimension = Dimension.valueOf("24dp");
    float result = dimension.apply(context());
    assertThat(result, is(24f));

    dimension = Dimension.valueOf("match_parent");
    result = dimension.apply(context());
    assertThat(result, is(-1f));

    result = Dimension.apply("24dp", context());
    assertThat(result, is(24f));
  }
}