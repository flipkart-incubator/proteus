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

package com.flipkart.android.proteus.demo;

import android.view.ViewGroup;

import com.flipkart.android.proteus.Layout;
import com.flipkart.android.proteus.demo.customviews.CircleView;
import com.flipkart.android.proteus.parser.Attributes;
import com.flipkart.android.proteus.parser.BaseTypeParser;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.view.ProteusView;
import com.google.gson.JsonObject;

/**
 * CircleViewParser
 *
 * @author aditya.sharat
 */

public class CircleViewParser extends WrappableParser<CircleView> {

    public CircleViewParser(BaseTypeParser<CircleView> wrappedParser) {
        super(wrappedParser);
    }

    @Override
    public ProteusView createView(ViewGroup parent, Layout layout, JsonObject data, Styles styles, int index) {
        return new CircleView(parent.getContext());
    }

    @Override
    protected void registerAttributeProcessors() {
        super.registerAttributeProcessors();
        addAttributeProcessor(new Attributes.Attribute("color"), new StringAttributeProcessor<CircleView>() {
            @Override
            public void handle(CircleView view, String value) {
                view.setColor(value);
            }
        });
    }
}

