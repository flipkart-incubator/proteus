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
import android.view.View;

import com.flipkart.android.proteus.toolbox.BoundAttribute;
import com.flipkart.android.proteus.toolbox.Scope;
import com.flipkart.android.proteus.value.Layout;
import com.google.gson.JsonObject;

/**
 * ViewManager
 *
 * @author aditya.sharat
 */
public class ViewManager implements ProteusViewManager {

    @NonNull
    protected final ProteusContext context;

    @NonNull
    protected final View view;

    @NonNull
    protected final Layout layout;

    @NonNull
    protected final Scope scope;

    @NonNull
    protected final ViewTypeParser parser;

    private BoundAttribute[] boundAttributes;

    public ViewManager(@NonNull ProteusContext context, @NonNull ViewTypeParser parser,
                       @NonNull View view, @NonNull Layout layout, @NonNull Scope scope) {
        this.context = context;
        this.parser = parser;
        this.view = view;
        this.layout = layout;
        this.scope = scope;
    }

    @Override
    public void update(@Nullable JsonObject data) {
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

    @Nullable
    @Override
    public View findViewById(@NonNull String id) {
        return view.findViewById(context.getInflater().getUniqueViewId(id));
    }

    @Override
    public void destroy() {
        boundAttributes = null;
    }

    private void updateDataContext(JsonObject data) {
        if (scope.isClone()) {
            scope.setData(data);
        } else {
            scope.updateDataContext(data);
        }
    }

    private void handleBinding(BoundAttribute boundAttribute) {
        //noinspection unchecked
        parser.handleAttribute(view, boundAttribute.attributeId, boundAttribute.attributeValue);
    }
}
