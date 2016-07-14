package com.flipkart.android.proteus.processor;

import com.flipkart.android.proteus.EventType;
import com.flipkart.android.proteus.builder.LayoutBuilderCallback;
import com.flipkart.android.proteus.view.ProteusView;
import com.google.gson.JsonElement;

/**
 * Use this as the base processor for handling events like OnClick , OnLongClick , OnTouch etc.
 * Created by prateek.dixit on 20/11/14.
 */

public abstract class EventProcessor<T> extends AttributeProcessor<T> {

    @Override
    public void handle(String key, JsonElement value, T view) {
        setOnEventListener(view, value);
    }

    public abstract void setOnEventListener(T view, JsonElement attributeValue);

    /**
     * This delegates Event with required attributes to client
     */
    public void fireEvent(ProteusView view, EventType eventType, JsonElement attributeValue) {
        LayoutBuilderCallback layoutBuilderCallback = view.getViewManager().getLayoutBuilder().getListener();
        layoutBuilderCallback.onEvent(view, attributeValue, eventType);
    }
}
