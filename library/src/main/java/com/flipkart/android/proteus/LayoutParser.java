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

    LayoutParser getAttribute(String name);

    void addAttribute(String name, LayoutParser value);

    void add(LayoutParser parser);

    void set(int position, LayoutParser parser);

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

    float getFloat();

    double getDouble();

    long getLong();

    String getString();

    String getType();

    void setType(String type);

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


    String toString();

    LayoutParser create();

    LayoutParser clone();

    LayoutParser merge(@Nullable Object layout);

    void reset();

    Value getValueParser(Object value);

    interface Value extends LayoutParser {

    }

}
