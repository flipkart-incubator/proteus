package com.flipkart.proteus.toolbox;

import com.google.gson.JsonElement;

/**
 * @author Aditya Sharat on 18-05-2015.
 */
public abstract class Formatter {

    public static final Formatter NOOP = new Formatter() {
        @Override
        public String format(JsonElement elementValue) {
            if (elementValue.isJsonPrimitive()) {
                return elementValue.getAsString();
            }
            return elementValue.toString();
        }

        @Override
        public String getName() {
            return "noop";
        }
    };

    public abstract String format(JsonElement elementValue);

    public abstract String getName();
}
