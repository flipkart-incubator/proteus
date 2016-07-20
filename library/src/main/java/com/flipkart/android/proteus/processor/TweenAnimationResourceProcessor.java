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

import android.view.View;
import android.view.animation.Animation;

import com.flipkart.android.proteus.toolbox.AnimationUtils;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.google.gson.JsonElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use this as the base processor for references like @anim
 */
public abstract class TweenAnimationResourceProcessor<V extends View> extends AttributeProcessor<V> {

    private static Logger logger = LoggerFactory.getLogger(TweenAnimationResourceProcessor.class);

    @Override
    public void handle(String key, JsonElement value, V view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), value);
        if (null != animation) {
            setAnimation(view, animation);
        } else {
            if (ProteusConstants.isLoggingEnabled()) {
                logger.error("Resource for key: " + key
                        + " must be a primitive or an object. value -> " + value.toString());
            }
        }
    }

    public abstract void setAnimation(V view, Animation animation);
}
