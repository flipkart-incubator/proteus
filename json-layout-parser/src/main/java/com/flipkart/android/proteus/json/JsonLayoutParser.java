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
import java.util.Map;

/**
 * JsonLayoutParser
 *
 * @author aditya.sharat
 */

public class JsonLayoutParser implements LayoutParser {

    private JsonElement element;
    @Nullable
    private JsonElement previous;

    private String name;

    public JsonLayoutParser(JsonElement element) {
        this.element = element;
        this.previous = null;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public void next() {

    }

    @Override
    public LayoutParser peek() {
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int size() {
        return ((JsonArray) element).size();
    }

    @Override
    public boolean isBoolean() {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean();
    }

    @Override
    public boolean isNumber() {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
    }

    @Override
    public boolean isString() {
        return element.isJsonPrimitive();
    }

    @Override
    public boolean isArray() {
        return element.isJsonArray();
    }

    @Override
    public boolean isObject() {
        return element.isJsonObject();
    }

    @Override
    public boolean isLayout() {
        return element != null && element != JsonNull.INSTANCE && element.isJsonObject() && element.getAsJsonObject().has(ProteusConstants.TYPE);
    }

    @Override
    public boolean isNull() {
        return element == null || element == JsonNull.INSTANCE;
    }

    @Override
    public boolean getBoolean() {
        return element.getAsJsonPrimitive().getAsBoolean();
    }

    @Override
    public int getInt() {
        return element.getAsJsonPrimitive().getAsInt();
    }

    @Override
    public float getLFloat() {
        return element.getAsJsonPrimitive().getAsFloat();
    }

    @Override
    public double getDouble() {
        return element.getAsJsonPrimitive().getAsDouble();
    }

    @Override
    public long getLong() {
        return element.getAsJsonPrimitive().getAsLong();
    }

    @Override
    public String getString() {
        return element.getAsString();
    }

    @Override
    public String getType() {
        return element.getAsJsonObject().getAsJsonPrimitive(ProteusConstants.TYPE).getAsString();
    }

    @Override
    public Map<String, String> getScope() {
        JsonObject jScope = element.getAsJsonObject().getAsJsonObject(ProteusConstants.DATA_CONTEXT);
        Map<String, String> scope = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : jScope.entrySet()) {
            scope.put(entry.getKey(), entry.getValue().getAsString());
        }
        return scope;
    }

    @Override
    public boolean isBoolean(String property) {
        JsonElement value = ((JsonObject) element).get(property);
        return value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean();
    }

    @Override
    public boolean isNumber(String property) {
        JsonElement value = ((JsonObject) element).get(property);
        return value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber();
    }

    @Override
    public boolean isString(String property) {
        JsonElement value = ((JsonObject) element).get(property);
        return value != null && value.isJsonPrimitive();
    }

    @Override
    public boolean isArray(String property) {
        JsonElement value = ((JsonObject) element).get(property);
        return value != null && value.isJsonArray();
    }

    @Override
    public boolean isObject(String property) {
        JsonElement value = ((JsonObject) element).get(property);
        return value != null && value.isJsonObject();
    }

    @Override
    public boolean isNull(String property) {
        JsonElement value = ((JsonObject) element).get(property);
        return value == null || value == JsonNull.INSTANCE;
    }

    @Override
    public boolean isLayout(String property) {
        JsonElement value = ((JsonObject) element).get(property);
        return value != null && value != JsonNull.INSTANCE && value.isJsonObject() && value.getAsJsonObject().has(ProteusConstants.TYPE);
    }

    @Override
    public int getInt(String property) {
        return ((JsonObject) element).get(property).getAsInt();
    }

    @Override
    public float getFloat(String property) {
        return ((JsonObject) element).get(property).getAsFloat();
    }

    @Override
    public double getDouble(String property) {
        return ((JsonObject) element).get(property).getAsDouble();
    }

    @Override
    public long getLong(String property) {
        return ((JsonObject) element).get(property).getAsLong();
    }

    @Override
    public String getString(String property) {
        return ((JsonObject) element).get(property).getAsString();
    }

    @Override
    public LayoutParser peek(String property) {
        return new JsonLayoutParser(((JsonObject) element).get(property));
    }

    @Override
    public void addAttribute(String name, Object value) {
        ((JsonObject) element).add(name, (JsonElement) value);
    }

    @Override
    public LayoutParser merge(@Nullable Object layout) {
        return new JsonLayoutParser(Utils.mergeLayouts((JsonObject) element, (JsonObject) layout));
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public LayoutParser clone() {
        return new JsonLayoutParser(Utils.addElements(new JsonObject(), (JsonObject) element, true));
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
