package com.flipkart.layoutengine.toolbox;

import android.annotation.TargetApi;
import android.os.Build;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aditya Sharat on 18-05-2015.
 */
public class Formatters {

    private static Formatter NumberFormatter = new Formatter() {
        @Override
        public String format(String value) {
            double valueAsNumber = Double.parseDouble(value);
            NumberFormat numberFormat = new DecimalFormat("#,###");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
                numberFormat.setRoundingMode(RoundingMode.FLOOR);
            }
            numberFormat.setMinimumFractionDigits(0);
            numberFormat.setMaximumFractionDigits(2);
            return numberFormat.format(valueAsNumber);
        }

        public String format(int value) {
            NumberFormat numberFormat = new DecimalFormat("#,###");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
                numberFormat.setRoundingMode(RoundingMode.FLOOR);
            }
            numberFormat.setMinimumFractionDigits(0);
            numberFormat.setMaximumFractionDigits(2);
            return numberFormat.format(value);
        }

        @Override
        public String getName() {
            return "number";
        }
    };

    private static Formatter NOOP = new Formatter() {
        @Override
        public String format(String value) {
            return value;
        }

        @Override
        public String getName() {
            return "noop";
        }
    };

    private static Map<String, Formatter> formatters = new HashMap<>();

    static {
        formatters.put(NumberFormatter.getName(), NumberFormatter);
    }

    public static Formatter get(String formatterName) {
        Formatter formatter = formatters.get(formatterName);
        if (formatter == null) {
            return NOOP;
        }
        return formatter;
    }

    public static void add(Formatter formatter) {
        formatters.put(formatter.getName(), formatter);
    }

    public static void remove(String formatterName) {
        formatters.remove(formatterName);
    }

    public interface Formatter {
        String format(String value);

        String getName();
    }
}
