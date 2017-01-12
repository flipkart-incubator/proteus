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

import android.content.res.ColorStateList;
import android.view.View;
import android.webkit.ValueCallback;

import com.flipkart.android.proteus.AttributeProcessor;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.toolbox.ColorUtils;

public abstract class ColorResourceProcessor<V extends View> extends AttributeProcessor<V> {

    public ColorResourceProcessor() {

    }
    @Override
    public void handle(final V view, Value value) {
        ColorUtils.loadColor(view.getContext(), value, new ValueCallback<Integer>() {
            /**
             * Invoked when the value is available.
             *
             * @param color The value.
             */
            @Override
            public void onReceiveValue(Integer color) {
                setColor(view, color);
            }
        }, new ValueCallback<ColorStateList>() {
            /**
             * Invoked when the value is available.
             *
             * @param color The value.
             */
            @Override
            public void onReceiveValue(ColorStateList color) {
                setColor(view, color);
            }
        });
    }

    @Override
    public int type() {
        return super.type();
    }

    public abstract void setColor(V view, int color);

    public abstract void setColor(V view, ColorStateList colors);
}
