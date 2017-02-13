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
import com.flipkart.android.proteus.Primitive;
import com.flipkart.android.proteus.Resource;
import com.flipkart.android.proteus.StyleResource;
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

    public static boolean isLocalBooleanResource(String value) {
        return value.startsWith(BOOLEAN_RESOURCE_PREFIX);
    }

    @Override
    public void handleValue(V view, Value value) {
        if (value.isPrimitive() && value.getAsPrimitive().isBoolean()) {
            setBoolean(view, value.getAsPrimitive().getAsBoolean());
        } else {
            process(view, compile(value, view.getContext()));
        }
    }

    @Override
    public void handleResource(V view, Resource resource) {
        Boolean bool = resource.getBoolean(view.getContext());
        setBoolean(view, null != bool ? bool : false);
    }

    @Override
    public void handleAttributeResource(V view, AttributeResource attribute) {
        TypedArray a = attribute.apply(view.getContext());
        setBoolean(view, a.getBoolean(0, false));
    }

    @Override
    public void handleStyleResource(V view, StyleResource style) {
        TypedArray a = style.apply(view.getContext());
        setBoolean(view, a.getBoolean(0, false));
    }

    public abstract void setBoolean(V view, boolean value);

    @Override
    public Value compile(Value value, Context context) {
        if (value.isPrimitive()) {
            String string = value.getAsString();
            if (isLocalBooleanResource(string)) {
                Resource resource = Resource.valueOf(string, Resource.BOOLEAN, context);
                return null == resource ? FALSE : resource;
            } else if (ParseHelper.isStyleAttribute(string)) {
                StyleResource style = StyleResource.valueOf(string);
                return null != style ? style : FALSE;
            } else {
                return ParseHelper.parseBoolean(value) ? TRUE : FALSE;
            }
        } else {
            return ParseHelper.parseBoolean(value) ? TRUE : FALSE;
        }
    }
}
