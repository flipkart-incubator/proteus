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

package com.flipkart.android.proteus;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author kirankumar
 * @author aditya.sharat
 */
public abstract class AttributeProcessor<V> {

    public static final int TYPE_VALUE = 0;
    public static final int TYPE_COLOR = 1;
    public static final int TYPE_DIMENSION = 2;
    public static final int TYPE_DRAWABLE = 3;
    public static final int TYPE_STRING = 4;
    public static final int TYPE_ANIMATION = 5;

    public abstract void handle(V view, Value value);

    @Type
    public int type() {
        return TYPE_VALUE;
    }

    @IntDef({TYPE_VALUE, TYPE_COLOR, TYPE_DIMENSION, TYPE_DRAWABLE, TYPE_STRING, TYPE_ANIMATION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

}
