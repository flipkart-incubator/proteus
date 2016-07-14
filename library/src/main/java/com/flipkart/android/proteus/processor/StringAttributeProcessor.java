/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Flipkart Internet Pvt. Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.flipkart.android.proteus.processor;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.View;

import com.flipkart.android.proteus.parser.ParseHelper;
import com.google.gson.JsonElement;

/**
 * @author kirankumar
 * @author aditya.sharat
 */
public abstract class StringAttributeProcessor<V extends View> extends AttributeProcessor<V> {
    /**
     * @param view View
     */
    @Override
    public void handle(String key, JsonElement value, V view) {
        if (value.isJsonPrimitive()) {
            handle(key, getStringFromAttribute(view, value.getAsString()), view);
        } else {
            handle(key, value.toString(), view);
        }
    }

    private String getStringFromAttribute(V view, String attributeValue) {
        String result;
        if (ParseHelper.isLocalResourceAttribute(attributeValue)) {
            int attributeId = ParseHelper.getAttributeId(view.getContext(), attributeValue);
            if (0 != attributeId) {
                TypedArray ta = view.getContext().obtainStyledAttributes(new int[]{attributeId});
                result = ta.getString(0/* index */);
                ta.recycle();
            } else {
                result = "";
            }
        } else if (ParseHelper.isLocalDrawableResource(attributeValue)) {
            try {
                Resources r = view.getContext().getResources();
                int stringId = r.getIdentifier(attributeValue, "string", view.getContext().getPackageName());
                result = r.getString(stringId);
            } catch (Exception ex) {
                result = "";
                System.out.println("Could not load local resource " + attributeValue);
            }
        } else {
            result = attributeValue;
        }
        return result;
    }

    /**
     * @param attributeKey   Attribute Key
     * @param attributeValue Attribute Value
     * @param view           View
     */
    public abstract void handle(String attributeKey, String attributeValue, V view);

}
