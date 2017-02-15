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

package com.flipkart.android.proteus.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.view.View;

import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.toolbox.BoundAttribute;
import com.flipkart.android.proteus.toolbox.Scope;
import com.flipkart.android.proteus.value.Layout;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * ProteusViewManager
 *
 * @author aditya.sharat
 */
public interface ProteusViewManager {

    /**
     * Update the {@link android.view.View} with new data.
     *
     * @param data New data for the view
     */
    void update(@Nullable JsonObject data);

    @NonNull
    ProteusContext getContext();

    /**
     * @return
     */
    @NonNull
    ViewTypeParser getParser();

    /**
     * @return
     */
    @NonNull
    Layout getLayout();

    /**
     * @return
     */
    @NonNull
    Scope getScope();

    /**
     * @param id
     * @return
     */
    int getUniqueViewId(@NonNull String id);

    /**
     * @param id
     * @return
     */
    @Nullable
    View findViewById(@NonNull String id);

    /**
     * @param dataPath
     * @param index
     * @return
     */
    JsonElement get(@NonNull @Size(min = 2) String dataPath, int index);

    /**
     * @param dataPath
     * @param newValue
     */
    void set(@NonNull @Size(min = 2) String dataPath, JsonElement newValue);

    /**
     * @param dataPath
     * @param newValue
     */
    void set(@NonNull @Size(min = 2) String dataPath, String newValue);

    /**
     * @param dataPath
     * @param newValue
     */
    void set(@NonNull @Size(min = 2) String dataPath, Number newValue);

    /**
     * @param dataPath
     * @param newValue
     */
    void set(@NonNull @Size(min = 2) String dataPath, boolean newValue);

    /**
     * @return
     */
    @Nullable
    Layout getChildLayout();

    /**
     * @param layout
     */
    void setChildLayout(@Nullable Layout layout);

    /**
     * @return
     */
    @Nullable
    String getDataPathForChildren();

    /**
     * @param dataPathForChildren
     */
    void setDataPathForChildren(@Nullable String dataPathForChildren);

    /**
     * @return
     */
    boolean isViewUpdating();

    /**
     * @param boundAttribute
     */
    void addBinding(@NonNull BoundAttribute boundAttribute);

    /**
     * Free all resources held by the view manager
     */
    void destroy();

    /**
     * @param listener
     */
    void setOnUpdateCallback(@Nullable OnUpdateCallback listener);

    /**
     *
     */
    void removeOnUpdateDataListener();

    @Nullable
    OnUpdateCallback getOnUpdateDataListeners();

    /**
     *
     */
    interface OnUpdateCallback {

        JsonObject onBeforeUpdateData(@Nullable JsonObject data);

        JsonObject onAfterDataContext(@Nullable JsonObject data);

        void onUpdateDataComplete(@Nullable JsonObject data);
    }
}
