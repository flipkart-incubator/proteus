/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Flipkart Internet Pvt. Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.flipkart.android.proteus.processor;

import android.content.res.ColorStateList;
import android.view.View;
import android.webkit.ValueCallback;

import com.flipkart.android.proteus.toolbox.ColorUtils;
import com.google.gson.JsonElement;

public abstract class ColorResourceProcessor<V extends View> extends AttributeProcessor<V> {

    public ColorResourceProcessor() {

    }

    @Override
    public void handle(String key, JsonElement value, final V view) {
        ColorUtils.loadColor(view.getContext(), value, new ValueCallback<Integer>() {
            /**
             * Invoked when the value is available.
             *
             * @param value The value.
             */
            @Override
            public void onReceiveValue(Integer value) {
                setColor(view, value);
            }
        }, new ValueCallback<ColorStateList>() {
            /**
             * Invoked when the value is available.
             *
             * @param value The value.
             */
            @Override
            public void onReceiveValue(ColorStateList value) {
                setColor(view, value);
            }
        });
    }

    public abstract void setColor(V view, int color);

    public abstract void setColor(V view, ColorStateList colors);
}
