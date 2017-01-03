/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 *
 * Copyright (c) 2017 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.flipkart.android.proteus.toolbox;

import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.widget.Adapter;

import com.flipkart.android.proteus.Layout;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.view.ProteusView;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * @author kiran.kumar
 */
public interface LayoutInflaterCallback {

    /**
     * called when the builder encounters an attribute key which is unhandled by its parser.
     *
     * @param view      corresponding view for current attribute that is being parsed
     * @param attribute attribute that is being parsed
     * @param value
     */
    void onUnknownAttribute(ProteusView view, int attribute, Value value);

    /**
     * called when the builder encounters a view type which it cannot understand.
     */
    @Nullable
    ProteusView onUnknownViewType(String type, View parent, Layout layout, JsonObject data, Styles styles, int index);

    /**
     *
     * @param type
     * @param include
     * @return
     */
    Layout onLayoutRequired(String type, Layout include);

    void onViewBuiltFromViewProvider(ProteusView view, View parent, String type, int index);

    /**
     * called when any click occurs on views
     *
     * @param view  The view that triggered the event
     * @param value
     */
    View onEvent(ProteusView view, EventType eventType, Value value);

    PagerAdapter onPagerAdapterRequired(ProteusView parent, final List<ProteusView> children, Layout layout);

    Adapter onAdapterRequired(ProteusView parent, final List<ProteusView> children, Layout layout);

}
