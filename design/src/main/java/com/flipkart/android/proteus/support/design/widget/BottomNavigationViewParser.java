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

package com.flipkart.android.proteus.support.design.widget;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.view.ViewGroup;

import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.processor.AttributeProcessor;
import com.flipkart.android.proteus.processor.ColorResourceProcessor;
import com.flipkart.android.proteus.value.AttributeResource;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.flipkart.android.proteus.value.Resource;
import com.flipkart.android.proteus.value.StyleResource;
import com.flipkart.android.proteus.value.Value;

/**
 * BottomNavigationViewParser
 *
 * @author adityasharat
 */

public class BottomNavigationViewParser<V extends BottomNavigationView> extends ViewTypeParser<V> {
    @NonNull
    @Override
    public String getType() {
        return "BottomNavigationView";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "FrameLayout";
    }

    @NonNull
    @Override
    public ProteusView createView(@NonNull ProteusContext context, @NonNull Layout layout, @NonNull ObjectValue data, @Nullable ViewGroup parent, int dataIndex) {
        return new ProteusBottomNavigationView(context);
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor("itemBackground", new AttributeProcessor<V>() {
            @Override
            public void handleValue(V view, Value value) {
                throw new IllegalArgumentException("itemBackground must be a drawable resource id");
            }

            @Override
            public void handleResource(V view, Resource resource) {
                view.setItemBackgroundResource(resource.resId);
            }

            @Override
            public void handleAttributeResource(V view, AttributeResource attribute) {
                throw new IllegalArgumentException("itemBackground must be a drawable resource id");
            }

            @Override
            public void handleStyleResource(V view, StyleResource style) {
                throw new IllegalArgumentException("itemBackground must be a drawable resource id");
            }
        });

        addAttributeProcessor("itemIconTint", new ColorResourceProcessor<V>() {
            @Override
            public void setColor(V view, int color) {
                throw new IllegalArgumentException("itemIconTint must be a color state list");
            }

            @Override
            public void setColor(V view, ColorStateList colors) {
                view.setItemIconTintList(colors);
            }
        });

        addAttributeProcessor("itemTextColor", new ColorResourceProcessor<V>() {
            @Override
            public void setColor(V view, int color) {
                throw new IllegalArgumentException("itemIconTint must be a color state list");
            }

            @Override
            public void setColor(V view, ColorStateList colors) {
                view.setItemTextColor(colors);
            }
        });

        addAttributeProcessor("menu", new AttributeProcessor<V>() {
            @Override
            public void handleValue(V view, Value value) {
                throw new IllegalArgumentException("menu must be a R.menu.<sid>");
            }

            @Override
            public void handleResource(V view, Resource resource) {
                Integer id = resource.getInteger(view.getContext());
                if (null != id) {
                    view.inflateMenu(id);
                }
            }

            @Override
            public void handleAttributeResource(V view, AttributeResource attribute) {
                TypedArray a = attribute.apply(view.getContext());
                int id = a.getResourceId(0, 0);
                view.inflateMenu(id);
            }

            @Override
            public void handleStyleResource(V view, StyleResource style) {
                TypedArray a = style.apply(view.getContext());
                int id = a.getResourceId(0, 0);
                view.inflateMenu(id);
            }
        });
    }
}
