package com.flipkart.layoutengine;

import android.util.Log;

import com.flipkart.layoutengine.exceptions.InvalidDataPathException;
import com.flipkart.layoutengine.exceptions.JsonNullException;
import com.flipkart.layoutengine.exceptions.NoSuchDataPathException;
import com.flipkart.layoutengine.provider.JsonProvider;
import com.flipkart.layoutengine.provider.ProteusConstants;
import com.flipkart.layoutengine.toolbox.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Aditya Sharat
 */
public class DataContext {

    public static final String TAG = Utils.getTagPrefix() + DataContext.class.getSimpleName();

    private List<DataContext> children;
    private JsonProvider dataProvider;
    private JsonObject reverseScope;
    private JsonObject scope;
    private int index;

    public DataContext() {
        this.scope = new JsonObject();
        this.reverseScope = new JsonObject();
        this.children = new ArrayList<>();
    }

    public JsonObject getScope() {
        return scope;
    }

    public JsonObject getReverseScopeMap() {
        return reverseScope;
    }

    public void setScope(JsonObject scope) {
        this.scope = scope;
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

    public JsonElement get(String dataPath, int childIndex) {
        String aliasedDataPath = getAliasedDataPath(dataPath, reverseScope, true);
        try {
            return Utils.getElementFromData(aliasedDataPath, dataProvider, childIndex);
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

    public List<DataContext> getChildren() {
        return children;
    }

    public void addChild(DataContext dataContext) {
        this.children.add(dataContext);
    }

    public DataContext createChildDataContext(JsonObject scope, int childIndex) {
        DataContext dataContext = updateDataContext(new DataContext(), dataProvider, scope, childIndex);
        this.addChild(dataContext);
        return dataContext;
    }

    public void updateDataContext(JsonObject data) {
        JsonProvider dataProvider = new JsonProvider(data);
        updateDataContext(this, dataProvider, scope, index);

        for (DataContext child : children) {
            child.updateDataContext(this.dataProvider.getData().getAsJsonObject());
        }
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
            if (entry.getValue().isJsonPrimitive()) {
                JsonElement element;
                String value = entry.getValue().getAsString();
                try {
                    element = Utils.getElementFromData(value, dataProvider, childIndex);
                } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException e) {
                    Log.e(TAG + "#getNewDataContext()", "could not find '" + value +
                            "' for '" + key + "'. ERROR: " + e.getMessage());
                    element = new JsonObject();
                }
                newData.add(key, element);
                String unAliasedValue = value.replace(ProteusConstants.CHILD_INDEX_REFERENCE, String.valueOf(childIndex));
                reverseScope.add(unAliasedValue, new JsonPrimitive(key));
            } else {
                newData.add(key, entry.getValue());
            }
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
}
