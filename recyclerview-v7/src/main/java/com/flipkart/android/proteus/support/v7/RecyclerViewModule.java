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

package com.flipkart.android.proteus.support.v7;

import android.support.annotation.NonNull;

import com.flipkart.android.proteus.ProteusBuilder;
import com.flipkart.android.proteus.support.v7.adapter.ProteusRecyclerViewAdapter;
import com.flipkart.android.proteus.support.v7.adapter.RecyclerViewAdapterFactory;
import com.flipkart.android.proteus.support.v7.adapter.SimpleListAdapter;
import com.flipkart.android.proteus.support.v7.widget.RecyclerViewParser;

/**
 * RecyclerViewModule.
 *
 * @author adityasharat
 */
public class RecyclerViewModule implements ProteusBuilder.Module {

    public static final String ADAPTER_TYPE_SIMPLE_LIST = "simple-list";

    @NonNull
    private RecyclerViewAdapterFactory adapterFactory;

    private RecyclerViewModule(@NonNull RecyclerViewAdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    public static RecyclerViewModule create() {
        return new Builder().includeDefaultAdapters().build();
    }

    @Override
    public void registerWith(ProteusBuilder builder) {
        builder.register(new RecyclerViewParser(adapterFactory));
    }

    /**
     * Use the Recycler View Module Builder to register custom {@link ProteusRecyclerViewAdapter}
     * implementations. A default {@link ProteusRecyclerViewAdapter} is included if you call the
     * {@link #includeDefaultAdapters()} method of the builder.
     *
     * @see ProteusRecyclerViewAdapter
     * @see RecyclerViewAdapterFactory
     * @see SimpleListAdapter
     *
     * @author adityasharat
     */
    public static class Builder {

        private final RecyclerViewAdapterFactory adapterFactory = new RecyclerViewAdapterFactory();

        public Builder register(@NonNull String type, @NonNull ProteusRecyclerViewAdapter.Builder builder) {
            adapterFactory.register(type, builder);
            return this;
        }

        public Builder includeDefaultAdapters() {
            adapterFactory.register(ADAPTER_TYPE_SIMPLE_LIST, SimpleListAdapter.BUILDER);
            return this;
        }

        public RecyclerViewModule build() {
            return new RecyclerViewModule(adapterFactory);
        }
    }
}
