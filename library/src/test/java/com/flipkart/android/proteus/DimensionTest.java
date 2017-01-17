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

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.ViewGroup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * DimensionTest
 *
 * @author aditya.sharat
 */
@RunWith(MockitoJUnitRunner.class)
public class DimensionTest {

    @Mock
    private Context context = mock(Context.class);
    @Mock
    private Resources resources = mock(Resources.class);
    @Mock
    private Resources.Theme theme = mock(Resources.Theme.class);
    @Mock
    private TypedArray typedArray = mock(TypedArray.class);

    @Before
    @After
    public void before() {
        reset(context);
        reset(resources);
        reset(resources, context);
    }

    @Test
    public void valueOf_Null() throws Exception {
        Dimension d = Dimension.valueOf(null, null);
        assertThat(d.value, is(0f));
        assertThat(d.unit, is(Dimension.DIMENSION_UNIT_PX));
    }

    @Test
    public void valueOf_MatchParent() throws Exception {
        Dimension d = Dimension.valueOf("match_parent", null);
        assertThat((int) d.value, is(ViewGroup.LayoutParams.MATCH_PARENT));
        assertThat(d.unit, is(Dimension.DIMENSION_UNIT_ENUM));
    }

    @Test
    public void valueOf_WrapContent() throws Exception {
        Dimension d;
        d = Dimension.valueOf("wrap_content", null);
        assertThat((int) d.value, is(ViewGroup.LayoutParams.WRAP_CONTENT));
        assertThat(d.unit, is(Dimension.DIMENSION_UNIT_ENUM));
    }

    @Test
    public void valueOf_Unit_Invalid() throws Exception {
        Dimension d = Dimension.valueOf("abcd", null);
        assertThat(d.value, is(0f));
        assertThat(d.unit, is(Dimension.DIMENSION_UNIT_PX));

        d = Dimension.valueOf("16q", null);
        assertThat(d.value, is(0f));
        assertThat(d.unit, is(Dimension.DIMENSION_UNIT_PX));

        d = Dimension.valueOf("16", null);
        assertThat(d.value, is(0f));
        assertThat(d.unit, is(Dimension.DIMENSION_UNIT_PX));

        d = Dimension.valueOf("1", null);
        assertThat(d.value, is(0f));
        assertThat(d.unit, is(Dimension.DIMENSION_UNIT_PX));
    }

    @Test
    public void valueOf_Unit_None() throws Exception {
        Dimension d = Dimension.valueOf("16", null);
        assertThat(d.value, is(0f));
        assertThat(d.unit, is(Dimension.DIMENSION_UNIT_PX));
    }

    @Test
    public void valueOf_Unit_PX() throws Exception {
        Dimension d = Dimension.valueOf("16px", null);
        assertThat(d.value, is(16f));
        assertThat(d.unit, is(Dimension.DIMENSION_UNIT_PX));
    }

    @Test
    public void valueOf_Unit_DP() throws Exception {
        Dimension d = Dimension.valueOf("16dp", null);
        assertThat(d.value, is(16f));
        assertThat(d.unit, is(Dimension.DIMENSION_UNIT_DP));
    }

    @Test
    public void valueOf_Unit_SP() throws Exception {
        Dimension d = Dimension.valueOf("16sp", null);
        assertThat(d.value, is(16f));
        assertThat(d.unit, is(Dimension.DIMENSION_UNIT_SP));
    }

    @Test
    public void valueOf_Unit_PT() throws Exception {
        Dimension d = Dimension.valueOf("16pt", null);
        assertThat(d.value, is(16f));
        assertThat(d.unit, is(Dimension.DIMENSION_UNIT_PT));
    }

    @Test
    public void valueOf_Unit_IN() throws Exception {
        Dimension d = Dimension.valueOf("16in", null);
        assertThat(d.value, is(16f));
        assertThat(d.unit, is(Dimension.DIMENSION_UNIT_IN));
    }

    @Test
    public void valueOf_Unit_MM() throws Exception {
        Dimension d = Dimension.valueOf("16mm", null);
        assertThat(d.value, is(16f));
        assertThat(d.unit, is(Dimension.DIMENSION_UNIT_MM));
    }

    @Test
    public void valueOf_Unit_Negative() throws Exception {
        Dimension d = Dimension.valueOf("-16dp", null);
        assertThat(d.value, is(-16f));
        assertThat(d.unit, is(Dimension.DIMENSION_UNIT_DP));
    }

    @Test
    public void valueOf_Unit_Resource() throws Exception {
        String input = "@dimen/padding";
        String name = "a.b.c";
        float resId = 1;

        when(resources.getIdentifier(input, "dimen", name)).thenReturn((int) resId);
        when(context.getResources()).thenReturn(resources);
        when(context.getPackageName()).thenReturn(name);

        Dimension d = Dimension.valueOf(input, context);
        assertThat(d.value, is(resId));
        assertThat(d.unit, is(Dimension.DIMENSION_UNIT_RESOURCE));
    }

    @Test
    public void valueOf_Unit_Resource_Exception() throws Exception {
        String input = "@dimen/undefined";

        //noinspection unchecked
        when(resources.getIdentifier(input, "dimen", context.getPackageName())).thenThrow(Exception.class);
        when(context.getResources()).thenReturn(resources);

        Dimension d = Dimension.valueOf(input, context);

        assertThat(d.value, is(0f));
        assertThat(d.unit, is(Dimension.DIMENSION_UNIT_PX));
    }

    @Test
    public void valueOf_Unit_Attribute_Exception() throws Exception {
        Dimension d = Dimension.valueOf("?undefined:undefined", context);
        assertThat(d.value, is(0f));
        assertThat(d.unit, is(Dimension.DIMENSION_UNIT_PX));
    }

    @Test
    public void copy() throws Exception {
        Dimension d = Dimension.valueOf("24dp", null);
        assertThat(d, is(d.copy()));
    }

    @Test
    public void cache() throws Exception {
        Dimension d1 = Dimension.valueOf("16dp", context);
        Dimension d2 = Dimension.valueOf("16dp", context);

        assertThat(d1, is(d2));
    }
}