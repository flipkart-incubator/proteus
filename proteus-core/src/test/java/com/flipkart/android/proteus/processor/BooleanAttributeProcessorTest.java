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

package com.flipkart.android.proteus.processor;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.View;

import com.flipkart.android.proteus.AttributeProcessor;
import com.flipkart.android.proteus.value.Null;
import com.flipkart.android.proteus.value.Primitive;
import com.flipkart.android.proteus.value.Value;

import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * BooleanAttributeProcessorTest
 *
 * @author aditya.sharat
 */
public class BooleanAttributeProcessorTest {

    @Mock
    private View view = mock(View.class);
    @Mock
    private Context context = mock(Context.class);
    @Mock
    private Resources.Theme theme = mock(Resources.Theme.class);
    @Mock
    private TypedArray typedArray = mock(TypedArray.class);

    @Test
    public void parse_true() throws Exception {
        Value True = new Primitive(true);
        AttributeProcessor processor = new BooleanAttributeProcessor() {
            @Override
            public void setBoolean(View view, boolean value) {

            }
        };

        Value value = processor.compile(True, null);
        assertThat(value, is((Value) BooleanAttributeProcessor.TRUE));
    }

    @Test
    public void parse_false() throws Exception {
        Value False = new Primitive(false);
        AttributeProcessor processor = new BooleanAttributeProcessor() {
            @Override
            public void setBoolean(View view, boolean value) {

            }
        };

        Value value = processor.compile(False, null);
        assertThat(value, is((Value) BooleanAttributeProcessor.FALSE));
    }

    @Test
    public void parse_truthy() throws Exception {
        Value Truthy = new Primitive("I'm true");
        AttributeProcessor processor = new BooleanAttributeProcessor() {
            @Override
            public void setBoolean(View view, boolean value) {

            }
        };

        Value value = processor.compile(Truthy, null);
        assertThat(value, is((Value) BooleanAttributeProcessor.TRUE));
    }

    @Test
    public void parse_falsy() throws Exception {
        Value Truthy = Null.INSTANCE;
        AttributeProcessor processor = new BooleanAttributeProcessor() {
            @Override
            public void setBoolean(View view, boolean value) {

            }
        };

        Value value = processor.compile(Truthy, null);
        assertThat(value, is((Value) BooleanAttributeProcessor.FALSE));
    }

    @Test
    public void handle_true() throws Exception {
        AttributeProcessor processor = new BooleanAttributeProcessor() {
            @Override
            public void setBoolean(View view, boolean value) {
                assertThat(value, is(true));
            }
        };

        Value value = processor.compile(new Primitive(true), null);
        processor.handleValue(null, value);
    }

    @Test
    public void handle_false() throws Exception {
        AttributeProcessor processor = new BooleanAttributeProcessor() {
            @Override
            public void setBoolean(View view, boolean value) {
                assertThat(value, is(false));
            }
        };

        Value value = processor.compile(new Primitive(false), null);
        processor.handleValue(null, value);
    }

    @Test
    public void handle_style() throws Exception {
        when(typedArray.getBoolean(anyInt(), anyBoolean())).thenReturn(true);
        when(theme.obtainStyledAttributes(anyInt(), new int[]{anyInt()})).thenReturn(typedArray);
        when(context.getTheme()).thenReturn(theme);
        when(view.getContext()).thenReturn(context);
        AttributeProcessor processor = new BooleanAttributeProcessor() {
            @Override
            public void setBoolean(View view, boolean value) {
                assertThat(value, is(true));
            }
        };

        Value value = processor.compile(new Primitive("?Theme_AppCompat:windowNoTitle"), null);
        processor.handleValue(view, value);
    }

}