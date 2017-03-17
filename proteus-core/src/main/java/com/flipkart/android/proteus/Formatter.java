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
import android.support.annotation.NonNull;

import com.flipkart.android.proteus.parser.ParseHelper;
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
        @NonNull
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

        @NonNull
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

        @NonNull
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
        @NonNull
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
        @NonNull
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
        @NonNull
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            double sum = 0;

            for (Value argument : arguments) {
                sum = sum + argument.getAsDouble();
            }

            return new Primitive(sum);
        }

        @Override
        public String getName() {
            return "add";
        }
    };

    public static final Formatter SUBTRACT = new Formatter() {
        @NonNull
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            double sum = arguments[0].getAsDouble();

            for (int i = 1; i < arguments.length; i++) {
                sum = sum - arguments[i].getAsDouble();
            }

            return new Primitive(sum);
        }

        @Override
        public String getName() {
            return "sub";
        }
    };

    public static final Formatter MULTIPLY = new Formatter() {
        @NonNull
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            double product = 1;

            for (Value argument : arguments) {
                product = product * argument.getAsDouble();
            }

            return new Primitive(product);
        }

        @Override
        public String getName() {
            return "mul";
        }
    };

    public static final Formatter DIVIDE = new Formatter() {
        @NonNull
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            double quotient = arguments[0].getAsDouble();

            for (int i = 1; i < arguments.length; i++) {
                quotient = quotient / arguments[i].getAsDouble();
            }

            return new Primitive(quotient);
        }

        @Override
        public String getName() {
            return "div";
        }
    };

    public static final Formatter MODULO = new Formatter() {
        @NonNull
        public Value format(Value data, int dataIndex, Value... arguments) {
            double remainder = arguments[0].getAsDouble();

            for (int i = 1; i < arguments.length; i++) {
                remainder = remainder % arguments[i].getAsDouble();
            }

            return new Primitive(remainder);
        }

        @Override
        public String getName() {
            return "mod";
        }
    };

    // Logical

    public static final Formatter AND = new Formatter() {
        @NonNull
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            if (arguments.length < 1) {
                return ProteusConstants.FALSE;
            }
            boolean bool = true;
            for (Value argument : arguments) {
                bool = ParseHelper.parseBoolean(argument);
                if (!bool) {
                    break;
                }
            }

            return bool ? ProteusConstants.TRUE : ProteusConstants.FALSE;
        }

        @Override
        public String getName() {
            return "AND";
        }
    };

    public static final Formatter OR = new Formatter() {
        @NonNull
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            if (arguments.length < 1) {
                return ProteusConstants.FALSE;
            }
            boolean bool = false;
            for (Value argument : arguments) {
                bool = ParseHelper.parseBoolean(argument);
                if (bool) {
                    break;
                }
            }

            return bool ? ProteusConstants.TRUE : ProteusConstants.FALSE;
        }

        @Override
        public String getName() {
            return "OR";
        }
    };

    // Unary

    public static final Formatter NOT = new Formatter() {
        @NonNull
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            if (arguments.length < 1) {
                return ProteusConstants.TRUE;
            }
            return ParseHelper.parseBoolean(arguments[0]) ? ProteusConstants.FALSE : ProteusConstants.TRUE;
        }

        @Override
        public String getName() {
            return "NOT";
        }
    };

    // Comparison

    public static final Formatter EQUALS = new Formatter() {
        @NonNull
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            if (arguments.length < 2) {
                return ProteusConstants.FALSE;
            }

            Value x = arguments[0];
            Value y = arguments[1];
            boolean bool = false
            if (x.isPrimitive() && y.isPrimitive()) {
                bool = x.getAsPrimitive().equals(y.getAsPrimitive());
            }

            return bool ? ProteusConstants.TRUE : ProteusConstants.FALSE;
        }

        @Override
        public String getName() {
            return "EQUALS";
        }
    };

    public static final Formatter LESS_THAN = new Formatter() {
        @NonNull
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
        @NonNull
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
        @NonNull
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
        @NonNull
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
        @NonNull
        @Override
        public Value format(Value data, int dataIndex, Value... arguments) {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    };

    @NonNull
    public abstract Value format(Value data, int dataIndex, Value... arguments);

    public abstract String getName();
}
