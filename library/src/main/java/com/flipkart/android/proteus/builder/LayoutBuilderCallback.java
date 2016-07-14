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

package com.flipkart.android.proteus.builder;

import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.widget.Adapter;

import com.flipkart.android.proteus.EventType;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * @author kiran.kumar
 */
public interface LayoutBuilderCallback {

    /**
     * called when the builder encounters an attribute key which is unhandled by its parser.
     *
     * @param attribute attribute that is being parsed
     * @param view      corresponding view for current attribute that is being parsed
     */
    void onUnknownAttribute(String attribute, JsonElement value, ProteusView view);

    /**
     * called when the builder encounters a view type which it cannot understand.
     */
    @Nullable
    ProteusView onUnknownViewType(String type, View parent, JsonObject layout, JsonObject data, int index, Styles styles);

    JsonObject onLayoutRequired(String type, ProteusView parent);

    void onViewBuiltFromViewProvider(ProteusView view, View parent, String type, int index);

    /**
     * called when any click occurs on views
     *
     * @param view The view that triggered the event
     */
    View onEvent(ProteusView view, JsonElement value, EventType eventType);

    PagerAdapter onPagerAdapterRequired(ProteusView parent, final List<ProteusView> children, JsonObject layout);

    Adapter onAdapterRequired(ProteusView parent, final List<ProteusView> children, JsonObject layout);

}
