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

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.flipkart.android.proteus.AttributeProcessor;
import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusLayoutInflater;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.manager.ProteusViewManager;
import com.flipkart.android.proteus.processor.BooleanAttributeProcessor;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.toolbox.Attributes;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Result;
import com.flipkart.android.proteus.value.AttributeResource;
import com.flipkart.android.proteus.value.Binding;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.flipkart.android.proteus.value.Resource;
import com.flipkart.android.proteus.value.StyleResource;
import com.flipkart.android.proteus.value.Value;
import com.flipkart.android.proteus.view.ProteusAspectRatioFrameLayout;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Iterator;

public class ViewGroupParser<T extends ViewGroup> extends ViewTypeParser<T> {

    private static final String LAYOUT_MODE_CLIP_BOUNDS = "clipBounds";
    private static final String LAYOUT_MODE_OPTICAL_BOUNDS = "opticalBounds";

    @Override
    public ProteusView createView(@NonNull ProteusContext context, @NonNull Layout layout, @NonNull JsonObject data, @Nullable ViewGroup parent, int dataIndex) {
        return new ProteusAspectRatioFrameLayout(context);
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor(Attributes.ViewGroup.ClipChildren, new BooleanAttributeProcessor<T>() {
            @Override
            public void setBoolean(T view, boolean value) {
                view.setClipChildren(value);
            }
        });

        addAttributeProcessor(Attributes.ViewGroup.ClipToPadding, new BooleanAttributeProcessor<T>() {
            @Override
            public void setBoolean(T view, boolean value) {
                view.setClipToPadding(value);
            }
        });

        addAttributeProcessor(Attributes.ViewGroup.LayoutMode, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    if (LAYOUT_MODE_CLIP_BOUNDS.equals(value)) {
                        view.setLayoutMode(ViewGroup.LAYOUT_MODE_CLIP_BOUNDS);
                    } else if (LAYOUT_MODE_OPTICAL_BOUNDS.equals(value)) {
                        view.setLayoutMode(ViewGroup.LAYOUT_MODE_OPTICAL_BOUNDS);
                    }
                }
            }
        });

        addAttributeProcessor(Attributes.ViewGroup.SplitMotionEvents, new BooleanAttributeProcessor<T>() {
            @Override
            public void setBoolean(T view, boolean value) {
                view.setMotionEventSplittingEnabled(value);
            }
        });

        addAttributeProcessor(Attributes.ViewGroup.Children, new AttributeProcessor<T>() {
            @Override
            public void handleValue(T view, Value value) {
                handleChildren((ProteusView) view, value);
            }

            @Override
            public void handleResource(T view, Resource resource) {
                throw new IllegalArgumentException("children cannot be a resource");
            }

            @Override
            public void handleAttributeResource(T view, AttributeResource attribute) {
                throw new IllegalArgumentException("children cannot be a resource");
            }

            @Override
            public void handleStyleResource(T view, StyleResource style) {
                throw new IllegalArgumentException("children cannot be a style attribute");
            }
        });
    }

    @Override
    public boolean handleChildren(ProteusView view, Value children) {
        ProteusViewManager viewManager = view.getViewManager();
        ProteusLayoutInflater layoutInflater = viewManager.getContext().getInflater();
        JsonObject data = viewManager.getScope().getData();
        int dataIndex = viewManager.getScope().getIndex();

        if (children.isArray()) {
            ProteusView child;
            Iterator<Value> iterator = children.getAsArray().iterator();
            while (iterator.hasNext()) {
                child = layoutInflater.inflate(iterator.next().getAsLayout(), data, (ViewGroup) view, dataIndex);
                addView(view, child);
            }
        } else if (children.isObject()) {
            handleDataDrivenChildren(layoutInflater, view, viewManager, children.getAsObject(), data, dataIndex);
        }

        return true;
    }

    private void handleDataDrivenChildren(ProteusLayoutInflater layoutInflater, ProteusView parent, ProteusViewManager viewManager,
                                          ObjectValue children, JsonObject data, int dataIndex) {

        //noinspection ConstantConditions : We want to throw an exception (for now)
        String binding = children.getAsString(ProteusConstants.DATA).substring(1);
        viewManager.setDataPathForChildren(binding);

        Result result = Binding.valueOf(binding).getExpression(0).evaluate(data, dataIndex);
        JsonElement element = result.isSuccess() ? result.element : null;

        Layout childLayout = children.getAsLayout(ProteusConstants.LAYOUT);

        viewManager.setChildLayout(childLayout);

        if (null == element || element.isJsonNull()) {
            return;
        }

        int length = element.getAsJsonArray().size();

        ProteusView child;
        for (int index = 0; index < length; index++) {
            child = layoutInflater.inflate(childLayout, data, (ViewGroup) parent, index);
            if (child != null) {
                this.addView(parent, child);
            }
        }
    }

    @Override
    public boolean addView(ProteusView parent, ProteusView view) {
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).addView(view.getAsView());
            return true;
        }
        return false;
    }
}
