/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Flipkart Internet Pvt. Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.flipkart.android.proteus.toolbox;

import java.util.regex.Pattern;

/**
 * Contains data binding constants
 */
public class ProteusConstants {

    public static final String TYPE = "type";
    public static final String ID = "id";
    public static final String TAG = "tag";

    public static final String CHILDREN = "children";
    public static final String CHILD_TYPE = "childType";

    public static final String STYLE_DELIMITER = "\\.";

    public static final String DATA_CONTEXT = "dataContext";
    public static final String CHILD_DATA_CONTEXT = "childDataContext";

    public static final String DATA_VISIBILITY = "data";
    public static final String DATA_NULL = "null";

    public static final char DATA_PREFIX = '$';
    public static final char REGEX_PREFIX = '~';
    public static final Pattern REGEX_PATTERN = Pattern.compile("\\{\\{(\\S+?)\\}\\}\\$\\((.+?)\\)|\\{\\{(\\S+?)\\}\\}");

    public static final String DATA_PATH_DELIMITER = "\\.|\\[|\\]";
    public static final String DATA_PATH_DELIMITERS = ".[]";
    public static final String DATA_PATH_SIMPLE_DELIMITER = "\\.";

    public static final String INDEX = "$index";
    public static final String ARRAY_DATA_LENGTH_REFERENCE = "$length";
    public static final String ARRAY_DATA_LAST_INDEX_REFERENCE = "$last";

    private static boolean isLoggingEnabled = false;

    public static void setIsLoggingEnabled(boolean isLoggingEnabled) {
        ProteusConstants.isLoggingEnabled = isLoggingEnabled;
    }

    public static boolean isLoggingEnabled() {
        return ProteusConstants.isLoggingEnabled;
    }
}
