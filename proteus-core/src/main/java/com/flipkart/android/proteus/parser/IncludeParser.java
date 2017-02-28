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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.ProteusConstants;
import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;

/**
 * IncludeParser
 *
 * @author aditya.sharat
 */

public class IncludeParser<V extends View> extends ViewTypeParser<V> {

    @NonNull
    @Override
    public String getType() {
        return "include";
    }

    @Nullable
    @Override
    public String getParentType() {
        return null;
    }

    @NonNull
    @Override
    public ProteusView createView(@NonNull ProteusContext context, @NonNull Layout include, @NonNull ObjectValue data, @Nullable ViewGroup parent, int dataIndex) {
        String type = include.extras.getAsString(ProteusConstants.LAYOUT);
        return context.getInflater().inflate(type, data, parent, dataIndex);
    }

    @Override
    protected void addAttributeProcessors() {

    }

}
