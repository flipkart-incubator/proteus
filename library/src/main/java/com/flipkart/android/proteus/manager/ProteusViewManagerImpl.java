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

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.Layout;
import com.flipkart.android.proteus.ProteusLayoutInflater;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.TypeParser;
import com.flipkart.android.proteus.toolbox.Binding;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Result;
import com.flipkart.android.proteus.toolbox.Scope;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.toolbox.Utils;
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
    private Styles styles;
    private Scope scope;
    private ProteusLayoutInflater proteusLayoutInflater;
    private TypeParser typeParser;
    private OnUpdateDataListener onUpdateDataListener;
    private String dataPathForChildren;
    private Layout childLayout;
    private boolean isViewUpdating;
    private ArrayList<Binding> bindings;
    private Layout layout;

    @Override
    public void update(@Nullable JsonObject data) {
        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "START: update data " + (data != null ? "(top-level)" : "") + "for view with " + getLayout());
        }
        this.isViewUpdating = true;

        data = onBeforeUpdateData(data);

        // update the data context so all child views can refer to new data
        if (data != null) {
            updateDataContext(data);
        }

        data = onAfterDataContext(scope.getData());

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
                        ((ProteusView) child).getViewManager().update(scope.getData());
                    }
                }
            }
        }

        this.isViewUpdating = false;
        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "END: update data " + (data != null ? "(top-level)" : "") + "for view with " + getLayout());
        }

        onUpdateDataComplete(scope.getData());
    }

    @Override
    public void setView(View view) {
        this.view = view;
    }

    private void updateDataContext(JsonObject data) {
        if (scope.isClone()) {
            scope.setData(data);
        } else {
            scope.updateDataContext(data);
        }
    }

    private void updateChildrenFromData() {
        JsonArray dataList = new JsonArray();
        ViewGroup parent = ((ViewGroup) view);
        Result result = Utils.readJson(dataPathForChildren, scope.getData(), scope.getIndex());
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

        JsonObject data = scope.getData();
        ProteusView childView;
        childCount = parent.getChildCount();

        for (int index = 0; index < dataList.size(); index++) {
            if (index < childCount) {
                child = parent.getChildAt(index);
                if (child instanceof ProteusView) {
                    ((ProteusView) child).getViewManager().update(data);
                }
            } else if (childLayout != null) {
                childView = proteusLayoutInflater.inflate(parent, getLayout(), data, styles, scope.getIndex());
                typeParser.addView((ProteusView) view, childView);
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
            proteusLayoutInflater.handleAttribute(typeParser, (ProteusView) view, binding.getAttributeId(), getLayout().create(binding.getAttributeValue()));
        } else {
            Result result = Utils.readJson(binding.getBindingName(), scope.getData(), scope.getIndex());
            JsonElement dataValue = result.isSuccess() ? result.element : JsonNull.INSTANCE;
            proteusLayoutInflater.handleAttribute(typeParser, (ProteusView) view, binding.getAttributeId(), getLayout().create(dataValue));
        }
    }

    public ProteusLayoutInflater getProteusLayoutInflater() {
        return proteusLayoutInflater;
    }

    public void setProteusLayoutInflater(ProteusLayoutInflater proteusLayoutInflater) {
        this.proteusLayoutInflater = proteusLayoutInflater;
    }

    public TypeParser getTypeParser() {
        return typeParser;
    }

    public void setTypeParser(TypeParser typeParser) {
        this.typeParser = typeParser;
    }

    @Override
    public Layout getLayout() {
        return this.layout;
    }

    @Override
    public void setLayout(Layout layout) {
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
        return proteusLayoutInflater.getUniqueViewId(id);
    }

    public JsonElement get(String dataPath, int index) {
        return scope.get(dataPath);
    }

    public void set(String dataPath, JsonElement newValue) {
        if (dataPath == null) {
            return;
        }

        String aliasedDataPath = Scope.getAliasedDataPath(dataPath, scope.getReverseScope(), true);
        Result result = Utils.readJson(aliasedDataPath.substring(0, aliasedDataPath.lastIndexOf(".")), scope.getData(), scope.getIndex());
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
                    aliasedDataPath = Scope.getAliasedDataPath(dataPath, scope.getReverseScope(), false);
                    ((ProteusViewManagerImpl) ((ProteusView) child).getViewManager()).update(aliasedDataPath);
                }
            }
        }

        this.isViewUpdating = false;
    }

    @Nullable
    @Override
    public Layout getChildLayout() {
        return childLayout;
    }

    public void setChildLayout(@Nullable Layout layout) {
        this.childLayout = layout;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
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
        childLayout = null;
        styles = null;
        proteusLayoutInflater = null;
        typeParser = null;
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
