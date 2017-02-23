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

/**
 * Contains data binding constants
 */
public class ProteusConstants {

    public static final String TYPE = "type";
    public static final String LAYOUT = "layout";

    public static final String DATA = "data";
    public static final String COLLECTION = "collection";

    public static final String DATA_NULL = "null";

    public static final String STYLE_DELIMITER = "\\.";
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
