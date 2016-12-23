/*
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.android.proteus.parser;

import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.LayoutParser;
import com.flipkart.android.proteus.processor.AttributeProcessor;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.view.ProteusView;
import com.google.gson.JsonObject;

/**
 * @author kiran.kumar
 */
public interface TypeParser<V extends View> {

    void onBeforeCreateView(ViewGroup parent, LayoutParser layout, JsonObject data, Styles styles, int index);

    ProteusView createView(ViewGroup parent, LayoutParser layout, JsonObject data, Styles styles, int index);

    void onAfterCreateView(ViewGroup parent, V view, LayoutParser layout, JsonObject data, Styles styles, int index);

    void prepare();

    boolean isPrepared();

    void addAttributeProcessor(Attributes.Attribute key, AttributeProcessor<V> handler);

    boolean handleAttribute(V view, String attribute, LayoutParser parser);

    boolean handleAttribute(V view, int attribute, LayoutParser parser);

    boolean minify(LayoutParser out, String attribute, LayoutParser value);

    boolean handleChildren(ProteusView view);

    boolean addView(ProteusView parent, ProteusView view);
}
