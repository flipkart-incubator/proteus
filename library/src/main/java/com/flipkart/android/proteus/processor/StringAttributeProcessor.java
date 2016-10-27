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

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.View;

import com.flipkart.android.proteus.LayoutParser;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.toolbox.ProteusConstants;

/**
 * @author kirankumar
 * @author aditya.sharat
 */
public abstract class StringAttributeProcessor<V extends View> extends AttributeProcessor<V> {

    private static final String TAG = "StringProcessor";

    /**
     * @param view View
     */
    @Override
    public void handle(V view, String key, LayoutParser parser) {
        if (parser.isString()) {
            handle(key, getStringFromAttribute(view, parser.getString()), view);
        } else {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, "Could not resolve string for : " + parser.toString());
            }
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
