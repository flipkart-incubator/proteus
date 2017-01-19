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
import com.flipkart.android.proteus.Primitive;
import com.flipkart.android.proteus.Resource;
import com.flipkart.android.proteus.StyleAttribute;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.parser.ParseHelper;

/**
 * BooleanAttributeProcessor
 *
 * @author aditya.sharat
 */

public abstract class BooleanAttributeProcessor<V extends View> extends AttributeProcessor<V> {

    public static final String BOOLEAN_RESOURCE_PREFIX = "@bool/";

    public static final Primitive TRUE = new Primitive(true);
    public static final Primitive FALSE = new Primitive(false);

    @Override
    public void handle(V view, Value value) {
        if (value.isPrimitive() && value.getAsPrimitive().isBoolean()) {
            handle(view, value.getAsPrimitive().getAsBoolean());
        } else if (value.isResource()) {
            handle(view, value.getAsResource().getBoolean(view.getContext()));
        } else if (value.isStyle()) {
            TypedArray a = value.getAsStyleAttribute().apply(view.getContext());
            handle(view, a.getBoolean(0, false));
        } else {
            handle(view, parse(value, view.getContext()));
        }
    }

    public abstract void handle(V view, boolean value);

    @Override
    public Value parse(Value value, Context context) {
        if (value.isPrimitive()) {
            String string = value.getAsString();
            if (ParseHelper.isStyleAttribute(string)) {
                StyleAttribute style = StyleAttribute.valueOf(string);
                return null != style ? style : FALSE;
            } else if (isLocalBooleanResource(string)) {
                Resource resource = Resource.valueOf(string, Resource.STRING, context);
                return Resource.NOT_FOUND == resource ? FALSE : resource;
            } else {
                return ParseHelper.parseBoolean(value) ? TRUE : FALSE;
            }
        } else {
            return ParseHelper.parseBoolean(value) ? TRUE : FALSE;
        }
    }

    public static boolean isLocalBooleanResource(String value) {
        return value.startsWith(BOOLEAN_RESOURCE_PREFIX);
    }
}
