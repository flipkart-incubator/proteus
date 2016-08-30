package com.flipkart.layoutengine;

import android.support.annotation.Nullable;

import com.flipkart.layoutengine.provider.JsonProvider;
import com.flipkart.layoutengine.provider.ProteusConstants;
import com.flipkart.layoutengine.toolbox.Result;
import com.flipkart.layoutengine.toolbox.Utils;
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

    private static final Logger logger = LoggerFactory.getLogger(DataContext.class);
    private JsonProvider dataProvider;
    private JsonObject reverseScope;
    private JsonObject scope;
    private int index;

    public DataContext() {
        this.scope = new JsonObject();
        this.reverseScope = new JsonObject();
    }

    public static DataContext updateDataContext(DataContext dataContext, JsonProvider dataProvider,
                                                JsonObject scope, int childIndex) {
        JsonObject reverseScope = new JsonObject();
        JsonObject newData = new JsonObject();
        JsonObject data = dataProvider.getData().getAsJsonObject();

        if (data == null) {
            data = new JsonObject();
        }

        for (Map.Entry<String, JsonElement> entry : scope.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getAsString();
            Result result = Utils.getElementFromData(value, dataProvider, childIndex);
            JsonElement element = result.isSuccess() ? result.element : new JsonObject();
            newData.add(key, element);
            String unAliasedValue = value.replace(ProteusConstants.CHILD_INDEX_REFERENCE, String.valueOf(childIndex));
            reverseScope.add(unAliasedValue, new JsonPrimitive(key));
        }

        Utils.addElements(newData, data, false);

        if (dataContext.getDataProvider() == null) {
            dataContext.setDataProvider(new JsonProvider(newData));
        } else {
            dataContext.getDataProvider().setData(newData);
        }
        dataContext.setScope(scope);
        dataContext.setReverseScope(reverseScope);
        dataContext.setIndex(childIndex);
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

    public JsonObject getScope() {
        return scope;
    }

    public void setScope(JsonObject scope) {
        this.scope = scope;
    }

    public JsonObject getReverseScopeMap() {
        return reverseScope;
    }

    public void setReverseScope(JsonObject reverseScope) {
        this.reverseScope = reverseScope;
    }

    public JsonProvider getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(JsonProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Nullable
    public JsonElement get(String dataPath, int childIndex) {
        String aliasedDataPath = getAliasedDataPath(dataPath, reverseScope, true);
        Result result = Utils.getElementFromData(aliasedDataPath, dataProvider, childIndex);
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

    public DataContext createChildDataContext(JsonObject scope, int childIndex) {
        return updateDataContext(new DataContext(), dataProvider, scope, childIndex);
    }

    public void updateDataContext(JsonObject data) {
        JsonProvider dataProvider = new JsonProvider(data);
        updateDataContext(this, dataProvider, scope, index);
    }
}
