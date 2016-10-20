/*
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.android.proteus.processor;


import android.view.View;

import com.flipkart.android.proteus.LayoutParser;
import com.flipkart.android.proteus.parser.ParseHelper;

public abstract class DimensionAttributeProcessor<T extends View> extends AttributeProcessor<T> {

    /**
     * @param view View
     */
    @Override
    public final void handle(T view, String key, LayoutParser parser) {
        if (parser != null && parser.isString()) {
            float dimension = ParseHelper.parseDimension(parser.getString(), view.getContext());
            setDimension(view, key, dimension, parser);
        }
    }

    /**
     * @param view   View
     * @param key    Attribute Key
     * @param parser Attribute Value
     */
    public abstract void setDimension(T view, String key, float dimension, LayoutParser parser);
}
