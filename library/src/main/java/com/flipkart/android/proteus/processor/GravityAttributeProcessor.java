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
import android.support.annotation.IntDef;
import android.view.View;

import com.flipkart.android.proteus.AttributeProcessor;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.parser.ParseHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

/**
 * GravityAttributeProcessor
 *
 * @author aditya.sharat
 */

public abstract class GravityAttributeProcessor<V extends View> extends AttributeProcessor<V> {

    @Override
    public void handle(V view, Value value) {
        int gravity = android.view.Gravity.NO_GRAVITY;
        if (value.isPrimitive() && value.getAsPrimitive().isNumber()) {
            gravity = value.getAsInt();
        } else if (value.isPrimitive()) {
            gravity = ParseHelper.parseGravity(value.getAsString());
        }
        //noinspection WrongConstant
        handle(view, gravity);
    }

    public abstract void handle(V view, @Gravity int gravity);

    @Override
    public Value parse(Value value, Context context) {
        return ParseHelper.getGravity(value.getAsString());
    }

    @IntDef({android.view.Gravity.NO_GRAVITY,
            android.view.Gravity.TOP,
            android.view.Gravity.BOTTOM,
            android.view.Gravity.LEFT,
            android.view.Gravity.RIGHT,
            android.view.Gravity.START,
            android.view.Gravity.END,
            android.view.Gravity.CENTER_VERTICAL,
            android.view.Gravity.FILL_VERTICAL,
            android.view.Gravity.CENTER_HORIZONTAL,
            android.view.Gravity.FILL_HORIZONTAL,
            android.view.Gravity.CENTER,
            android.view.Gravity.FILL})
    @Retention(RetentionPolicy.SOURCE)
    @Target({FIELD, METHOD, PARAMETER, LOCAL_VARIABLE, TYPE})
    public @interface Gravity {
    }

}
