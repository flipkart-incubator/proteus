/*
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
