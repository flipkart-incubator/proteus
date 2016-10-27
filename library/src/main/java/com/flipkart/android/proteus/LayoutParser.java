package com.flipkart.android.proteus;

import java.util.Map;

/**
 * LayoutParser
 *
 * @author aditya.sharat
 */

public interface LayoutParser {

    boolean hasNext();

    void next();

    LayoutParser peek();


    String getType();

    Map<String, String> getScope();

    String getName();

    int size();


    boolean isBoolean();

    boolean isNumber();

    boolean isString();

    boolean isArray();

    boolean isObject();

    boolean isNull();


    boolean getBoolean();

    int getInt();

    double getDouble();

    long getLong();

    String getString();


    boolean isBoolean(String property);

    boolean isNumber(String property);

    boolean isString(String property);

    boolean isArray(String property);

    boolean isObject(String property);

    boolean isNull(String property);


    int getInt(String property);

    double getDouble(String property);

    long getLong(String property);

    String getString(String property);


    LayoutParser peek(String property);

    void add(Object value);

    void remove(Object value);


    LayoutParser setInput(Object input);


    String toString();

}
