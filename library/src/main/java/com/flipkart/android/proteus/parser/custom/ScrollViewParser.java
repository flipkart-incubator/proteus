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

package com.flipkart.android.proteus.parser.custom;


import android.view.ViewGroup;
import android.widget.ScrollView;

import com.flipkart.android.proteus.Layout;
import com.flipkart.android.proteus.ProteusLayoutInflater;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.toolbox.Attributes;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.view.ProteusScrollView;
import com.google.gson.JsonObject;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class ScrollViewParser<T extends ScrollView> extends ViewTypeParser<T> {

    @Override
    public ProteusView createView(ProteusLayoutInflater inflater, ViewGroup parent, Layout layout, JsonObject data, Styles styles, ProteusLayoutInflater.Callback callback, ProteusLayoutInflater.ImageLoader loader, int index) {
        return new ProteusScrollView(parent.getContext());
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor(Attributes.ScrollView.Scrollbars, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                if ("none".equals(value)) {
                    view.setHorizontalScrollBarEnabled(false);
                    view.setVerticalScrollBarEnabled(false);
                } else if ("horizontal".equals(value)) {
                    view.setHorizontalScrollBarEnabled(true);
                    view.setVerticalScrollBarEnabled(false);
                } else if ("vertical".equals(value)) {
                    view.setHorizontalScrollBarEnabled(false);
                    view.setVerticalScrollBarEnabled(true);
                } else {
                    view.setHorizontalScrollBarEnabled(false);
                    view.setVerticalScrollBarEnabled(false);
                }
            }
        });
    }
}
