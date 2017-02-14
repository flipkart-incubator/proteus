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

import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.toolbox.Result;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Arrays;
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
    public static final String TEMPLATE = "%s";
    public static final String DATA_PATH_DELIMITERS = ".[]";
    public static final String INDEX = "$index";
    public static final String ARRAY_DATA_LENGTH_REFERENCE = "$length";
    public static final String ARRAY_DATA_LAST_INDEX_REFERENCE = "$last";
    public static final Pattern BINDING_PATTERN = Pattern.compile("@\\{(\\S+?)\\}\\$\\{(\\S+?)\\}|@\\{(\\S+?)\\}");

    public final String template;
    private final Expression[] expressions;

    /**
     * @param template
     * @param expressions
     */
    private Binding(String template, Expression[] expressions) {
        this.template = template;
        this.expressions = expressions;
    }

    /**
     * @param value
     * @return
     */
    public static boolean isBindingValue(final String value) {
        return !value.isEmpty() && value.charAt(0) == BINDING_PREFIX;
    }

    /**
     * @param value
     * @return
     */
    public static Binding valueOf(final String value) {
        Binding binding = BindingCache.cache.get(value);
        if (null == binding) {
            Matcher matcher = BINDING_PATTERN.matcher(value);
            StringBuffer sb = new StringBuffer();
            Expression expression, expressions[] = new Expression[0];

            while (matcher.find()) {
                if (matcher.group(3) != null) {
                    expression = Expression.valueOf(matcher.group(3), null);
                } else {
                    expression = Expression.valueOf(matcher.group(1), matcher.group(2));
                }
                matcher.appendReplacement(sb, TEMPLATE);
                expressions = Arrays.copyOf(expressions, expressions.length + 1);
                expressions[expressions.length - 1] = expression;
            }
            matcher.appendTail(sb);

            binding = new Binding(sb.toString(), expressions);
            BindingCache.cache.put(value, binding);
        }
        return binding;
    }

    /**
     * @param index
     * @return
     * @throws IndexOutOfBoundsException
     */
    public Expression getExpression(int index) {
        return expressions[index];
    }

    /**
     * @param data
     * @param index
     * @return
     */
    public Value evaluate(JsonElement data, int index) {
        Value empty = StringAttributeProcessor.EMPTY;
        Result result;
        if (expressions.length == 1) {
            result = expressions[0].evaluate(data, index);
            return result.isSuccess() ? Value.fromJson(result.element) : empty;
        } else {
            String[] variables = new String[expressions.length];
            for (int i = 0; i < expressions.length; i++) {
                result = expressions[i].evaluate(data, index);
                variables[i] = result.isSuccess() ? result.element.toString() : "";
            }
            return new Primitive(String.format(template, (Object[]) variables));
        }
    }

    @Override
    Value copy() {
        return this;
    }

    /**
     *
     */
    private static class BindingCache {
        private static final LruCache<String, Binding> cache = new LruCache<>(64);
    }

    /**
     *
     */
    public static class Expression {

        @Nullable
        public final String formatter;
        private final String[] tokens;

        private Expression(String[] tokens, @Nullable String formatter) {
            this.tokens = tokens;
            this.formatter = formatter;
        }

        /**
         * @param path
         * @param formatter
         * @return
         */
        public static Expression valueOf(String path, @Nullable String formatter) {
            String key = path + (null == formatter ? "" : '$' + formatter);
            Expression expression = ExpressionCache.cache.get(key);
            if (null == expression) {
                StringTokenizer tokenizer = new StringTokenizer(path, DATA_PATH_DELIMITERS);
                String[] tokens = new String[0];
                while (tokenizer.hasMoreTokens()) {
                    tokens = Arrays.copyOf(tokens, tokens.length + 1);
                    tokens[tokens.length - 1] = tokenizer.nextToken();
                }
                expression = new Expression(tokens, formatter);
                ExpressionCache.cache.put(key, expression);
            }
            return expression;
        }

        /**
         * @param index
         * @return
         */
        public String getToken(int index) {
            return tokens[index];
        }

        /**
         * @param data
         * @param index
         * @return
         */
        public Result evaluate(JsonElement data, int index) {
            // replace INDEX with index value
            if (tokens.length == 1 && INDEX.equals(tokens[0])) {
                return Result.success(new JsonPrimitive(String.valueOf(index)));
            } else {
                JsonElement elementToReturn = data;
                JsonElement tempElement;
                JsonArray tempArray;

                for (int i = 0; i < tokens.length; i++) {
                    String segment = tokens[i];
                    if (elementToReturn == null) {
                        return Result.NO_SUCH_DATA_PATH_EXCEPTION;
                    }
                    if (elementToReturn.isJsonNull()) {
                        return Result.JSON_NULL_EXCEPTION;
                    }
                    if ("".equals(segment)) {
                        continue;
                    }
                    if (elementToReturn.isJsonArray()) {
                        tempArray = elementToReturn.getAsJsonArray();

                        if (INDEX.equals(segment)) {
                            if (index < tempArray.size()) {
                                elementToReturn = tempArray.get(index);
                            } else {
                                return Result.NO_SUCH_DATA_PATH_EXCEPTION;
                            }
                        } else if (ARRAY_DATA_LENGTH_REFERENCE.equals(segment)) {
                            elementToReturn = new JsonPrimitive(tempArray.size());
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
                    } else if (elementToReturn.isJsonObject()) {
                        tempElement = elementToReturn.getAsJsonObject().get(segment);
                        if (tempElement != null) {
                            elementToReturn = tempElement;
                        } else {
                            return Result.NO_SUCH_DATA_PATH_EXCEPTION;
                        }
                    } else if (elementToReturn.isJsonPrimitive()) {
                        return Result.INVALID_DATA_PATH_EXCEPTION;
                    } else {
                        return Result.NO_SUCH_DATA_PATH_EXCEPTION;
                    }
                }
                if (elementToReturn.isJsonNull()) {
                    return Result.JSON_NULL_EXCEPTION;
                }
                return Result.success(elementToReturn);
            }
        }

        /**
         *
         */
        private static class ExpressionCache {
            private static final LruCache<String, Expression> cache = new LruCache<>(64);
        }
    }
}
