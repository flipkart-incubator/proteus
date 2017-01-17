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
import com.flipkart.android.proteus.Style;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.parser.ParseHelper;

public abstract class DimensionAttributeProcessor<T extends View> extends AttributeProcessor<T> {

    /**
     * @param view  View
     * @param value Value of the dimension
     */
    @Override
    public final void handle(T view, Value value) {
        if (value.isDimension()) {
            setDimension(view, value.getAsDimension().apply(view.getContext()));
        } else if (value.isStyle()) {
            TypedArray a = value.getAsStyle().apply(view.getContext());
            setDimension(view, a.getDimensionPixelSize(0, 0));
        } else if (value.isPrimitive()) {
            handle(view, parse(value, view.getContext()));
        }
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
        if (ParseHelper.isLocalResourceAttribute(string)) {
            Style style = Style.valueOf(string);
            return null != style ? style : Dimension.ZERO;
        } else {
            return Dimension.valueOf(value.getAsString(), context);
        }
    }

}
