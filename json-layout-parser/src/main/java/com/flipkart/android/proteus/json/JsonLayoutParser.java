package com.flipkart.android.proteus.json;

import android.support.annotation.Nullable;

import com.flipkart.android.proteus.LayoutParser;

import java.util.Map;

/**
 * JsonLayoutParser
 *
 * @author aditya.sharat
 */

public class JsonLayoutParser implements LayoutParser {

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public void next() {

    }

    @Override
    public LayoutParser peek() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isBoolean() {
        return false;
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public boolean isLayout() {
        return false;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean getBoolean() {
        return false;
    }

    @Override
    public int getInt() {
        return 0;
    }

    @Override
    public float getLFloat() {
        return 0;
    }

    @Override
    public double getDouble() {
        return 0;
    }

    @Override
    public long getLong() {
        return 0;
    }

    @Override
    public String getString() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public Map<String, String> getScope() {
        return null;
    }

    @Override
    public boolean isBoolean(String property) {
        return false;
    }

    @Override
    public boolean isNumber(String property) {
        return false;
    }

    @Override
    public boolean isString(String property) {
        return false;
    }

    @Override
    public boolean isArray(String property) {
        return false;
    }

    @Override
    public boolean isObject(String property) {
        return false;
    }

    @Override
    public boolean isNull(String property) {
        return false;
    }

    @Override
    public boolean isLayout(String property) {
        return false;
    }

    @Override
    public int getInt(String property) {
        return 0;
    }

    @Override
    public float getFloat(String property) {
        return 0;
    }

    @Override
    public double getDouble(String property) {
        return 0;
    }

    @Override
    public long getLong(String property) {
        return 0;
    }

    @Override
    public String getString(String property) {
        return null;
    }

    @Override
    public LayoutParser peek(String property) {
        return null;
    }

    @Override
    public void addAttribute(String name, Object value) {

    }

    @Override
    public LayoutParser merge(@Nullable Object layout) {
        return null;
    }

    @Override
    public LayoutParser clone() {
        return null;
    }

    @Override
    public Object getLayout() {
        return null;
    }

    @Override
    public Value getValueParser(Object value) {
        return null;
    }
}
