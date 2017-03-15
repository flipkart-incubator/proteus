/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 *
 * Copyright (c) 2017 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.flipkart.android.proteus;

import android.annotation.SuppressLint;

import com.flipkart.android.proteus.toolbox.Utils;
import com.flipkart.android.proteus.value.Primitive;
import com.flipkart.android.proteus.value.Value;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Aditya Sharat on 18-05-2015.
 */
public abstract class Formatter {

    // SPECIAL

    public static final Formatter NOOP = new Formatter() {
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            return data;
        }

        @Override
        public String getName() {
            return "noop";
        }
    };

    public static final Formatter NUMBER = new Formatter() {

        private final DecimalFormat formatter = new DecimalFormat("#,###");

        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            double valueAsNumber;
            try {
                valueAsNumber = Double.parseDouble(data.getAsString());
            } catch (NumberFormatException e) {
                return data;
            }
            formatter.setRoundingMode(RoundingMode.FLOOR);
            formatter.setMinimumFractionDigits(0);
            formatter.setMaximumFractionDigits(2);
            return new Primitive(formatter.format(valueAsNumber));
        }

        @Override
        public String getName() {
            return "number";
        }
    };

    @SuppressLint("SimpleDateFormat")
    public static final Formatter DATE = new Formatter() {

        private SimpleDateFormat from = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        private SimpleDateFormat to = new SimpleDateFormat("E, d MMM");

        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            try {
                // data should for of the format : 2015-06-18 12:01:37
                Date in = getFromFormat(arguments).parse(data.getAsString());
                String out = getToFormat(arguments).format(in);
                return new Primitive(out);
            } catch (Exception e) {
                return data;
            }
        }

        private SimpleDateFormat getFromFormat(Value[] arguments) {
            if (arguments.length > 1) {
                return new SimpleDateFormat(arguments[1].getAsString());
            } else {
                return from;
            }
        }

        private SimpleDateFormat getToFormat(Value[] arguments) {
            if (arguments.length > 0) {
                return new SimpleDateFormat(arguments[0].getAsString());
            } else {
                return to;
            }
        }

        @Override
        public String getName() {
            return "date";
        }
    };

    public static final Formatter INDEX = new Formatter() {
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            int valueAsNumber;
            try {
                valueAsNumber = Integer.parseInt(data.getAsString());
            } catch (NumberFormatException e) {
                return data;
            }
            return new Primitive(valueAsNumber + 1);
        }

        @Override
        public String getName() {
            return "index";
        }
    };

    public static final Formatter JOIN = new Formatter() {
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            if (data.isArray()) {
                return new Primitive(Utils.getStringFromArray(data.getAsArray(), ","));
            } else {
                return data;
            }
        }

        @Override
        public String getName() {
            return "join";
        }
    };

    // Mathematical

    public static final Formatter ADD = new Formatter() {
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    };

    public static final Formatter SUBTRACT = new Formatter() {
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    };

    public static final Formatter MULTIPLY = new Formatter() {
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    };

    public static final Formatter DIVIDE = new Formatter() {
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    };

    public static final Formatter MODULO = new Formatter() {
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    };

    // Logical

    public static final Formatter AND = new Formatter() {
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    };

    public static final Formatter OR = new Formatter() {
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    };

    // Unary

    public static final Formatter NOT = new Formatter() {
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    };

    // Comparison == > < >= <=

    public static final Formatter EQUALS = new Formatter() {
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    };

    public static final Formatter LESS_THAN = new Formatter() {
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    };

    public static final Formatter GREATER_THAN = new Formatter() {
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    };

    public static final Formatter LESS_THAN_OR_EQUALS = new Formatter() {
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    };

    public static final Formatter GREATER_THAN_OR_EQUALS = new Formatter() {
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    };

    // Conditional

    public static final Formatter IF_THEN_ELSE = new Formatter() {
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    };

    public abstract Value format(Value data, int dataIndex, Value... arguments);

    public abstract String getName();
}
