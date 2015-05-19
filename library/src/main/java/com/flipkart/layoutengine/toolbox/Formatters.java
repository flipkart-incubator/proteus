package com.flipkart.layoutengine.toolbox;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aditya Sharat on 18-05-2015.
 */
public class Formatters {

    public static Formatter NumberFormatter = new Formatter() {
        @Override
        public String format(String value) {
            int valueAsNumber = Integer.parseInt(value);
            return new DecimalFormat("#,###.00").format(valueAsNumber);
        }

        public String format(int value) {
            return new DecimalFormat("#,###.00").format(value);
        }

        @Override
        public String getName() {
            return "number";
        }
    };

    public static Formatter NOOP = new Formatter() {
        @Override
        public String format(String value) {
            return value;
        }

        @Override
        public String getName() {
            return "noop";
        }
    };

    public static Formatter get(String formatterName) {
        switch (formatterName) {
            case "number":
                return NumberFormatter;
            default:
                return NOOP;
        }
    }

    private static Map<String, Formatter> formatters = new HashMap<>();

    public interface Formatter {
        String format(String value);

        String getName();
    }
}
