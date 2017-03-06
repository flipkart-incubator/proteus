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

package com.flipkart.android.proteus.toolbox;

import com.flipkart.android.proteus.value.Array;
import com.flipkart.android.proteus.value.ObjectValue;
import com.flipkart.android.proteus.value.Value;

import java.util.Map;

/**
 * @author Aditya Sharat
 */
public class Utils {

    public static final String LIB_NAME = "proteus";
    public static final String VERSION = "5.0.0-SNAPSHOT";

    public static ObjectValue addAllEntries(ObjectValue destination, ObjectValue source) {
        for (Map.Entry<String, Value> entry : source.entrySet()) {
            if (destination.get(entry.getKey()) != null) {
                continue;
            }
            destination.add(entry.getKey(), entry.getValue());
        }
        return destination;
    }

    public static String getStringFromArray(String[] array, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static String getStringFromArray(Array array, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).isPrimitive()) {
                sb.append(array.get(i).getAsString());
            } else {
                sb.append(array.get(i).toString());
            }
            if (i < array.size() - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static String getStringFromArray(Value[] array, String delimiter) {
        return getStringFromArray(new Array(array), delimiter);
    }

    public static String getVersion() {
        return LIB_NAME + ":" + VERSION;
    }
}
