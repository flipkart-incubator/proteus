package com.flipkart.layoutengine.processor;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.toolbox.AnimationUtils;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use this as the base processor for references like @anim
 */
public abstract class TweenAnimationResourceProcessor<V extends View> extends AttributeProcessor<V> {
    private static final String TAG = TweenAnimationResourceProcessor.class.getSimpleName();
    private Context mContext;
    private Logger mLogger = LoggerFactory.getLogger(TweenAnimationResourceProcessor.class);

    public TweenAnimationResourceProcessor(Context context) {
        this.mContext = context;
    }

    @Override
    public void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue,
                       V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
        Animation animation = AnimationUtils.loadAnimation(mContext, attributeValue);
        if (null != animation) {
            setAnimation(view, animation);
        } else {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("Resource for key: " + attributeKey
                        + " must be a primitive or an object. value -> " + attributeValue.toString());
            }
        }
    }


    public abstract void setAnimation(V view, Animation animation);
}
