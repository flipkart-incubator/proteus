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
import android.util.Log;
import android.util.LruCache;

import com.flipkart.android.proteus.Function;
import com.flipkart.android.proteus.FunctionManager;
import com.flipkart.android.proteus.ProteusConstants;
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

    public static final String DATA_PATH_DELIMITERS = ".]";

    public static final char DELIMITER_OBJECT = '.';
    public static final char DELIMITER_ARRAY_OPENING = '[';
    public static final char DELIMITER_ARRAY_CLOSING = ']';

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
        private final Token[] tokens;

        private DataBinding(@NonNull Token[] tokens) {
            this.tokens = tokens;
        }

        @NonNull
        public static DataBinding valueOf(@NonNull String path) {
            DataBinding binding = DATA_BINDING_CACHE.get(path);
            if (null == binding) {
                StringTokenizer tokenizer = new StringTokenizer(path, DATA_PATH_DELIMITERS, true);
                Token[] tokens = new Token[0];
                String token;
                char first;
                int length;
                while (tokenizer.hasMoreTokens()) {
                    token = tokenizer.nextToken();
                    length = token.length();
                    first = token.charAt(0);
                    if (length == 1 && first == DELIMITER_OBJECT) {
                        continue;
                    }
                    if (length == 1 && first == DELIMITER_ARRAY_CLOSING) {
                        tokens = correctPreviousToken(tokens);
                        continue;
                    }
                    tokens = Arrays.copyOf(tokens, tokens.length + 1);
                    tokens[tokens.length - 1] = new Token(token, false, false);
                }
                binding = new DataBinding(tokens);
                DATA_BINDING_CACHE.put(path, binding);
            }
            return binding;
        }

        private static void assign(Token[] tokens, @NonNull Value value, @NonNull Value data, int dataIndex) {
            Value current = data;
            Token token;
            int index = dataIndex;

            for (int i = 0; i < tokens.length - 1; i++) {
                token = tokens[i];
                if (token.isArrayIndex) {
                    try {
                        index = getArrayIndex(token.value, dataIndex);
                    } catch (NumberFormatException e) {
                        return;
                    }
                    current = getArrayItem(current.getAsArray(), index, token.isArray);
                } else if (token.isArray) {
                    current = getArray(current, token.value, index);
                } else {
                    current = getObject(current, token, index);
                }
            }

            token = tokens[tokens.length - 1];

            if (token.isArrayIndex) {
                try {
                    index = getArrayIndex(token.value, dataIndex);
                } catch (NumberFormatException e) {
                    return;
                }
                getArrayItem(current.getAsArray(), index, false);
                current.getAsArray().remove(index);
                current.getAsArray().add(index, value);
            } else {
                current.getAsObject().add(token.value, value);
            }
        }

        @NonNull
        private static Value getObject(Value parent, Token token, int index) {
            Value temp;
            ObjectValue object;
            if (parent.isArray()) {
                temp = parent.getAsArray().get(index);
                if (temp != null && temp.isObject()) {
                    object = temp.getAsObject();
                } else {
                    object = new ObjectValue();
                    parent.getAsArray().remove(index);
                    parent.getAsArray().add(index, object);
                }
            } else {
                temp = parent.getAsObject().get(token.value);
                if (temp != null && temp.isObject()) {
                    object = temp.getAsObject();
                } else {
                    object = new ObjectValue();
                    parent.getAsObject().add(token.value, object);
                }

            }
            return object;
        }

        @NonNull
        private static Array getArray(Value parent, String token, int index) {
            Value temp;
            Array array;
            if (parent.isArray()) {
                temp = parent.getAsArray().get(index);
                if (temp != null && temp.isArray()) {
                    array = temp.getAsArray();
                } else {
                    array = new Array();
                    parent.getAsArray().remove(index);
                    parent.getAsArray().add(index, array);
                }
            } else {
                temp = parent.getAsObject().get(token);
                if (temp != null && temp.isArray()) {
                    array = temp.getAsArray();
                } else {
                    array = new Array();
                    parent.getAsObject().add(token, array);
                }
            }
            return array;
        }

        @NonNull
        private static Value getArrayItem(Array array, int index, boolean isArray) {
            if (index >= array.size()) {
                while (array.size() < index) {
                    array.add(Null.INSTANCE);
                }
                if (isArray) {
                    array.add(new Array());
                } else {
                    array.add(new ObjectValue());
                }
            }
            return array.get(index);
        }

        private static int getArrayIndex(@NonNull String token, int dataIndex) throws NumberFormatException {
            int index;
            if (INDEX.equals(token)) {
                index = dataIndex;
            } else {
                index = Integer.parseInt(token);
            }
            return index;
        }

        @NonNull
        private static Token[] correctPreviousToken(Token[] tokens) {
            Token previous = tokens[tokens.length - 1];
            int index = previous.value.indexOf(DELIMITER_ARRAY_OPENING);
            String prefix = previous.value.substring(0, index);
            String suffix = previous.value.substring(index + 1, previous.value.length());

            if (prefix.equals(ProteusConstants.EMPTY)) {
                Token token = tokens[tokens.length - 1];
                tokens[tokens.length - 1] = new Token(token.value, true, false);
            } else {
                tokens[tokens.length - 1] = new Token(prefix, true, false);
            }

            tokens = Arrays.copyOf(tokens, tokens.length + 1);
            tokens[tokens.length - 1] = new Token(suffix, false, true);

            return tokens;
        }

        @NonNull
        private static Result resolve(Token[] tokens, Value data, int index) {
            // replace INDEX with index value
            if (tokens.length == 1 && INDEX.equals(tokens[0].value)) {
                return Result.success(new Primitive(String.valueOf(index)));
            } else {
                Value elementToReturn = data;
                Value tempElement;
                Array tempArray;

                for (int i = 0; i < tokens.length; i++) {
                    String segment = tokens[i].value;
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
                    .append(Utils.join(Token.getValues(tokens), String.valueOf(DELIMITER_OBJECT)))
                    .append(BINDING_SUFFIX).toString();
        }

        public Iterator<Token> getTokens() {
            return new SimpleArrayIterator<>(this.tokens);
        }

        public void assign(Value value, Value data, int index) {
            assign(tokens, value, data, index);
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
                    resolved = AttributeProcessor.staticPreCompile(new Primitive(token), context, manager);
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
            try {
                return this.function.call(data, index, arguments);
            } catch (Exception e) {
                if (ProteusConstants.isLoggingEnabled()) {
                    Log.e(Utils.LIB_NAME, e.getMessage(), e);
                }
                return data;
            }
        }

        @NonNull
        @Override
        public String toString() {
            return String.format("@{fn:%s(%s)}", function.getName(), Utils.join(arguments, ",", Utils.STYLE_SINGLE));
        }
    }

    public static class Token {

        @NonNull
        public final String value;

        public final boolean isArray;

        public final boolean isArrayIndex;

        public final boolean isBinding = false;

        public Token(@NonNull String value, boolean isArray, boolean isArrayIndex) {
            this.value = value;
            this.isArray = isArray;
            this.isArrayIndex = isArrayIndex;
        }

        public static String[] getValues(Token[] tokens) {
            String[] values = new String[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                values[i] = tokens[i].value;
            }
            return values;
        }
    }
}
