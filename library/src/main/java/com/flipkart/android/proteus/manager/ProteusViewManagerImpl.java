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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.toolbox.BoundAttribute;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Result;
import com.flipkart.android.proteus.toolbox.Scope;
import com.flipkart.android.proteus.toolbox.Utils;
import com.flipkart.android.proteus.value.Layout;
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

    @NonNull
    private final ProteusContext context;

    @NonNull
    private final View view;

    @NonNull
    private final Layout layout;

    @NonNull
    private final Scope scope;

    @NonNull
    private final ViewTypeParser parser;


    private String dataPathForChildren;
    private Layout childLayout;

    private boolean isViewUpdating;

    private ArrayList<BoundAttribute> boundAttributes;

    public ProteusViewManagerImpl(@NonNull ProteusContext context, @NonNull ViewTypeParser parser,
                                  @NonNull View view, @NonNull Layout layout, @NonNull Scope scope) {
        this.context = context;
        this.parser = parser;
        this.view = view;
        this.layout = layout;
        this.scope = scope;
    }

    @Override
    public void update(@Nullable JsonObject data) {
        if (ProteusConstants.isLoggingEnabled()) {
            Log.d(TAG, "START: update data " + (data != null ? "(top-level)" : "") + "for view with " + getLayout());
        }
        this.isViewUpdating = true;

        // update the data context so all child views can refer to new data
        if (data != null) {
            updateDataContext(data);
        }

        // update the boundAttributes of this view
        if (this.boundAttributes != null) {
            for (BoundAttribute boundAttribute : this.boundAttributes) {
                this.handleBinding(boundAttribute);
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
    }

    @NonNull
    @Override
    public ProteusContext getContext() {
        return this.context;
    }

    @NonNull
    @Override
    public ViewTypeParser getParser() {
        return parser;
    }

    @NonNull
    @Override
    public Layout getLayout() {
        return this.layout;
    }

    @NonNull
    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public int getUniqueViewId(@NonNull String id) {
        return context.getInflater().getUniqueViewId(id);
    }

    @Nullable
    @Override
    public View findViewById(@NonNull String id) {
        return view.findViewById(getUniqueViewId(id));
    }

    @Override
    public JsonElement get(@NonNull String dataPath, int index) {
        return scope.get(dataPath);
    }

    @Override
    public void set(@NonNull String dataPath, JsonElement newValue) {
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

    @Override
    public void set(@NonNull String dataPath, String newValue) {
        set(dataPath, new JsonPrimitive(newValue));
    }

    @Override
    public void set(@NonNull String dataPath, Number newValue) {
        set(dataPath, new JsonPrimitive(newValue));
    }

    @Override
    public void set(@NonNull String dataPath, boolean newValue) {
        set(dataPath, new JsonPrimitive(newValue));
    }

    protected void update(String dataPath) {
        this.isViewUpdating = true;

        if (this.boundAttributes != null) {
            for (BoundAttribute boundAttribute : this.boundAttributes) {
                if (boundAttribute.getBindingName().equals(dataPath)) {
                    this.handleBinding(boundAttribute);
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

    @Override
    public void setChildLayout(@Nullable Layout layout) {
        this.childLayout = layout;
    }

    @Nullable
    @Override
    public String getDataPathForChildren() {
        return dataPathForChildren;
    }

    @Override
    public void setDataPathForChildren(@Nullable String dataPathForChildren) {
        this.dataPathForChildren = dataPathForChildren;
    }

    @Override
    public boolean isViewUpdating() {
        return isViewUpdating;
    }

    @Override
    public void addBinding(@NonNull BoundAttribute boundAttribute) {
        if (this.boundAttributes == null) {
            this.boundAttributes = new ArrayList<>();
        }
        boundAttributes.add(boundAttribute);
    }

    @Override
    public void destroy() {
        childLayout = null;
        dataPathForChildren = null;
        boundAttributes = null;
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
                childView = context.getInflater().inflate(getLayout(), data, parent, scope.getIndex());
                parser.addView((ProteusView) view, childView);
            }
        }
    }

    private void handleBinding(BoundAttribute boundAttribute) {
        if (boundAttribute.hasRegEx()) {
            parser.handleAttribute(view, boundAttribute.getAttributeId(), getLayout().create(boundAttribute.getAttributeValue()));
        } else {
            Result result = Utils.readJson(boundAttribute.getBindingName(), scope.getData(), scope.getIndex());
            JsonElement dataValue = result.isSuccess() ? result.element : JsonNull.INSTANCE;
            parser.handleAttribute(view, boundAttribute.getAttributeId(), getLayout().create(dataValue));
        }
    }
}
