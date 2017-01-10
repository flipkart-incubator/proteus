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

package com.flipkart.android.proteus.parser;

import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.Layout;
import com.flipkart.android.proteus.ProteusLayoutInflater;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.TypeParser;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Styles;
import com.google.gson.JsonObject;

/**
 * IncludeParser
 *
 * @author aditya.sharat
 */

public class IncludeParser<V extends View> extends TypeParser<V> {

    @Override
    public ProteusView createView(ProteusLayoutInflater inflater, ViewGroup parent, Layout include, JsonObject data, Styles styles, int index) {
        String type = include.extras.getAsString(ProteusConstants.LAYOUT);
        Layout layout = inflater.onIncludeLayout(type, include);
        return inflater.inflate(parent, layout, data, styles, index);
    }

    @Override
    protected void addAttributeProcessors() {

    }

}
