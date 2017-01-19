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
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.view.View;

import com.flipkart.android.proteus.AttributeProcessor;
import com.flipkart.android.proteus.Color;
import com.flipkart.android.proteus.Style;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.parser.ParseHelper;

public abstract class ColorResourceProcessor<V extends View> extends AttributeProcessor<V> {

    @Override
    public void handle(final V view, Value value) {
        if (value.isColor()) {
            apply(view, value.getAsColor());
        } else if (value.isStyle()) {
            apply(view, value.getAsStyle());
        } else if (value.isPrimitive()) {
            handle(view, parse(value, view.getContext()));
        }
    }

    private void apply(V view, Color color) {
        Color.Result result = color.apply(view.getContext());
        if (null != result.colors) {
            setColor(view, result.colors);
        } else {
            setColor(view, result.color);
        }
    }

    private void apply(V view, Style style) {
        TypedArray a = style.apply(view.getContext());
        ColorStateList colors = a.getColorStateList(0);
        if (null != colors) {
            setColor(view, colors);
        } else {
            setColor(view, a.getColor(0, Color.Int.BLACK.value));
        }
    }

    public abstract void setColor(V view, int color);

    public abstract void setColor(V view, ColorStateList colors);

    @Override
    public Value parse(Value value, Context context) {
        if (value.isObject()) {
            return Color.valueOf(value.getAsObject(), context);
        } else {
            String string = value.getAsString();
            if (ParseHelper.isStyleAttribute(string)) {
                Style style = Style.valueOf(string);
                return null != style ? style : Color.Int.BLACK;
            } else {
                return Color.valueOf(value.getAsString(), context);
            }
        }
    }
}
