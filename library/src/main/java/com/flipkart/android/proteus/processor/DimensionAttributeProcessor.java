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
import com.flipkart.android.proteus.Dimension;
import com.flipkart.android.proteus.Resource;
import com.flipkart.android.proteus.StyleAttribute;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.parser.ParseHelper;

public abstract class DimensionAttributeProcessor<T extends View> extends AttributeProcessor<T> {

    @Override
    public final void handleValue(T view, Value value) {
        if (value.isDimension()) {
            setDimension(view, value.getAsDimension().apply(view.getContext()));
        } else if (value.isPrimitive()) {
            process(view, parse(value, view.getContext()));
        }
    }

    @Override
    public void handleResource(T view, Resource resource) {
        Float dimension = resource.getDimension(view.getContext());
        setDimension(view, null == dimension ? 0 : dimension);
    }

    @Override
    public void handleStyleAttribute(T view, StyleAttribute style) {
        TypedArray a = style.apply(view.getContext());
        setDimension(view, a.getDimensionPixelSize(0, 0));
    }

    /**
     * @param view View
     */
    public abstract void setDimension(T view, float dimension);

    /**
     * @param value
     * @param context
     * @return
     */
    @Override
    public Value parse(Value value, Context context) {
        String string = value.getAsString();
        if (Dimension.isLocalDimensionResource(string)) {
            Resource resource = Resource.valueOf(string, Resource.DIMEN, context);
            return null == resource ? Dimension.ZERO : resource;
        } else if (ParseHelper.isStyleAttribute(string)) {
            StyleAttribute style = StyleAttribute.valueOf(string);
            return null != style ? style : Dimension.ZERO;
        } else {
            return Dimension.valueOf(value.getAsString(), context);
        }
    }

}
