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
