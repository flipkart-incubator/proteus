package com.flipkart.layoutengine.processor;

import android.content.Context;
import android.view.View;

import com.flipkart.layoutengine.EventType;
import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.builder.LayoutBuilderCallback;
import com.google.gson.JsonElement;

/**
 * Created by prateek.dixit on 20/11/14.
 */

/**
 * Use this as the base processor for handling events like OnClick , OnLongClick , OnTouch etc.
 */

public abstract class EventProcessor<T> extends AttributeProcessor<T> {

    private Context context;
    public EventProcessor(Context context) {
        this.context = context;
    }

    @Override
    public void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue, T view) {
        setOnEventListener(view,parserContext,attributeValue);
    }

    public abstract void setOnEventListener(T view, ParserContext parserContext, JsonElement attributeValue);

    /**
     * This delegates Event with required attributes to client
     * @param parserContext
     * @param eventType
     * @param view
     */
    public void fireEvent(View view, ParserContext parserContext, EventType eventType, JsonElement attributeValue) {
        LayoutBuilderCallback layoutBuilderCallback = parserContext.getLayoutBuilder().getListener();
        layoutBuilderCallback.onEvent(parserContext, view, attributeValue, eventType);
    }
}
