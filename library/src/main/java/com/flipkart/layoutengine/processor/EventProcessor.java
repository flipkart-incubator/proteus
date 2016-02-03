package com.flipkart.layoutengine.processor;

import com.flipkart.layoutengine.EventType;
import com.flipkart.layoutengine.builder.LayoutBuilderCallback;
import com.flipkart.layoutengine.view.ProteusView;
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
