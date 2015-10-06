package com.flipkart.layoutengine.processor;

import android.content.Context;
import android.view.View;

import com.flipkart.layoutengine.EventType;
import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.builder.LayoutBuilderCallback;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Use this as the base processor for handling events like OnClick , OnLongClick , OnTouch etc.
 * Created by prateek.dixit on 20/11/14.
 */

public abstract class EventProcessor<T> extends AttributeProcessor<T> {

    private Context context;

    public EventProcessor(Context context) {
        this.context = context;
    }

    @Override
    public void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue,
                       T view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
        setOnEventListener(view, parserContext, attributeValue);
    }

    public abstract void setOnEventListener(T view, ParserContext parserContext, JsonElement attributeValue);

    /**
     * This delegates Event with required attributes to client
     *
     * @param parserContext
     * @param eventType
     * @param view
     */
    public void fireEvent(View view, ParserContext parserContext, EventType eventType, JsonElement attributeValue) {
        LayoutBuilderCallback layoutBuilderCallback = parserContext.getLayoutBuilder().getListener();
        layoutBuilderCallback.onEvent(parserContext, view, attributeValue, eventType);
    }
}
