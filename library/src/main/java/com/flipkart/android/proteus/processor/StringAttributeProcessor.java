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
import com.flipkart.android.proteus.Resource;
import com.flipkart.android.proteus.Style;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.parser.ParseHelper;

/**
 * @author kirankumar
 * @author aditya.sharat
 */
public abstract class StringAttributeProcessor<V extends View> extends AttributeProcessor<V> {

    private static final String TAG = "StringProcessor";

    private static final String STRING_LOCAL_RESOURCE_STR = "@string/";

    public static final Primitive EMPTY = new Primitive("");

    /**
     * @param view  View
     * @param value
     */
    @Override
    public void handle(V view, Value value) {
        if (value.isResource()) {
            handle(view, value.getAsResource().getString(view.getContext()));
        } else {
            handle(view, value.getAsString());
        }
    }

    /**
     * @param view View
     */
    public abstract void handle(V view, String value);

    @Override
    public Value parse(Value value, Context context) {
        String string = value.getAsString();
        if (ParseHelper.isLocalResourceAttribute(string)) {
            Style style = Style.valueOf(string);
            return null != style ? style : EMPTY;
        } else if (isLocalStringResource(string)) {
            Resource resource = Resource.valueOf(string, Resource.STRING, context);
            return Resource.NOT_FOUND == resource ? EMPTY : resource;
        } else {
            return value;
        }
    }

    public static boolean isLocalStringResource(String value) {
        return value.startsWith(STRING_LOCAL_RESOURCE_STR);
    }
}
