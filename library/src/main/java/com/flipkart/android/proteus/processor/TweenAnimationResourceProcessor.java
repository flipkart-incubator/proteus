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

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import com.flipkart.android.proteus.toolbox.AnimationUtils;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.google.gson.JsonElement;

/**
 * Use this as the base processor for references like @anim
 */
public abstract class TweenAnimationResourceProcessor<V extends View> extends AttributeProcessor<V> {

    private static final String TAG = "TweenAnimationResource";

    @Override
    public void handle(String key, JsonElement value, V view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), value);
        if (null != animation) {
            setAnimation(view, animation);
        } else {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, "Resource for key: " + key + " must be a primitive or an object. value -> " + value.toString());
            }
        }
    }

    public abstract void setAnimation(V view, Animation animation);
}
