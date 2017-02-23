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
import android.support.annotation.Nullable;
import android.view.View;

import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.value.AttributeResource;
import com.flipkart.android.proteus.value.Color;
import com.flipkart.android.proteus.value.Resource;
import com.flipkart.android.proteus.value.StyleResource;
import com.flipkart.android.proteus.value.Value;

public abstract class ColorResourceProcessor<V extends View> extends AttributeProcessor<V> {

    public static Color.Result evaluate(Value value, ProteusView view) {
        final Color.Result[] result = new Color.Result[1];
        ColorResourceProcessor<View> processor = new ColorResourceProcessor<View>() {
            @Override
            public void setColor(View view, int color) {
                result[0] = new Color.Result(color, null);
            }

            @Override
            public void setColor(View view, ColorStateList colors) {
                result[0] = new Color.Result(Color.Int.BLACK.value, colors);
            }
        };
        processor.process(view.getAsView(), value);
        return result[0];
    }

    public static Value staticCompile(@Nullable Value value, Context context) {
        if (null == value) {
            return Color.Int.BLACK;
        }
        if (value.isObject()) {
            return Color.valueOf(value.getAsObject(), context);
        } else if (value.isPrimitive()) {
            return Color.valueOf(value.getAsString(), Color.Int.BLACK);
        } else {
            return Color.Int.BLACK;
        }
    }

    @Override
    public void handleValue(final V view, Value value) {
        if (value.isColor()) {
            apply(view, value.getAsColor());
        } else {
            process(view, precompile(value, view.getContext()));
        }
    }

    @Override
    public void handleResource(V view, Resource resource) {
        ColorStateList colors = resource.getColorStateList(view.getContext());
        if (null != colors) {
            setColor(view, colors);
        } else {
            Integer color = resource.getColor(view.getContext());
            setColor(view, null == color ? Color.Int.BLACK.value : color);
        }
    }

    @Override
    public void handleAttributeResource(V view, AttributeResource attribute) {
        TypedArray a = attribute.apply(view.getContext());
        set(view, a);
    }

    @Override
    public void handleStyleResource(V view, StyleResource style) {
        TypedArray a = style.apply(view.getContext());
        set(view, a);
    }

    private void set(V view, TypedArray a) {
        ColorStateList colors = a.getColorStateList(0);
        if (null != colors) {
            setColor(view, colors);
        } else {
            setColor(view, a.getColor(0, Color.Int.BLACK.value));
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

    public abstract void setColor(V view, int color);

    public abstract void setColor(V view, ColorStateList colors);

    @Override
    public Value compile(@Nullable Value value, Context context) {
        return staticCompile(value, context);
    }
}
