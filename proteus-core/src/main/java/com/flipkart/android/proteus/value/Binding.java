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

package com.flipkart.android.proteus.value;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LruCache;
import android.util.Pair;

import com.flipkart.android.proteus.Function;
import com.flipkart.android.proteus.FunctionManager;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.toolbox.Result;
import com.flipkart.android.proteus.toolbox.SimpleArrayIterator;
import com.flipkart.android.proteus.toolbox.Utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Binding
 *
 * @author adityasharat
 */
public class Binding extends Value {

    public static final char BINDING_PREFIX = '~';
    public static final String EMPTY_STRING = "";
    public static final String EMPTY_TEMPLATE = EMPTY_STRING;
    public static final String TEMPLATE = "%s";
    public static final String DATA_PATH_DELIMITERS = ".[]";
    public static final String INDEX = "$index";
    public static final String ARRAY_DATA_LENGTH_REFERENCE = "$length";
    public static final String ARRAY_DATA_LAST_INDEX_REFERENCE = "$last";
    public static final Pattern BINDING_PATTERN = Pattern.compile("@\\{(\\S+)\\}\\$\\{((?:\\S|(?<!\\\\)'.*?(?<!\\\\)')+)\\}|@\\{(\\S+)\\}");
    public static final Pattern FORMATTER_PATTERN = Pattern.compile(",(?=(?:[^']*'[^']*')*[^']*$)");
    public static final char FORMATTER_ARG_PREFIX = '(';

    public final String template;
    private final Expression[] expressions;

    /**
     * @param template
     * @param expressions
     */
    Binding(String template, Expression[] expressions) {
        this.template = template;
        this.expressions = expressions;
    }

    /**
     * @param value
     * @return
     */
    public static boolean isBindingValue(@NonNull final String value) {
        return !value.isEmpty() && value.charAt(0) == BINDING_PREFIX;
    }

    /**
     * @param value
     * @param manager
     * @return
     */
    public static Binding valueOf(@NonNull final String value, FunctionManager manager) {
        Matcher matcher = BINDING_PATTERN.matcher(value);
        StringBuffer sb = new StringBuffer();
        Expression expression, expressions[] = new Expression[0];

        while (matcher.find()) {
            if (matcher.group(3) != null) {
                expression = Expression.valueOf(matcher.group(3), null, manager);
            } else {
                expression = Expression.valueOf(matcher.group(1), matcher.group(2), manager);
            }
            matcher.appendReplacement(sb, TEMPLATE);
            expressions = Arrays.copyOf(expressions, expressions.length + 1);
            expressions[expressions.length - 1] = expression;
        }
        matcher.appendTail(sb);
        String template = sb.toString();
        if (TEMPLATE.equals(template)) {
            template = EMPTY_TEMPLATE;
        }
        return new Binding(template, expressions);
    }

    /**
     * @return
     * @throws IndexOutOfBoundsException
     */
    public Iterator<Expression> getExpressions() {
        return new SimpleArrayIterator<>(expressions);
    }

    /**
     * @param data
     * @param index @return
     */
    public Value evaluate(Value data, int index) {
        Value empty = StringAttributeProcessor.EMPTY;
        Result result;
        //noinspection StringEquality the string object compare can be safely used here, do not convert it to .equals()
        if (expressions.length == 1 && template == EMPTY_TEMPLATE) {
            result = expressions[0].evaluate(data, index);
            return result.isSuccess() ? result.value : empty;
        } else {
            String[] variables = new String[expressions.length];
            String variable;
            for (int i = 0; i < expressions.length; i++) {
                result = expressions[i].evaluate(data, index);
                if (result.isSuccess()) {
                    //noinspection ConstantConditions
                    if (result.value.isPrimitive()) {
                        variable = result.value.getAsString();
                    } else {
                        variable = result.value.toString();
                    }
                } else {
                    variable = EMPTY_STRING;
                }
                variables[i] = variable;
            }
            return new Primitive(String.format(template, (Object[]) variables));
        }
    }

    @Override
    public Value copy() {
        return this;
    }

    @Override
    public String toString() {
        String[] strings = new String[expressions.length];
        for (int i = 0; i < expressions.length; i++) {
            strings[i] = expressions[i].toString();
        }
        if (EMPTY_TEMPLATE.equals(template)) {
            return strings[0];
        } else {
            return String.format(template, (Object[]) strings);
        }
    }

    /**
     *
     */
    public static class Expression {

        @Nullable
        public final Function function;

        @Nullable
        public final Value[] arguments;

        @NonNull
        private final String[] tokens;

        private Expression(@NonNull String[] tokens, @Nullable Function function, @Nullable Value[] arguments) {
            this.tokens = tokens;
            this.function = function;
            this.arguments = arguments;
        }

        /**
         * @param path
         * @param formatter
         * @param manager
         * @return
         */
        public static Expression valueOf(String path, @Nullable String formatter, FunctionManager manager) {
            String key = path + (null == formatter ? "" : '$' + formatter);
            Expression expression = ExpressionCache.cache.get(key);
            if (null == expression) {
                StringTokenizer tokenizer = new StringTokenizer(path, DATA_PATH_DELIMITERS);
                String[] tokens = new String[0];
                while (tokenizer.hasMoreTokens()) {
                    tokens = Arrays.copyOf(tokens, tokens.length + 1);
                    tokens[tokens.length - 1] = tokenizer.nextToken();
                }
                if (null != formatter) {
                    Pair<Function, Value[]> value = valueOf(formatter, manager);
                    expression = new Expression(tokens, value.first, value.second);
                } else {
                    expression = new Expression(tokens, null, null);
                }
                ExpressionCache.cache.put(key, expression);
            }
            return expression;
        }

        private static Pair<Function, Value[]> valueOf(String value, FunctionManager manager) {
            int index = value.indexOf(FORMATTER_ARG_PREFIX);
            String name = value.substring(0, index);
            String section = value.substring(index + 1, value.length() - 1);

            if (section.isEmpty()) {
                return new Pair<>(manager.get(name), new Value[0]);
            } else {
                String[] tokens = FORMATTER_PATTERN.split(section);
                Value[] arguments = new Value[tokens.length];
                String token;
                Value resolved;
                for (int i = 0; i < tokens.length; i++) {
                    token = tokens[i].trim();
                    if (isBindingValue(tokens[i])) {
                        resolved = Binding.valueOf(token, manager);
                    } else {
                        if (!token.isEmpty() && token.charAt(0) == '\'') {
                            token = token.substring(1, token.length() - 1);
                        }
                        resolved = new Primitive(token);
                    }
                    arguments[i] = resolved;
                }

                return new Pair<>(manager.get(name), arguments);
            }
        }

        private static Result resolveData(String[] tokens, Value data, int index) {
            // replace INDEX with index value
            if (tokens.length == 1 && INDEX.equals(tokens[0])) {
                return Result.success(new Primitive(String.valueOf(index)));
            } else {
                Value elementToReturn = data;
                Value tempElement;
                Array tempArray;

                for (int i = 0; i < tokens.length; i++) {
                    String segment = tokens[i];
                    if (elementToReturn == null) {
                        return Result.NO_SUCH_DATA_PATH_EXCEPTION;
                    }
                    if (elementToReturn.isNull()) {
                        return Result.NULL_EXCEPTION;
                    }
                    if ("".equals(segment)) {
                        continue;
                    }
                    if (elementToReturn.isArray()) {
                        tempArray = elementToReturn.getAsArray();

                        if (INDEX.equals(segment)) {
                            if (index < tempArray.size()) {
                                elementToReturn = tempArray.get(index);
                            } else {
                                return Result.NO_SUCH_DATA_PATH_EXCEPTION;
                            }
                        } else if (ARRAY_DATA_LENGTH_REFERENCE.equals(segment)) {
                            elementToReturn = new Primitive(tempArray.size());
                        } else if (ARRAY_DATA_LAST_INDEX_REFERENCE.equals(segment)) {
                            if (tempArray.size() == 0) {
                                return Result.NO_SUCH_DATA_PATH_EXCEPTION;
                            }
                            elementToReturn = tempArray.get(tempArray.size() - 1);
                        } else {
                            try {
                                index = Integer.parseInt(segment);
                            } catch (NumberFormatException e) {
                                return Result.INVALID_DATA_PATH_EXCEPTION;
                            }
                            if (index < tempArray.size()) {
                                elementToReturn = tempArray.get(index);
                            } else {
                                return Result.NO_SUCH_DATA_PATH_EXCEPTION;
                            }
                        }
                    } else if (elementToReturn.isObject()) {
                        tempElement = elementToReturn.getAsObject().get(segment);
                        if (tempElement != null) {
                            elementToReturn = tempElement;
                        } else {
                            return Result.NO_SUCH_DATA_PATH_EXCEPTION;
                        }
                    } else if (elementToReturn.isPrimitive()) {
                        return Result.INVALID_DATA_PATH_EXCEPTION;
                    } else {
                        return Result.NO_SUCH_DATA_PATH_EXCEPTION;
                    }
                }
                if (elementToReturn.isNull()) {
                    return Result.NULL_EXCEPTION;
                }
                return Result.success(elementToReturn);
            }
        }

        private static Value[] resolveArguments(Value[] in, Value data, int index) {
            //noinspection ConstantConditions because we want it to crash, it is an illegal state anyway
            Value[] out = new Value[in.length];
            Value argument, resolved;
            for (int i = 0; i < in.length; i++) {
                argument = in[i];
                if (argument.isBinding()) {
                    resolved = argument.getAsBinding().evaluate(data, index);
                } else {
                    resolved = argument;
                }
                out[i] = resolved;
            }

            return out;
        }

        /**
         * @return
         */
        public Iterator getTokens() {
            return new SimpleArrayIterator<>(tokens);
        }

        /**
         * @param data
         * @param index @return
         */
        public Result evaluate(Value data, int index) {
            Result result = resolveData(tokens, data, index);
            if (null == this.function) {
                return result;
            } else {
                Value resolved = this.function.format(result.value, index, resolveArguments(arguments, data, index));
                return Result.success(resolved);
            }
        }

        @Override
        public String toString() {
            String context = "@{" + Utils.getStringFromArray(tokens, ".") + "}";
            String functions = "";
            if (null != function) {
                functions = "${" + function.getName() + "(" + Utils.getStringFromArray(arguments, ",", Utils.STYLE_SINGLE) + ")}";
            }
            return context + functions;
        }

        /**
         *
         */
        private static class ExpressionCache {
            private static final LruCache<String, Expression> cache = new LruCache<>(64);
        }
    }
}
