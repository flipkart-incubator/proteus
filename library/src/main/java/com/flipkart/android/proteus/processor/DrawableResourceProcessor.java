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
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;

import com.flipkart.android.proteus.AttributeProcessor;
import com.flipkart.android.proteus.DrawableValue;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.Resource;
import com.flipkart.android.proteus.StyleResource;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.toolbox.BitmapLoader;

/**
 * Use this as the base processor for references like @drawable or remote resources with http:// urls.
 */
public abstract class DrawableResourceProcessor<V extends View> extends AttributeProcessor<V> {

    @Nullable
    public static Drawable evaluate(Value value, ProteusView view) {
        if (null == value) {
            return null;
        }
        final Drawable[] d = new Drawable[1];
        DrawableResourceProcessor<View> processor = new DrawableResourceProcessor<View>() {
            @Override
            public void setDrawable(View view, Drawable drawable) {
                d[0] = drawable;
            }
        };
        processor.process((View) view, value);
        return d[0];
    }

    public static Value staticCompile(Value value, Context context) {
        if (value.isPrimitive()) {
            String string = value.getAsString();
            if (DrawableValue.isLocalDrawableResource(string)) {
                return Resource.valueOf(string, Resource.DRAWABLE, context);
            } else if (ParseHelper.isStyleAttribute(string)) {
                return StyleResource.valueOf(string);
            } else {
                return DrawableValue.valueOf(string, context);
            }
        } else if (value.isObject()) {
            return DrawableValue.valueOf(value.getAsObject(), context);
        } else {
            throw new IllegalArgumentException("drawable must be a primitive or object");
        }
    }

    @Override
    public void handleValue(final V view, Value value) {
        if (value.isDrawable()) {
            DrawableValue d = value.getAsDrawable();
            if (null != d) {
                BitmapLoader loader = ((ProteusView) view).getViewManager().getProteusLayoutInflater().getBitmapLoader();
                d.apply(view.getContext(), new DrawableValue.Callback() {
                    @Override
                    public void apply(Drawable drawable) {
                        if (null != drawable) {
                            setDrawable(view, drawable);
                        }
                    }
                }, loader, (ProteusView) view);
            }
        } else {
            process(view, compile(value, view.getContext()));
        }
    }

    @Override
    public void handleResource(V view, Resource resource) {
        Drawable d = resource.getDrawable(view.getContext());
        if (null != d) {
            setDrawable(view, d);
        }
    }

    @Override
    public void handleStyleResource(V view, StyleResource style) {
        TypedArray a = style.apply(view.getContext());
        Drawable d = a.getDrawable(0);
        if (null != d) {
            setDrawable(view, d);
        }
    }

    public abstract void setDrawable(V view, Drawable drawable);

    @Override
    public Value compile(Value value, Context context) {
        return staticCompile(value, context);
    }
}
