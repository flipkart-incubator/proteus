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
 * @author kirankumar
 * @author aditya.sharat
 */
public abstract class StringAttributeProcessor<V extends View> extends AttributeProcessor<V> {

    private static final String STRING_RESOURCE_PREFIX = "@string/";

    public static final String EMPTY_STRING = "";
    public static final Primitive EMPTY = new Primitive("");

    /**
     * @param view  View
     * @param value
     */
    @Override
    public void handleValue(V view, Value value) {
        String string = value.getAsString();
        if (ParseHelper.isStyleAttribute(string) || isLocalStringResource(string)) {
            process(view, compile(value, view.getContext()));
        } else {
            setString(view, string);
        }
    }

    @Override
    public void handleResource(V view, Resource resource) {
        String string = resource.getString(view.getContext());
        setString(view, null == string ? EMPTY_STRING : string);
    }

    @Override
    public void handleStyleAttribute(V view, StyleAttribute style) {
        TypedArray a = style.apply(view.getContext());
        setString(view, a.getString(0));
    }

    /**
     * @param view View
     */
    public abstract void setString(V view, String value);

    @Override
    public Value compile(Value value, Context context) {
        String string = value.getAsString();
        if (isLocalStringResource(string)) {
            Resource resource = Resource.valueOf(string, Resource.STRING, context);
            return null == resource ? EMPTY : resource;
        } else if (ParseHelper.isStyleAttribute(string)) {
            StyleAttribute style = StyleAttribute.valueOf(string);
            return null != style ? style : EMPTY;
        } else {
            return value;
        }
    }

    public static boolean isLocalStringResource(String value) {
        return value.startsWith(STRING_RESOURCE_PREFIX);
    }
}
