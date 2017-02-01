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
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.Resource;
import com.flipkart.android.proteus.StyleAttribute;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.parser.ParseHelper;

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
        processor.process((View) view, value);
        return result[0];
    }

    public static Value staticParse(Value value, Context context) {
        if (null == value) {
            return Color.Int.BLACK;
        }
        if (value.isObject()) {
            return Color.valueOf(value.getAsObject(), context);
        } else if (value.isPrimitive()) {
            String string = value.getAsString();
            if (Color.isLocalColorResource(string)) {
                Resource resource = Resource.valueOf(string, Resource.COLOR, context);
                return null == resource ? Color.Int.BLACK : resource;
            } else if (ParseHelper.isStyleAttribute(string)) {
                StyleAttribute style = StyleAttribute.valueOf(string);
                return null != style ? style : Color.Int.BLACK;
            } else {
                return Color.valueOf(value.getAsString());
            }
        } else {
            return Color.Int.BLACK;
        }
    }

    @Override
    public void handleValue(final V view, Value value) {
        if (value.isColor()) {
            apply(view, value.getAsColor());
        } else {
            process(view, parse(value, view.getContext()));
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
    public void handleStyleAttribute(V view, StyleAttribute style) {
        TypedArray a = style.apply(view.getContext());
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
    public Value parse(Value value, Context context) {
        return staticParse(value, context);
    }
}
