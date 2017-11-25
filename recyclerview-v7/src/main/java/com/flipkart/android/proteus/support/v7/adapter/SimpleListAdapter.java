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

package com.flipkart.android.proteus.support.v7.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.flipkart.android.proteus.DataContext;
import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusLayoutInflater;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.support.v7.widget.ProteusRecyclerView;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.flipkart.android.proteus.value.Value;

import java.util.Map;

/**
 * SimpleListAdapter.
 *
 * @author adityasharat
 */
public class SimpleListAdapter extends ProteusRecyclerViewAdapter<ProteusViewHolder> {

    private static final String ATTRIBUTE_ITEM_LAYOUT = "item-layout";
    private static final String ATTRIBUTE_ITEM_COUNT = "item-count";

    public static final Builder<SimpleListAdapter> BUILDER = new Builder<SimpleListAdapter>() {
        @NonNull
        @Override
        public SimpleListAdapter create(@NonNull ProteusRecyclerView view, @NonNull ObjectValue config) {
            Layout layout = config.getAsObject().getAsLayout(ATTRIBUTE_ITEM_LAYOUT);
            Integer count = config.getAsObject().getAsInteger(ATTRIBUTE_ITEM_COUNT);
            ObjectValue data = view.getViewManager().getDataContext().getData();
            ProteusContext context = (ProteusContext) view.getContext();

            return new SimpleListAdapter(context.getInflater(), data, layout, count != null ? count : 0);
        }
    };

    private ProteusLayoutInflater inflater;

    private ObjectValue data;
    private int count;
    private Layout layout;
    private Map<String, Value> scope;

    private SimpleListAdapter(ProteusLayoutInflater inflater, ObjectValue data, Layout layout, int count) {
        this.inflater = inflater;
        this.data = data;
        this.count = count;
        this.layout = new Layout(layout.type, layout.attributes, null, layout.extras);
        this.scope = layout.data;
    }

    @Override
    public ProteusViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        ProteusView view = inflater.inflate(layout, new ObjectValue());
        return new ProteusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProteusViewHolder holder, int position) {
        DataContext context = DataContext.create(holder.context, data, position, scope);
        holder.view.getViewManager().update(context.getData());
    }

    @Override
    public int getItemCount() {
        return count;
    }
}
