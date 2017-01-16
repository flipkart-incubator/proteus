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
import android.view.View;

import com.flipkart.android.proteus.AttributeProcessor;
import com.flipkart.android.proteus.Primitive;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.parser.ParseHelper;

/**
 * BooleanAttributeProcessor
 *
 * @author aditya.sharat
 */

public abstract class BooleanAttributeProcessor<V extends View> extends AttributeProcessor<V> {

    public static final Primitive TRUE = new Primitive(true);
    public static final Primitive FALSE = new Primitive(false);

    @Override
    public void handle(V view, Value value) {
        handle(view, ParseHelper.parseBoolean(value));
    }

    public abstract void handle(V view, boolean value);

    @Override
    public Value parse(Value value, Context context) {
        return ParseHelper.parseBoolean(value) ? TRUE : FALSE;
    }
}
