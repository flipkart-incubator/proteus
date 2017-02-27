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

import android.support.annotation.Nullable;

import com.flipkart.android.proteus.toolbox.Utils;
import com.flipkart.android.proteus.value.ObjectValue;
import com.flipkart.android.proteus.value.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Aditya Sharat
 */
public class DataContext {

    private final boolean isClone;
    private ObjectValue data;
    @Nullable
    private Map<String, Value> scope;
    private int index;

    public DataContext() {
        this.data = new ObjectValue();
        this.scope = new HashMap<>();
        this.index = -1;
        this.isClone = false;
    }

    public DataContext(DataContext dataContext) {
        this.data = dataContext.getData();
        this.scope = dataContext.getScope();
        this.index = dataContext.getIndex();
        this.isClone = true;
    }

    public static DataContext updateDataContext(DataContext context, ObjectValue in, Map<String, Value> scope, int dataIndex) {

        ObjectValue out = new ObjectValue();

        context.setIndex(dataIndex);

        if (in == null) {
            in = new ObjectValue();
        }

        for (Map.Entry<String, Value> entry : scope.entrySet()) {
            String key = entry.getKey();
            Value value = entry.getValue();
            Value resolved;
            if (value.isBinding()) {
                resolved = value.getAsBinding().evaluate(in, dataIndex);
            } else {
                resolved = value;
            }
            out.add(key, resolved);
        }

        Utils.addElements(out, in, false);

        if (context.getData() == null) {
            context.setData(new ObjectValue());
        } else {
            context.setData(out);
        }

        context.setScope(scope);

        return context;
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

    public void setScope(@Nullable Map<String, Value> scope) {
        this.scope = scope;
    }

    public boolean isClone() {
        return isClone;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public DataContext createChildScope(Map<String, Value> scope, int dataIndex) {
        return updateDataContext(new DataContext(), data, scope, dataIndex);
    }

    public void updateDataContext(ObjectValue data) {
        updateDataContext(this, data, scope, index);
    }
}
