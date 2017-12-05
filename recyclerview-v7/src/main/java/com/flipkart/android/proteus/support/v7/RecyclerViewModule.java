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
import com.flipkart.android.proteus.support.v7.layoutmanager.LayoutManagerFactory;
import com.flipkart.android.proteus.support.v7.layoutmanager.ProteusLinearLayoutManager;
import com.flipkart.android.proteus.support.v7.widget.RecyclerViewParser;

/**
 * <p>
 * RecyclerView Module contains the attribute processors, Layout Manager and Adapter Factories, and
 * their default implementations for the {@link android.support.v7.widget.RecyclerView}.
 * </p>
 *
 * @author adityasharat
 * @see RecyclerViewAdapterFactory
 * @see LayoutManagerFactory
 */
public class RecyclerViewModule implements ProteusBuilder.Module {

    static final String ADAPTER_SIMPLE_LIST = "SimpleListAdapter";

    static final String LAYOUT_MANAGER_LINEAR = "LinearLayoutManager";

    @NonNull
    private final RecyclerViewAdapterFactory adapterFactory;

    @NonNull
    private final LayoutManagerFactory layoutManagerFactory;

    /**
     * <p>
     * Returns a new instance of the Recycler View Module.
     * </p>
     *
     * @param adapterFactory       The adapter factory to be used to evaluate the 'adapter' attribute.
     * @param layoutManagerFactory The layout manager factory to evaluate the 'layout_manager' attribute.
     */
    RecyclerViewModule(@NonNull RecyclerViewAdapterFactory adapterFactory, @NonNull LayoutManagerFactory layoutManagerFactory) {
        this.adapterFactory = adapterFactory;
        this.layoutManagerFactory = layoutManagerFactory;
    }

    /**
     * <p>
     * The default constructor method to create a new instance of this class. This method internally
     * uses the {@link Builder} and registers the default Adapters and Layout Managers of the
     * Recycler View.
     * </p>
     *
     * @return Returns a new instance of the module with default implementations registered.
     * @see SimpleListAdapter
     * @see ProteusLinearLayoutManager
     */
    public static RecyclerViewModule create() {
        return new Builder().build();
    }

    @Override
    public void registerWith(ProteusBuilder builder) {
        builder.register(new RecyclerViewParser(adapterFactory, layoutManagerFactory));
    }

    /**
     * Use the Recycler View Module Builder to register custom {@link ProteusRecyclerViewAdapter}
     * implementations. A default {@link ProteusRecyclerViewAdapter} and
     * {@link android.support.v7.widget.RecyclerView.LayoutManager} are included by default. To
     * exclude them call {@link #excludeDefaultAdapters()} and {@link #excludeDefaultLayoutManagers()}.
     *
     * @author adityasharat
     * @see ProteusRecyclerViewAdapter
     * @see RecyclerViewAdapterFactory
     * @see SimpleListAdapter
     * @see LayoutManagerFactory
     * @see ProteusLinearLayoutManager
     */
    public static class Builder {

        @NonNull
        private final RecyclerViewAdapterFactory adapterFactory = new RecyclerViewAdapterFactory();

        @NonNull
        private LayoutManagerFactory layoutManagerFactory = new LayoutManagerFactory();

        private boolean includeDefaultAdapters = true;

        private boolean includeDefaultLayoutManagers = true;

        /**
         * <p>
         * Registers a new {@link ProteusRecyclerViewAdapter}.
         * </p>
         *
         * @param type    The 'type' of the adapter which will be used in the 'adapter' attribute..
         * @param builder The builder for the adapter.
         * @return this builder.
         */
        public Builder register(@NonNull String type, @NonNull ProteusRecyclerViewAdapter.Builder builder) {
            adapterFactory.register(type, builder);
            return this;
        }

        private void registerDefaultAdapters() {
            adapterFactory.register(ADAPTER_SIMPLE_LIST, SimpleListAdapter.BUILDER);
        }

        private void registerDefaultLayoutManagers() {
            layoutManagerFactory.register(LAYOUT_MANAGER_LINEAR, ProteusLinearLayoutManager.BUILDER);
        }

        /**
         * <p>
         * Will exclude the default {@link ProteusRecyclerViewAdapter} implementations from the module.
         * </p>
         *
         * @return this builder.
         */
        @SuppressWarnings("WeakerAccess")
        public Builder excludeDefaultAdapters() {
            includeDefaultAdapters = false;
            return this;
        }

        /**
         * <p>
         * Will exclude the default {@link android.support.v7.widget.RecyclerView.LayoutManager}
         * implementations from the module.
         * </p>
         *
         * @return this builder.
         */
        @SuppressWarnings("WeakerAccess")
        public Builder excludeDefaultLayoutManagers() {
            includeDefaultLayoutManagers = false;
            return this;
        }

        /**
         * <p>
         * Returns a new instance of {@link RecyclerViewModule}.
         * </p>
         *
         * @return a new instance of {@link RecyclerViewModule}.
         */
        @SuppressWarnings("WeakerAccess")
        public RecyclerViewModule build() {

            if (includeDefaultAdapters) {
                registerDefaultAdapters();
            }

            if (includeDefaultLayoutManagers) {
                registerDefaultLayoutManagers();
            }

            return new RecyclerViewModule(adapterFactory, layoutManagerFactory);
        }
    }
}
