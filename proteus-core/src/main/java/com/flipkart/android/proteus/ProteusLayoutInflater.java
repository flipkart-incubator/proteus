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

package com.flipkart.android.proteus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.flipkart.android.proteus.toolbox.DrawableCallback;
import com.flipkart.android.proteus.toolbox.EventType;
import com.flipkart.android.proteus.toolbox.IdGenerator;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.Value;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * @author kirankumar
 * @author adityasharat
 */
public interface ProteusLayoutInflater {

    /**
     * This methods builds a {@link ProteusView} from a layout {@link JsonObject} and data {@link JsonObject}.
     *
     * @param layout    The {@link Layout} which defines the layout for the {@link View} to be built.
     * @param data      The {@link JsonObject} which will be used to replace bindings with values in the {@link View}.
     * @param parent    The intended parent view for the {@link View} that will be built.
     * @param dataIndex The index of this view in its parent. Pass 0 if it has no parent.
     * @return An native android view
     */
    @NonNull
    ProteusView inflate(@NonNull Layout layout, @NonNull JsonObject data, @Nullable ViewGroup parent, int dataIndex);

    /**
     * This methods builds a {@link ProteusView} from a layout {@link JsonObject} and data {@link JsonObject}.
     *
     * @param layout    The {@link Layout} which defines the layout for the {@link View} to be built.
     * @param data      The {@link JsonObject} which will be used to replace bindings with values in the {@link View}.
     * @param dataIndex The index of this view in its parent. Pass 0 if it has no parent.
     * @return An native android view
     */
    @NonNull
    ProteusView inflate(@NonNull Layout layout, @NonNull JsonObject data, int dataIndex);

    /**
     * This methods builds a {@link ProteusView} from a layout {@link JsonObject} and data {@link JsonObject}.
     *
     * @param layout The {@link Layout} which defines the layout for the {@link View} to be built.
     * @param data   The {@link JsonObject} which will be used to replace bindings with values in the {@link View}.
     * @return An native android view
     */
    @NonNull
    ProteusView inflate(@NonNull Layout layout, @NonNull JsonObject data);

    /**
     *
     * @param name
     * @param data
     * @param parent
     * @param dataIndex
     * @return
     */
    @NonNull
    ProteusView inflate(@NonNull String name, @NonNull JsonObject data, @Nullable ViewGroup parent, int dataIndex);

    /**
     *
     * @param name
     * @param data
     * @param dataIndex
     * @return
     */
    @NonNull
    ProteusView inflate(@NonNull String name, @NonNull JsonObject data, int dataIndex);

    /**
     *
     * @param name
     * @param data
     * @return
     */
    @NonNull
    ProteusView inflate(@NonNull String name, @NonNull JsonObject data);

    /**
     * Returns the {@link ViewTypeParser} for the specified view type.
     *
     * @param type The name of the view type.
     * @return The {@link ViewTypeParser} associated to the specified view type
     */
    @Nullable
    ViewTypeParser getParser(@NonNull String type);

    /**
     * Give the View ID for this string. This will generally be given by the instance of ID Generator
     * which will be available with the Layout Builder.
     * This is similar to R.id auto generated
     *
     * @return int value for this id. This will never be -1.
     */
    int getUniqueViewId(@NonNull String id);

    /**
     * All consumers of this should ensure that they save the instance state of the ID generator along with the activity/
     * fragment and resume it when the Layout Builder is being re-initialized
     *
     * @return Returns the Id Generator for this Layout Builder
     */
    @NonNull
    IdGenerator getIdGenerator();

    /**
     * The Layout Inflaters callback interface
     */
    interface Callback {

        /**
         * called when the builder encounters a view type which it cannot understand.
         */
        @NonNull
        ProteusView onUnknownViewType(ProteusContext context, String type, Layout layout, JsonObject data, int index);

        /**
         * called when any click occurs on views
         *
         * @param view  The view that triggered the event
         * @param value Value set to the event attribute
         */
        @NonNull
        View onEvent(ProteusView view, EventType eventType, Value value);

        /**
         * @param parent
         * @param children
         * @param layout
         * @return Adapter
         */
        @Nullable
        PagerAdapter onPagerAdapterRequired(ProteusView parent, final List<ProteusView> children, Layout layout);

        /**
         * @param parent
         * @param children
         * @param layout
         * @return Adapter
         */
        @Nullable
        Adapter onAdapterRequired(ProteusView parent, final List<ProteusView> children, Layout layout);

    }

    /**
     * Used for loading drawables/images/bitmaps asynchronously
     */
    interface ImageLoader {
        /**
         * Useful for asynchronous download of bitmap.
         *
         * @param url      the url for the drawable/bitmap/image
         * @param callback the callback to set the drawable/bitmap
         */
        void getBitmap(ProteusView view, String url, DrawableCallback callback);
    }
}
