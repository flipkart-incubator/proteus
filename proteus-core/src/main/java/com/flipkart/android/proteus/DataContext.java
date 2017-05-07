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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.flipkart.android.proteus.value.Null;
import com.flipkart.android.proteus.value.ObjectValue;
import com.flipkart.android.proteus.value.Value;

import java.util.Map;

/**
 * @author Aditya Sharat
 */
public class DataContext {

    private final boolean hasOwnProperties;

    @Nullable
    private final Map<String, Value> scope;

    private final int index;

    private ObjectValue data;

    private DataContext(@Nullable Map<String, Value> scope, int index) {
        this.scope = scope;
        this.index = index;
        this.hasOwnProperties = scope != null;
    }

    public DataContext(DataContext dataContext) {
        this.data = dataContext.getData();
        this.scope = dataContext.getScope();
        this.index = dataContext.getIndex();
        this.hasOwnProperties = false;
    }

    public static DataContext create(@NonNull Context context, @Nullable ObjectValue data, int dataIndex) {
        DataContext dataContext = new DataContext(null, dataIndex);
        dataContext.update(context, data);
        return dataContext;
    }

    public static DataContext create(@NonNull Context context, @Nullable ObjectValue data,
                                     int dataIndex, @Nullable Map<String, Value> scope) {
        DataContext dataContext = new DataContext(scope, dataIndex);
        dataContext.update(context, data);
        return dataContext;
    }

    public void update(@NonNull Context context, @Nullable ObjectValue in) {
        if (in == null) {
            in = new ObjectValue();
        }

        if (scope == null) {
            data = in;
            return;
        }

        ObjectValue out = new ObjectValue();

        for (Map.Entry<String, Value> entry : scope.entrySet()) {
            String key = entry.getKey();
            Value value = entry.getValue();
            Value resolved;
            if (value.isBinding()) {
                resolved = value.getAsBinding().evaluate(context, out, index);
                if (resolved == Null.INSTANCE) {
                    resolved = value.getAsBinding().evaluate(context, in, index);
                }
            } else {
                resolved = value;
            }
            out.add(key, resolved);
        }

        data = out;
    }

    public DataContext createChild(@NonNull Context context, @NonNull Map<String, Value> scope, int dataIndex) {
        return create(context, data, dataIndex, scope);
    }

    public DataContext copy() {
        return new DataContext(this);
    }

    public ObjectValue getData() {
        return data;
    }

    public void setData(ObjectValue data) {
        this.data = data;
    }

    @Nullable
    public Map<String, Value> getScope() {
        return scope;
    }

    public boolean hasOwnProperties() {
        return hasOwnProperties;
    }

    public int getIndex() {
        return index;
    }
}
