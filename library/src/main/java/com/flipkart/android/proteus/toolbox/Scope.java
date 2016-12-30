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

package com.flipkart.android.proteus.toolbox;

import android.support.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Aditya Sharat
 */
public class Scope {

    private final boolean isClone;
    private JsonObject data;
    @Nullable
    private Map<String, String> reverseScope;
    @Nullable
    private Map<String, String> scope;
    private int index;

    public Scope() {
        this.data = new JsonObject();
        this.scope = new HashMap<>();
        this.reverseScope = new HashMap<>();
        this.index = -1;
        this.isClone = false;
    }

    public Scope(Scope scope) {
        this.data = scope.getData();
        this.scope = scope.getScope();
        this.reverseScope = scope.getReverseScope();
        this.index = scope.getIndex();
        this.isClone = true;
    }

    public static Scope updateDataContext(Scope dataContext, JsonObject data, Map<String, String> scope, int dataIndex) {
        Map<String, String> reverseScope = new HashMap<>();
        JsonObject newData = new JsonObject();

        dataContext.setIndex(dataIndex);

        if (data == null) {
            data = new JsonObject();
        }

        for (Map.Entry<String, String> entry : scope.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Result result = Utils.readJson(value, data, dataContext.getIndex());
            JsonElement element = result.isSuccess() ? result.element : new JsonObject();
            newData.add(key, element);
            String unAliasedValue = value.replace(ProteusConstants.INDEX, String.valueOf(dataContext.getIndex()));
            reverseScope.put(unAliasedValue, key);
        }

        Utils.addElements(newData, data, false);

        if (dataContext.getData() == null) {
            dataContext.setData(new JsonObject());
        } else {
            dataContext.setData(newData);
        }
        dataContext.setScope(scope);
        dataContext.setReverseScope(reverseScope);
        return dataContext;
    }

    public static String getAliasedDataPath(String dataPath, Map<String, String> reverseScope, boolean isBindingPath) {
        String[] segments;
        if (isBindingPath) {
            segments = dataPath.split(ProteusConstants.DATA_PATH_DELIMITER);
        } else {
            segments = dataPath.split(ProteusConstants.DATA_PATH_SIMPLE_DELIMITER);
        }

        if (reverseScope == null) {
            return dataPath;
        }
        String alias = reverseScope.get(segments[0]);
        if (alias == null) {
            return dataPath;
        }

        return dataPath.replaceFirst(Pattern.quote(segments[0]), alias);
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    @Nullable
    public Map<String, String> getScope() {
        return scope;
    }

    public void setScope(@Nullable Map<String, String> scope) {
        this.scope = scope;
    }

    @Nullable
    public Map<String, String> getReverseScope() {
        return reverseScope;
    }

    public void setReverseScope(@Nullable Map<String, String> reverseScope) {
        this.reverseScope = reverseScope;
    }

    public boolean isClone() {
        return isClone;
    }

    @Nullable
    public JsonElement get(String dataPath) {
        String aliasedDataPath = getAliasedDataPath(dataPath, reverseScope, true);
        Result result = Utils.readJson(aliasedDataPath, data, index);
        if (result.isSuccess()) {
            return result.element;
        } else if (result.RESULT_CODE == Result.RESULT_JSON_NULL_EXCEPTION) {
            return JsonNull.INSTANCE;
        }
        return null;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Scope createChildDataContext(Map<String, String> scope, int dataIndex) {
        return updateDataContext(new Scope(), data, scope, dataIndex);
    }

    public void updateDataContext(JsonObject data) {
        updateDataContext(this, data, scope, index);
    }
}
