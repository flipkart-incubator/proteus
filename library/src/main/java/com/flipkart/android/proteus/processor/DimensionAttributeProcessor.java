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
import android.view.View;

import com.flipkart.android.proteus.AttributeProcessor;
import com.flipkart.android.proteus.AttributeResource;
import com.flipkart.android.proteus.Dimension;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.Resource;
import com.flipkart.android.proteus.StyleResource;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.parser.ParseHelper;

/**
 *
 */
public abstract class DimensionAttributeProcessor<T extends View> extends AttributeProcessor<T> {

    public static float evaluate(Value value, ProteusView view) {
        if (value == null) {
            return Dimension.ZERO.apply(((View) view).getContext());
        }

        final float[] result = new float[1];
        DimensionAttributeProcessor<View> processor = new DimensionAttributeProcessor<View>() {
            @Override
            public void setDimension(View view, float dimension) {
                result[0] = dimension;
            }
        };
        processor.process((View) view, value);

        return result[0];
    }

    public static Value staticCompile(Value value, Context context) {
        if (null == value || !value.isPrimitive()) {
            return Dimension.ZERO;
        }
        String string = value.getAsString();
        if (Dimension.isLocalDimensionResource(string)) {
            Resource resource = Resource.valueOf(string, Resource.DIMEN, context);
            return null == resource ? Dimension.ZERO : resource;
        } else if (ParseHelper.isStyleAttribute(string)) {
            StyleResource style = StyleResource.valueOf(string);
            return null != style ? style : Dimension.ZERO;
        } else {
            return Dimension.valueOf(string, context);
        }
    }

    @Override
    public final void handleValue(T view, Value value) {
        if (value.isDimension()) {
            setDimension(view, value.getAsDimension().apply(view.getContext()));
        } else if (value.isPrimitive()) {
            process(view, compile(value, view.getContext()));
        }
    }

    @Override
    public void handleResource(T view, Resource resource) {
        Float dimension = resource.getDimension(view.getContext());
        setDimension(view, null == dimension ? 0 : dimension);
    }

    @Override
    public void handleAttributeResource(T view, AttributeResource attribute) {
        TypedArray a = attribute.apply(view.getContext());
        setDimension(view, a.getDimensionPixelSize(0, 0));
    }

    @Override
    public void handleStyleResource(T view, StyleResource style) {
        TypedArray a = style.apply(view.getContext());
        setDimension(view, a.getDimensionPixelSize(0, 0));
    }

    /**
     * @param view View
     */
    public abstract void setDimension(T view, float dimension);

    @Override
    public Value compile(Value value, Context context) {
        return staticCompile(value, context);
    }

}
