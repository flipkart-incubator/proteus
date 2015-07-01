package com.flipkart.layoutengine.toolbox;

/**
 * @author Aditya Sharat on 18-05-2015.
 */
public abstract class Formatter {

    public static final Formatter NOOP = new Formatter() {
        @Override
        public String format(String value) {
            return value;
        }

        @Override
        public String getName() {
            return "noop";
        }
    };

    public abstract String format(String value);

    public abstract String getName();
}
