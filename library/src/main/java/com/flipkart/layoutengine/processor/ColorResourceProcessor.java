package com.flipkart.layoutengine.processor;

import android.content.res.ColorStateList;
import android.view.View;
import android.webkit.ValueCallback;

import com.flipkart.layoutengine.toolbox.ColorUtils;
import com.google.gson.JsonElement;

public abstract class ColorResourceProcessor<V extends View> extends AttributeProcessor<V> {

    public ColorResourceProcessor() {

    }

    @Override
    public void handle(String key, JsonElement value, final V view) {
        ColorUtils.loadColor(view.getContext(), value, new ValueCallback<Integer>() {
            /**
             * Invoked when the value is available.
             *
             * @param value The value.
             */
            @Override
            public void onReceiveValue(Integer value) {
                setColor(view, value);
            }
        }, new ValueCallback<ColorStateList>() {
            /**
             * Invoked when the value is available.
             *
             * @param value The value.
             */
            @Override
            public void onReceiveValue(ColorStateList value) {
                setColor(view, value);
            }
        });
    }

    public abstract void setColor(V view, int color);

    public abstract void setColor(V view, ColorStateList colors);
}
