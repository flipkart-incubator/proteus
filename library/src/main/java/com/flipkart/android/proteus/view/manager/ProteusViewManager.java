/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Flipkart Internet Pvt. Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.flipkart.android.proteus.view.manager;

import android.support.annotation.Nullable;
import android.view.View;

import com.flipkart.android.proteus.DataContext;
import com.flipkart.android.proteus.binding.Binding;
import com.flipkart.android.proteus.builder.LayoutBuilder;
import com.flipkart.android.proteus.parser.LayoutHandler;
import com.flipkart.android.proteus.toolbox.Styles;
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

    /**
     * Set the {@link View} which will be managed.
     * @param view The view to manage.
     */
    void setView(View view);

    LayoutBuilder getLayoutBuilder();

    void setLayoutBuilder(LayoutBuilder layoutBuilder);

    LayoutHandler getLayoutHandler();

    void setLayoutHandler(LayoutHandler layoutHandler);

    /**
     * Returns the layout used to build this {@link android.view.View}.
     *
     * @return Returns the layout used to build this {@link android.view.View}
     */
    JsonObject getLayout();

    /**
     * Sets the layout used to build this {@link android.view.View}.
     *
     * @param layout The layout used to build this {@link android.view.View}
     */
    void setLayout(JsonObject layout);

    /**
     * Returns the current {@link Styles} set in this {@link android.view.View}.
     *
     * @return Returns the {@link Styles}.
     */
    @Nullable
    Styles getStyles();

    /**
     * Sets the {@link Styles} to be applied to this {@link android.view.View}
     */
    void setStyles(@Nullable Styles styles);

    int getUniqueViewId(String id);

    JsonElement get(String dataPath, int index);

    void set(String dataPath, JsonElement newValue);

    void set(String dataPath, String newValue);

    void set(String dataPath, Number newValue);

    void set(String dataPath, boolean newValue);

    @Nullable
    JsonObject getChildLayout();

    void setChildLayout(@Nullable JsonObject childLayout);

    DataContext getDataContext();

    void setDataContext(DataContext dataContext);

    @Nullable
    String getDataPathForChildren();

    void setDataPathForChildren(@Nullable String dataPathForChildren);

    boolean isViewUpdating();

    void addBinding(Binding binding);

    /**
     * Free all resources held by the view manager
     */
    void destroy();

    void setOnUpdateDataListener(@Nullable OnUpdateDataListener listener);

    void removeOnUpdateDataListener();

    @Nullable
    OnUpdateDataListener getOnUpdateDataListeners();

    interface OnUpdateDataListener {

        JsonObject onBeforeUpdateData(@Nullable JsonObject data);

        JsonObject onAfterDataContext(@Nullable JsonObject data);

        void onUpdateDataComplete(@Nullable JsonObject data);
    }
}
