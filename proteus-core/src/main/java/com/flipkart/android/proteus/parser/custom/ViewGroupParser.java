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
import com.flipkart.android.proteus.ProteusViewManager;
import com.flipkart.android.proteus.ViewGroupManager;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.exceptions.ProteusInflateException;
import com.flipkart.android.proteus.processor.BooleanAttributeProcessor;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.toolbox.Attributes;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Result;
import com.flipkart.android.proteus.toolbox.Scope;
import com.flipkart.android.proteus.value.AttributeResource;
import com.flipkart.android.proteus.value.Binding;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.NestedBinding;
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
    public ProteusViewManager createViewManager(@NonNull ProteusContext context, @NonNull ProteusView view, @NonNull Layout layout, @NonNull JsonObject data, @Nullable ViewGroup parent, int dataIndex) {
        Scope scope = createScope(layout, data, parent, dataIndex);
        return new ViewGroupManager(context, this, view.getAsView(), layout, scope);
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
            public void handleBinding(T view, Binding value) {
                handleDataDrivenChildren(view, value);
            }

            @Override
            public void handleValue(T view, Value value) {
                handleChildren(view, value);
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
    public boolean handleChildren(T view, Value children) {
        ProteusView proteusView = ((ProteusView) view);
        ProteusViewManager viewManager = proteusView.getViewManager();
        ProteusLayoutInflater layoutInflater = viewManager.getContext().getInflater();
        JsonObject data = viewManager.getScope().getData();
        int dataIndex = viewManager.getScope().getIndex();

        if (children.isArray()) {
            ProteusView child;
            Iterator<Value> iterator = children.getAsArray().iterator();
            while (iterator.hasNext()) {
                child = layoutInflater.inflate(iterator.next().getAsLayout(), data, view, dataIndex);
                addView(proteusView, child);
            }
        }

        return true;
    }

    private void handleDataDrivenChildren(T view, Binding value) {
        ProteusView parent = ((ProteusView) view);
        Scope scope = parent.getViewManager().getScope();
        ObjectValue config = ((NestedBinding) value).getValue().getAsObject();

        Value collection = config.get(ProteusConstants.COLLECTION);
        Layout layout = config.getAsLayout(ProteusConstants.LAYOUT);

        if (collection.isNull()) {
            return;
        }

        JsonElement dataset = null;
        if (collection.isBinding()) {
            Result result = collection.getAsBinding().getExpression(0).evaluate(scope.getData(), scope.getIndex());
            dataset = result.isSuccess() ? result.element : null;
        }

        if (null == dataset || dataset.isJsonNull()) {
            return;
        }

        if (!dataset.isJsonArray()) {
            throw new ProteusInflateException("'collection' in attribute:'children' must be NULL or Array");
        }

        int length = dataset.getAsJsonArray().size();
        ProteusLayoutInflater inflater = parent.getViewManager().getContext().getInflater();

        ProteusView child;
        for (int index = 0; index < length; index++) {
            //noinspection ConstantConditions : We want to throw an exception if the layout is null
            child = inflater.inflate(layout, scope.getData(), (ViewGroup) parent, index);
            addView(parent, child);
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
