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

package com.flipkart.android.proteus;

import android.support.annotation.Nullable;

import com.flipkart.android.proteus.exceptions.InvalidDataPathException;
import com.flipkart.android.proteus.exceptions.JsonNullException;
import com.flipkart.android.proteus.exceptions.NoSuchDataPathException;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Aditya Sharat
 */
public class DataContext {

    private static Logger logger = LoggerFactory.getLogger(DataContext.class);
    private final boolean isClone;
    private JsonObject data;
    @Nullable
    private JsonObject reverseScope;
    @Nullable
    private JsonObject scope;
    private int index;

    public DataContext() {
        this.data = new JsonObject();
        this.scope = new JsonObject();
        this.reverseScope = new JsonObject();
        this.index = -1;
        this.isClone = false;
    }

    public DataContext(DataContext dataContext) {
        this.data = dataContext.getData();
        this.scope = dataContext.getScope();
        this.reverseScope = dataContext.getReverseScope();
        this.index = dataContext.getIndex();
        this.isClone = true;
    }

    public static DataContext updateDataContext(DataContext dataContext, JsonObject data, JsonObject scope) {
        JsonObject reverseScope = new JsonObject();
        JsonObject newData = new JsonObject();

        if (data == null) {
            data = new JsonObject();
        }

        for (Map.Entry<String, JsonElement> entry : scope.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getAsString();
            JsonElement element;
            try {
                element = Utils.readJson(value, data, dataContext.getIndex());
            } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException e) {
                if (ProteusConstants.isLoggingEnabled()) {
                    logger.error("#getNewDataContext could not find: '" + value + "' for '" + key + "'. ERROR: " + e.getMessage());
                }
                element = new JsonObject();
            }

            newData.add(key, element);
            String unAliasedValue = value.replace(ProteusConstants.INDEX, String.valueOf(dataContext.getIndex()));
            reverseScope.add(unAliasedValue, new JsonPrimitive(key));
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

    public static String getAliasedDataPath(String dataPath, JsonObject reverseScope, boolean isBindingPath) {
        String[] segments;
        if (isBindingPath) {
            segments = dataPath.split(ProteusConstants.DATA_PATH_DELIMITER);
        } else {
            segments = dataPath.split(ProteusConstants.DATA_PATH_SIMPLE_DELIMITER);
        }

        if (reverseScope == null) {
            return dataPath;
        }
        String alias = Utils.getPropertyAsString(reverseScope, segments[0]);
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
    public JsonObject getScope() {
        return scope;
    }

    public void setScope(@Nullable JsonObject scope) {
        this.scope = scope;
    }

    @Nullable
    public JsonObject getReverseScope() {
        return reverseScope;
    }

    public void setReverseScope(@Nullable JsonObject reverseScope) {
        this.reverseScope = reverseScope;
    }

    public boolean isClone() {
        return isClone;
    }

    @Nullable
    public JsonElement get(String dataPath) {
        String aliasedDataPath = getAliasedDataPath(dataPath, reverseScope, true);
        try {
            return Utils.readJson(aliasedDataPath, data, index);
        } catch (JsonNullException e) {
            return JsonNull.INSTANCE;
        } catch (NoSuchDataPathException | InvalidDataPathException e) {
            return null;
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public DataContext createChildDataContext(JsonObject scope, int childIndex) {
        return updateDataContext(new DataContext(), data, scope);
    }

    public void updateDataContext(JsonObject data) {
        updateDataContext(this, data, scope);
    }
}
