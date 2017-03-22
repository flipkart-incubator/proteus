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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LruCache;

import com.flipkart.android.proteus.Function;
import com.flipkart.android.proteus.FunctionManager;
import com.flipkart.android.proteus.processor.AttributeProcessor;
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
public abstract class Binding extends Value {

    public static final char BINDING_PREFIX_0 = '@';
    public static final char BINDING_PREFIX_1 = '{';
    public static final char BINDING_SUFFIX = '}';

    public static final String INDEX = "$index";

    public static final String ARRAY_DATA_LENGTH_REFERENCE = "$length";
    public static final String ARRAY_DATA_LAST_INDEX_REFERENCE = "$last";

    public static final Pattern BINDING_PATTERN = Pattern.compile("@\\{fn:(\\S+?)\\(((?:(?<!\\\\)'.*?(?<!\\\\)'|.?)+)\\)\\}|@\\{(.+)\\}");
    public static final Pattern FUNCTION_ARGS_DELIMITER = Pattern.compile(",(?=(?:[^']*'[^']*')*[^']*$)");
    public static final String DATA_PATH_DELIMITERS = ".[]";
    public static final String SIMPLE_DATA_PATH_DELIMITER = ".";

    /**
     * @param value
     * @return
     */
    public static boolean isBindingValue(@NonNull final String value) {
        return value.length() > 3
                && value.charAt(0) == BINDING_PREFIX_0
                && value.charAt(1) == BINDING_PREFIX_1
                && value.charAt(value.length() - 1) == BINDING_SUFFIX;
    }

    /**
     * @param value
     * @param context
     * @param manager @return
     */
    public static Binding valueOf(@NonNull final String value, Context context, FunctionManager manager) {
        Matcher matcher = BINDING_PATTERN.matcher(value);
        if (matcher.find()) {
            if (matcher.group(3) != null) {
                return DataBinding.valueOf(matcher.group(3));
            } else {
                return FunctionBinding.valueOf(matcher.group(1), matcher.group(2), context, manager);
            }
        } else {
            throw new IllegalArgumentException(value + " is not a binding");
        }
    }

    @NonNull
    public abstract Value evaluate(Context context, Value data, int index);

    @NonNull
    public abstract String toString();

    public static class DataBinding extends Binding {

        private static final LruCache<String, DataBinding> DATA_BINDING_CACHE = new LruCache<>(64);

        @NonNull
        private final String[] tokens;

        private DataBinding(@NonNull String[] tokens) {
            this.tokens = tokens;
        }

        @NonNull
        public static DataBinding valueOf(@NonNull String path) {
            DataBinding binding = DATA_BINDING_CACHE.get(path);
            if (null == binding) {
                StringTokenizer tokenizer = new StringTokenizer(path, DATA_PATH_DELIMITERS);
                String[] tokens = new String[0];
                while (tokenizer.hasMoreTokens()) {
                    tokens = Arrays.copyOf(tokens, tokens.length + 1);
                    tokens[tokens.length - 1] = tokenizer.nextToken();
                }
                binding = new DataBinding(tokens);
                DATA_BINDING_CACHE.put(path, binding);
            }
            return binding;
        }

        private static Result resolve(String[] tokens, Value data, int index) {
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

        public Iterator<String> getTokens() {
            return new SimpleArrayIterator<>(this.tokens);
        }

        @Override
        public Value copy() {
            return this;
        }

        @NonNull
        @Override
        public Value evaluate(Context context, Value data, int index) {
            Result result = resolve(tokens, data, index);
            return result.isSuccess() ? result.value : Null.INSTANCE;
        }

        @NonNull
        @Override
        public String toString() {

            //noinspection StringBufferReplaceableByString
            return new StringBuilder()
                    .append(BINDING_PREFIX_0)
                    .append(BINDING_PREFIX_1)
                    .append(Utils.join(tokens, SIMPLE_DATA_PATH_DELIMITER))
                    .append(BINDING_SUFFIX).toString();
        }
    }

    public static class FunctionBinding extends Binding {

        @NonNull
        public final Function function;

        @Nullable
        private final Value[] arguments;

        public FunctionBinding(@NonNull Function function, @Nullable Value[] arguments) {
            this.arguments = arguments;
            this.function = function;
        }

        public static FunctionBinding valueOf(@NonNull String name, @NonNull String args, Context context, @NonNull FunctionManager manager) {
            Function function = manager.get(name);
            String[] tokens = FUNCTION_ARGS_DELIMITER.split(args);
            Value[] arguments = new Value[tokens.length];
            String token;
            Value resolved;
            for (int i = 0; i < tokens.length; i++) {
                token = tokens[i].trim();
                if (!token.isEmpty() && token.charAt(0) == '\'') {
                    token = token.substring(1, token.length() - 1);
                    resolved = new Primitive(token);
                } else {
                    resolved = AttributeProcessor.staticPrecompile(new Primitive(token), context, manager);
                }
                arguments[i] = resolved != null ? resolved : new Primitive(token);
            }
            return new FunctionBinding(function, arguments);
        }

        private static Value[] resolve(Context context, Value[] in, Value data, int index) {

            //noinspection ConstantConditions because we want it to crash, it is an illegal state anyway
            Value[] out = new Value[in.length];
            for (int i = 0; i < in.length; i++) {
                out[i] = AttributeProcessor.evaluate(context, in[i], data, index);
            }

            return out;
        }

        public Iterator<Value> getTokens() {
            return new SimpleArrayIterator<>(this.arguments);
        }

        @Override
        public Value copy() {
            return this;
        }

        @NonNull
        @Override
        public Value evaluate(Context context, Value data, int index) {
            Value[] arguments = resolve(context, this.arguments, data, index);
            return this.function.call(data, index, arguments);
        }

        @NonNull
        @Override
        public String toString() {
            /*String context = "@{" + Utils.join(arguments, ".") + "}";
            String functions = "";
            functions = "${" + function.getName() + "(" + Utils.join(arguments, ",", Utils.STYLE_SINGLE) + ")}";*/

            return "crap";
        }
    }
}
