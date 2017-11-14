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

package com.flipkart.android.proteus.support.v7.widget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.flipkart.android.proteus.DataContext;
import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.managers.AdapterBasedViewManager;
import com.flipkart.android.proteus.processor.AttributeProcessor;
import com.flipkart.android.proteus.support.v7.adapter.ProteusRecyclerViewAdapter;
import com.flipkart.android.proteus.support.v7.adapter.RecyclerViewAdapterFactory;
import com.flipkart.android.proteus.value.AttributeResource;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.flipkart.android.proteus.value.Resource;
import com.flipkart.android.proteus.value.StyleResource;
import com.flipkart.android.proteus.value.Value;

/**
 * RecyclerViewParser
 *
 * @author adityasharat
 */

public class RecyclerViewParser<V extends RecyclerView> extends ViewTypeParser<V> {

    private static final String ATTRIBUTE_TYPE = "type";

    @NonNull
    private final RecyclerViewAdapterFactory factory;

    public RecyclerViewParser(@NonNull RecyclerViewAdapterFactory factory) {
        this.factory = factory;
    }

    @NonNull
    @Override
    public String getType() {
        return "RecyclerView";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "ViewGroup";
    }

    @NonNull
    @Override
    public ProteusView createView(@NonNull ProteusContext context, @NonNull Layout layout, @NonNull ObjectValue data, @Nullable ViewGroup parent, int dataIndex) {
        return new ProteusRecyclerView(context);
    }

    @NonNull
    @Override
    public ProteusView.Manager createViewManager(@NonNull ProteusContext context, @NonNull ProteusView view, @NonNull Layout layout,
                                                 @NonNull ObjectValue data, @Nullable ViewTypeParser caller, @Nullable ViewGroup parent,
                                                 int dataIndex) {
        DataContext dataContext = createDataContext(context, layout, data, parent, dataIndex);
        return new AdapterBasedViewManager(context, null != caller ? caller : this, view.getAsView(), layout, dataContext);
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor("adapter", new AttributeProcessor<V>() {

            @Override
            public void handleValue(V view, Value value) {
                if (value.isObject()) {
                    String type = value.getAsObject().getAsString(ATTRIBUTE_TYPE);
                    if (type != null) {
                        ProteusRecyclerViewAdapter adapter = factory.create(type, (ProteusRecyclerView) view, value.getAsObject());
                        view.setAdapter(adapter);
                        view.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    }
                }
            }

            @Override
            public void handleResource(V view, Resource resource) {
                throw new IllegalArgumentException("Recycler View 'adapter' expects only object values");
            }

            @Override
            public void handleAttributeResource(V view, AttributeResource attribute) {
                throw new IllegalArgumentException("Recycler View 'adapter' expects only object values");
            }

            @Override
            public void handleStyleResource(V view, StyleResource style) {
                throw new IllegalArgumentException("Recycler View 'adapter' expects only object values");
            }
        });

    }
}
