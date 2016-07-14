package com.flipkart.android.proteus.processor;

import android.view.View;
import android.view.animation.Animation;

import com.flipkart.android.proteus.toolbox.AnimationUtils;
import com.google.gson.JsonElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use this as the base processor for references like @anim
 */
public abstract class TweenAnimationResourceProcessor<V extends View> extends AttributeProcessor<V> {

    private Logger mLogger = LoggerFactory.getLogger(TweenAnimationResourceProcessor.class);

    @Override
    public void handle(String key, JsonElement value, V view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), value);
        if (null != animation) {
            setAnimation(view, animation);
        } else {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("Resource for key: " + key
                        + " must be a primitive or an object. value -> " + value.toString());
            }
        }
    }

    public abstract void setAnimation(V view, Animation animation);
}
