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
import android.view.View;

import com.flipkart.android.proteus.AttributeProcessor;
import com.flipkart.android.proteus.Primitive;
import com.flipkart.android.proteus.Value;

import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * DimensionAttributeProcessorTest
 *
 * @author aditya.sharat
 */
public class DimensionAttributeProcessorTest {

    @Mock
    private Context context = mock(Context.class);
    @Mock
    private Resources resources = mock(Resources.class);

    @Test
    public void parse() throws Exception {
        String input = "@dimen/padding";
        Primitive dimension = new Primitive("@dimen/padding");
        String name = "a.b.c";
        int resId = 1;

        when(resources.getIdentifier(input, "dimen", name)).thenReturn(resId);
        when(context.getResources()).thenReturn(resources);
        when(context.getPackageName()).thenReturn(name);

        AttributeProcessor processor = new DimensionAttributeProcessor() {
            @Override
            public void setDimension(View view, float dimension) {

            }
        };

        Value value = processor.parse(dimension, context);

        assertThat(value.getAsResource().resId, is(resId));
    }

}