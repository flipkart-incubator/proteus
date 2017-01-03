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


import android.view.View;

import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.parser.ParseHelper;

public abstract class DimensionAttributeProcessor<T extends View> extends AttributeProcessor<T> {

    /**
     * @param view  View
     * @param value
     */
    @Override
    public final void handle(T view, Value value) {
        if (value != null && value.isPrimitive()) {
            float dimension = ParseHelper.parseDimension(value.getAsString(), view.getContext());
            setDimension(view, dimension);
        }
    }

    /**
     * @param view View
     */
    public abstract void setDimension(T view, float dimension);
}
