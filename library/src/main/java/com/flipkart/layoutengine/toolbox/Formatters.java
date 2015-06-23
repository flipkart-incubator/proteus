package com.flipkart.layoutengine.toolbox;

import android.os.Build;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Aditya Sharat on 18-05-2015.
 */
public class Formatters {

    private static Formatter NumberFormatter = new Formatter() {
        @Override
        public String format(String value) {
            double valueAsNumber;
            try {
                valueAsNumber = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return value;
            }
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

    private static Formatter DateFormatter = new Formatter() {
        @Override
        public String format(String value) {
            try {
                // 2015-06-18 12:01:37
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value);
                return new SimpleDateFormat("d MMM, E").format(date);
            } catch (Exception e) {
                return  value;
            }
        }

        @Override
        public String getName() {
            return "date";
        }
    };

    private static Formatter IndexFormatter = new Formatter() {
        @Override
        public String format(String value) {
            int valueAsNumber;
            try {
                valueAsNumber = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return value;
            }
            return String.valueOf(valueAsNumber + 1);
        }

        @Override
        public String getName() {
            return "index";
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
        formatters.put(DateFormatter.getName(), DateFormatter);
        formatters.put(IndexFormatter.getName(), IndexFormatter);
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
