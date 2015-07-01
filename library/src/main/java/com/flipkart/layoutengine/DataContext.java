package com.flipkart.layoutengine;

import com.flipkart.layoutengine.exceptions.InvalidDataPathException;
import com.flipkart.layoutengine.exceptions.JsonNullException;
import com.flipkart.layoutengine.exceptions.NoSuchDataPathException;
import com.flipkart.layoutengine.provider.ProteusConstants;
import com.flipkart.layoutengine.provider.JsonProvider;
import com.flipkart.layoutengine.toolbox.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Aditya Sharat
 */
public class DataContext {

    private JsonProvider dataProvider;
    private Map<String, String> reverseScope;
    private Map<String, String> scope;
    private DataContext parent;
    private int index;

    public DataContext(JsonProvider dataProvider, Map<String, String> scope, Map<String, String> reverseScope, DataContext parent, int index) {
        this.dataProvider = dataProvider;
        this.reverseScope = reverseScope;
        this.scope = scope;
        this.parent = parent;
        this.index = index;
    }

    public Map<String, String> getReverseScopeMap() {
        return reverseScope;
    }

    public void setReverseScopeMap(Map<String, String> reverseScopeMap) {
        this.reverseScope = reverseScopeMap;
    }

    public JsonProvider getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(JsonProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public DataContext getParent() {
        return parent;
    }

    public void setParent(DataContext parent) {
        this.parent = parent;
    }

    public Map<String, String> getScope() {
        return scope;
    }

    public void setScope(Map<String, String> scope) {
        this.scope = scope;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
