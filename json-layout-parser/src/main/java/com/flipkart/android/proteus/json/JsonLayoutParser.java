package com.flipkart.android.proteus.json;

import android.support.annotation.Nullable;

import com.flipkart.android.proteus.LayoutParser;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * JsonLayoutParser
 *
 * @author aditya.sharat
 */

public class JsonLayoutParser implements LayoutParser {

    private JsonElement element;
    private JsonElement current;
    private Iterator<JsonElement> arrayIterator;
    private Iterator<Map.Entry<String, JsonElement>> entriesIterator;
    private String name;

    public JsonLayoutParser(JsonElement element) {
        this.element = element;
        this.current = element;
        if (element.isJsonArray()) {
            arrayIterator = element.getAsJsonArray().iterator();
            entriesIterator = null;
        } else if (element.isJsonObject()) {
            entriesIterator = element.getAsJsonObject().entrySet().iterator();
            arrayIterator = null;
        }
    }

    @Override
    public boolean hasNext() {
        if (element.isJsonObject()) {
            return entriesIterator.hasNext();
        } else if (element.isJsonArray()) {
            return arrayIterator.hasNext();
        }
        return false;
    }

    @Override
    public void next() {
        if (element.isJsonObject()) {
            Map.Entry<String, JsonElement> entry = entriesIterator.next();
            name = entry.getKey();
            current = entry.getValue();
        } else if (element.isJsonArray()) {
            current = arrayIterator.next();
        }
    }

    @Override
    public LayoutParser peek() {
        return new JsonLayoutParser(current);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        element.getAsJsonObject().add(name, current);
        element.getAsJsonObject().remove(this.name);
        this.name = name;
    }

    @Override
    public int size() {
        return ((JsonArray) current).size();
    }

    @Override
    public boolean isBoolean() {
        return current.isJsonPrimitive() && current.getAsJsonPrimitive().isBoolean();
    }

    @Override
    public boolean isNumber() {
        return current.isJsonPrimitive() && current.getAsJsonPrimitive().isNumber();
    }

    @Override
    public boolean isString() {
        return current.isJsonPrimitive();
    }

    @Override
    public boolean isArray() {
        return current.isJsonArray();
    }

    @Override
    public boolean isObject() {
        return current.isJsonObject();
    }

    @Override
    public boolean isLayout() {
        return current != null && current != JsonNull.INSTANCE && current.isJsonObject() && current.getAsJsonObject().has(ProteusConstants.TYPE);
    }

    @Override
    public boolean isNull() {
        return current == null || current == JsonNull.INSTANCE;
    }

    @Override
    public boolean getBoolean() {
        return current.getAsJsonPrimitive().getAsBoolean();
    }

    @Override
    public int getInt() {
        return current.getAsJsonPrimitive().getAsInt();
    }

    @Override
    public float getFloat() {
        return current.getAsJsonPrimitive().getAsFloat();
    }

    @Override
    public double getDouble() {
        return current.getAsJsonPrimitive().getAsDouble();
    }

    @Override
    public long getLong() {
        return current.getAsJsonPrimitive().getAsLong();
    }

    @Override
    public String getString() {
        return current.getAsString();
    }

    @Override
    public String getType() {
        return current.getAsJsonObject().getAsJsonPrimitive(ProteusConstants.TYPE).getAsString();
    }

    @Override
    public Map<String, String> getScope() {
        JsonObject jScope = current.getAsJsonObject().getAsJsonObject(ProteusConstants.DATA_CONTEXT);
        if (null == jScope) {
            return null;
        }
        Map<String, String> scope = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : jScope.entrySet()) {
            scope.put(entry.getKey(), entry.getValue().getAsString());
        }
        return scope;
    }

    @Override
    public boolean hasProperty(String property) {
        return ((JsonObject) current).has(property);
    }

    @Override
    public boolean isBoolean(String property) {
        JsonElement value = ((JsonObject) current).get(property);
        return value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean();
    }

    @Override
    public boolean isNumber(String property) {
        JsonElement value = ((JsonObject) current).get(property);
        return value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber();
    }

    @Override
    public boolean isString(String property) {
        JsonElement value = ((JsonObject) current).get(property);
        return value != null && value.isJsonPrimitive();
    }

    @Override
    public boolean isArray(String property) {
        JsonElement value = ((JsonObject) current).get(property);
        return value != null && value.isJsonArray();
    }

    @Override
    public boolean isObject(String property) {
        JsonElement value = ((JsonObject) current).get(property);
        return value != null && value.isJsonObject();
    }

    @Override
    public boolean isNull(String property) {
        JsonElement value = ((JsonObject) current).get(property);
        return value == null || value == JsonNull.INSTANCE;
    }

    @Override
    public boolean isLayout(String property) {
        JsonElement value = ((JsonObject) current).get(property);
        return value != null && value != JsonNull.INSTANCE && value.isJsonObject() && value.getAsJsonObject().has(ProteusConstants.TYPE);
    }

    @Override
    public boolean getBoolean(String property) {
        return ((JsonObject) current).has(property) && ((JsonObject) current).get(property).getAsBoolean();
    }

    @Override
    public int getInt(String property) {
        return ((JsonObject) current).has(property) ? ((JsonObject) current).get(property).getAsInt() : 0;
    }

    @Override
    public float getFloat(String property) {
        return ((JsonObject) current).has(property) ? ((JsonObject) current).get(property).getAsFloat() : 0;
    }

    @Override
    public double getDouble(String property) {
        return ((JsonObject) current).has(property) ? ((JsonObject) current).get(property).getAsDouble() : 0;
    }

    @Override
    public long getLong(String property) {
        return ((JsonObject) current).has(property) ? ((JsonObject) current).get(property).getAsLong() : 0;
    }

    @Override
    public String getString(String property) {
        return ((JsonObject) current).has(property) ? ((JsonObject) current).get(property).getAsString() : null;
    }

    @Override
    @Nullable
    public LayoutParser peek(String property) {
        if (((JsonObject) current).has(property)) {
            return new JsonLayoutParser(((JsonObject) current).get(property));
        }
        return null;
    }

    @Override
    public void addAttribute(String name, Object value) {
        ((JsonObject) element).add(name, (JsonElement) value);
    }

    @Override
    public LayoutParser merge(@Nullable Object layout) {
        return new JsonLayoutParser(Utils.mergeLayouts((JsonObject) layout, (JsonObject) current));
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public LayoutParser clone() {
        return new JsonLayoutParser(Utils.addElements(new JsonObject(), (JsonObject) current, true));
    }

    @Override
    public Value getValueParser(Object value) {
        return new JsonValue((JsonElement) value);
    }

    public class JsonValue extends JsonLayoutParser implements LayoutParser.Value {
        public JsonValue(JsonElement element) {
            super(element);
        }
    }
}
