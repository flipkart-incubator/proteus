/*
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.android.proteus.view.manager;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.DataContext;
import com.flipkart.android.proteus.binding.Binding;
import com.flipkart.android.proteus.builder.LayoutBuilder;
import com.flipkart.android.proteus.parser.LayoutHandler;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Result;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.toolbox.Utils;
import com.flipkart.android.proteus.view.ProteusView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;

/**
 * ProteusViewManagerImpl
 *
 * @author aditya.sharat
 */
public class ProteusViewManagerImpl implements ProteusViewManager {

    private static final String TAG = "ProteusViewManagerImpl";
    private View view;
    private JsonObject layout;
    private Styles styles;
    private DataContext dataContext;
    private LayoutBuilder layoutBuilder;
    private LayoutHandler layoutHandler;
    private OnUpdateDataListener onUpdateDataListener;
    private String dataPathForChildren;
    private JsonObject childLayout;
    private boolean isViewUpdating;
    private ArrayList<Binding> bindings;

    @Override
    public void update(@Nullable JsonObject data) {
        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "START: update data " + (data != null ? "(top-level)" : "") + "for view with " + Utils.getLayoutIdentifier(layout));
        }
        this.isViewUpdating = true;

        data = onBeforeUpdateData(data);

        // update the data context so all child views can refer to new data
        if (data != null) {
            updateDataContext(data);
        }

        data = onAfterDataContext(dataContext.getData());

        // update the bindings of this view
        if (this.bindings != null) {
            for (Binding binding : this.bindings) {
                this.handleBinding(binding);
            }
        }

        // update the child views
        if (view instanceof ViewGroup) {
            if (dataPathForChildren != null) {
                updateChildrenFromData();
            } else {
                ViewGroup parent = (ViewGroup) view;
                View child;
                int childCount = parent.getChildCount();

                for (int index = 0; index < childCount; index++) {
                    child = parent.getChildAt(index);
                    if (child instanceof ProteusView) {
                        ((ProteusView) child).getViewManager().update(dataContext.getData());
                    }
                }
            }
        }

        this.isViewUpdating = false;
        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "END: update data " + (data != null ? "(top-level)" : "") + "for view with " + Utils.getLayoutIdentifier(layout));
        }

        onUpdateDataComplete(dataContext.getData());
    }

    @Override
    public void setView(View view) {
        this.view = view;
    }

    private void updateDataContext(JsonObject data) {
        if (dataContext.isClone()) {
            dataContext.setData(data);
        } else {
            dataContext.updateDataContext(data);
        }
    }

    private void updateChildrenFromData() {
        JsonArray dataList = new JsonArray();
        ViewGroup parent = ((ViewGroup) view);
        Result result = Utils.readJson(dataPathForChildren, dataContext.getData(), dataContext.getIndex());
        if (result.isSuccess() && null != result.element && result.element.isJsonArray()) {
            dataList = result.element.getAsJsonArray();
        }

        int childCount = parent.getChildCount();
        View child;

        if (childCount > dataList.size()) {
            while (childCount > dataList.size()) {
                childCount--;
                child = parent.getChildAt(childCount);
                if (child instanceof ProteusView) {
                    ((ProteusView) child).getViewManager().destroy();
                }
                parent.removeViewAt(childCount);
            }
        }

        JsonObject data = dataContext.getData();
        ProteusView childView;
        childCount = parent.getChildCount();

        for (int index = 0; index < dataList.size(); index++) {
            if (index < childCount) {
                child = parent.getChildAt(index);
                if (child instanceof ProteusView) {
                    ((ProteusView) child).getViewManager().update(data);
                }
            } else if (childLayout != null) {
                childView = layoutBuilder.build(parent, childLayout, data, dataContext.getIndex(), styles);
                layoutHandler.addView((ProteusView) view, childView);
            }
        }
    }

    @Nullable
    private JsonObject onBeforeUpdateData(JsonObject data) {
        if (onUpdateDataListener != null) {
            JsonObject override = onUpdateDataListener.onBeforeUpdateData(data);
            if (override != null) {
                return override;
            }
        }
        return data;
    }

    @Nullable
    private JsonObject onAfterDataContext(JsonObject data) {
        if (onUpdateDataListener != null) {
            JsonObject override = onUpdateDataListener.onAfterDataContext(data);
            if (override != null) {
                return override;
            }
        }
        return data;
    }

    private void onUpdateDataComplete(JsonObject data) {
        if (onUpdateDataListener != null) {
            onUpdateDataListener.onUpdateDataComplete(data);
        }
    }

    private void handleBinding(Binding binding) {
        if (binding.hasRegEx()) {
            layoutBuilder.handleAttribute(layoutHandler, (ProteusView) view, binding.getAttributeKey(), new JsonPrimitive(binding.getAttributeValue()));
        } else {
            Result result = Utils.readJson(binding.getBindingName(), dataContext.getData(), dataContext.getIndex());
            JsonElement dataValue = result.isSuccess() ? result.element : JsonNull.INSTANCE;
            layoutBuilder.handleAttribute(layoutHandler, (ProteusView) view, binding.getAttributeKey(), dataValue);
        }
    }

    @Override
    public LayoutBuilder getLayoutBuilder() {
        return layoutBuilder;
    }

    @Override
    public void setLayoutBuilder(LayoutBuilder layoutBuilder) {
        this.layoutBuilder = layoutBuilder;
    }

    @Override
    public LayoutHandler getLayoutHandler() {
        return layoutHandler;
    }

    @Override
    public void setLayoutHandler(LayoutHandler layoutHandler) {
        this.layoutHandler = layoutHandler;
    }

    @Override
    public JsonObject getLayout() {
        return layout;
    }

    @Override
    public void setLayout(JsonObject layout) {
        this.layout = layout;
    }

    @Nullable
    @Override
    public Styles getStyles() {
        return styles;
    }

    @Override
    public void setStyles(@Nullable Styles styles) {
        this.styles = styles;
    }

    @Override
    public int getUniqueViewId(String id) {
        return layoutBuilder.getUniqueViewId(id);
    }

    public JsonElement get(String dataPath, int index) {
        return dataContext.get(dataPath);
    }

    public void set(String dataPath, JsonElement newValue) {
        if (dataPath == null) {
            return;
        }

        String aliasedDataPath = DataContext.getAliasedDataPath(dataPath, dataContext.getReverseScope(), true);
        Result result = Utils.readJson(aliasedDataPath.substring(0, aliasedDataPath.lastIndexOf(".")), dataContext.getData(), dataContext.getIndex());
        JsonElement parent = result.isSuccess() ? result.element : null;
        if (parent == null || !parent.isJsonObject()) {
            return;
        }

        String propertyName = aliasedDataPath.substring(aliasedDataPath.lastIndexOf(".") + 1, aliasedDataPath.length());
        parent.getAsJsonObject().add(propertyName, newValue);

        update(aliasedDataPath);
    }

    public void set(String dataPath, String newValue) {
        set(dataPath, new JsonPrimitive(newValue));
    }

    public void set(String dataPath, Number newValue) {
        set(dataPath, new JsonPrimitive(newValue));
    }

    public void set(String dataPath, boolean newValue) {
        set(dataPath, new JsonPrimitive(newValue));
    }

    protected void update(String dataPath) {
        this.isViewUpdating = true;

        if (this.bindings != null) {
            for (Binding binding : this.bindings) {
                if (binding.getBindingName().equals(dataPath)) {
                    this.handleBinding(binding);
                }
            }
        }

        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            int childCount = parent.getChildCount();
            View child;
            String aliasedDataPath;

            for (int index = 0; index < childCount; index++) {
                child = parent.getChildAt(index);
                if (child instanceof ProteusView) {
                    aliasedDataPath = DataContext.getAliasedDataPath(dataPath, dataContext.getReverseScope(), false);
                    ((ProteusViewManagerImpl) ((ProteusView) child).getViewManager()).update(aliasedDataPath);
                }
            }
        }

        this.isViewUpdating = false;
    }

    @Nullable
    @Override
    public JsonObject getChildLayout() {
        return childLayout;
    }

    public void setChildLayout(@Nullable JsonObject childLayout) {
        this.childLayout = childLayout;
    }

    @Override
    public DataContext getDataContext() {
        return dataContext;
    }

    @Override
    public void setDataContext(DataContext dataContext) {
        this.dataContext = dataContext;
    }

    @Nullable
    @Override
    public String getDataPathForChildren() {
        return dataPathForChildren;
    }

    public void setDataPathForChildren(@Nullable String dataPathForChildren) {
        this.dataPathForChildren = dataPathForChildren;
    }

    public boolean isViewUpdating() {
        return isViewUpdating;
    }

    @Override
    public void addBinding(Binding binding) {
        if (this.bindings == null) {
            this.bindings = new ArrayList<>();
        }
        bindings.add(binding);
    }

    @Override
    public void destroy() {
        view = null;
        layout = null;
        childLayout = null;
        styles = null;
        layoutBuilder = null;
        layoutHandler = null;
        onUpdateDataListener = null;
        dataPathForChildren = null;
        bindings = null;
    }

    @Override
    public void setOnUpdateDataListener(@Nullable OnUpdateDataListener listener) {
        this.onUpdateDataListener = listener;
    }

    @Override
    public void removeOnUpdateDataListener() {
        onUpdateDataListener = null;
    }

    @Nullable
    @Override
    public OnUpdateDataListener getOnUpdateDataListeners() {
        return onUpdateDataListener;
    }
}
