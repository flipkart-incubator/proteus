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


import android.view.View;

import com.flipkart.android.proteus.parser.ParseHelper;
import com.google.gson.JsonElement;

public abstract class DimensionAttributeProcessor<T extends View> extends AttributeProcessor<T> {

    /**
     * @param view View
     */
    @Override
    public final void handle(String key, JsonElement value, T view) {
        if (value != null && value.isJsonPrimitive()) {
            float dimension = ParseHelper.parseDimension(value.getAsString(), view.getContext());
            setDimension(dimension, view, key, value);
        }
    }

    /**
     * @param view  View
     * @param key   Attribute Key
     * @param value Attribute Value
     */
    public abstract void setDimension(float dimension, T view, String key, JsonElement value);
}
