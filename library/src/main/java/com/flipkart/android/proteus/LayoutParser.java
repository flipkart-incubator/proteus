package com.flipkart.android.proteus;

import android.support.annotation.Nullable;

import java.util.Map;

/**
 * LayoutParser
 *
 * @author aditya.sharat
 */

public interface LayoutParser extends Cloneable {

    boolean hasNext();

    void next();

    LayoutParser peek();

    String getName();

    int size();

    boolean isBoolean();

    boolean isNumber();

    boolean isString();

    boolean isArray();

    boolean isObject();

    boolean isLayout();

    boolean isNull();


    boolean getBoolean();

    int getInt();

    float getLFloat();

    double getDouble();

    long getLong();

    String getString();

    String getType();

    Map<String, String> getScope();

    boolean hasProperty(String property);

    boolean isBoolean(String property);

    boolean isNumber(String property);

    boolean isString(String property);

    boolean isArray(String property);

    boolean isObject(String property);

    boolean isNull(String property);

    boolean isLayout(String property);


    boolean getBoolean(String property);

    int getInt(String property);

    float getFloat(String property);

    double getDouble(String property);

    long getLong(String property);

    String getString(String property);

    @Nullable
    LayoutParser peek(String property);

    void addAttribute(String name, Object value);

    String toString();

    LayoutParser merge(@Nullable Object layout);

    LayoutParser clone();

    Value getValueParser(Object value);

    interface Value extends LayoutParser {

    }

}
