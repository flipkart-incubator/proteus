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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.flipkart.android.proteus.toolbox.Formatter;
import com.flipkart.android.proteus.toolbox.IdGenerator;
import com.flipkart.android.proteus.toolbox.IdGeneratorImpl;

import java.util.Map;

/**
 * Factory class for creating Layout inflaters with different predefined behaviours. NOTE: This is the
 * only way to create proteus layout inflaters.
 */
public class LayoutInflaterFactory {

    private final Map<String, ViewTypeParser> parsers;
    private final Map<String, Formatter> formatters;

    LayoutInflaterFactory(Map<String, ViewTypeParser> parsers, Map<String, Formatter> formatters) {
        this.parsers = parsers;
        this.formatters = formatters;
    }

    /**
     * Returns a layout inflater which can parse @data blocks as well as custom view blocks.
     * See {@link DataParsingLayoutInflater}
     *
     * @return A new {@link DataAndViewParsingLayoutInflater}
     */
    public DataAndViewParsingLayoutInflater getDataAndViewParsingLayoutInflater(@NonNull IdGenerator idGenerator, @Nullable ProteusLayoutInflater.ImageLoader loader, @Nullable ProteusLayoutInflater.Callback callback) {
        return new DataAndViewParsingLayoutInflater(parsers, formatters, idGenerator, loader, callback);
    }

    public DataAndViewParsingLayoutInflater getDataAndViewParsingLayoutInflater() {
        return getDataAndViewParsingLayoutInflater(new IdGeneratorImpl(), null, null);
    }

    /**
     * Returns a layout inflater which can parse @data blocks. See {@link DataParsingLayoutInflater}
     *
     * @return A new {@link DataParsingLayoutInflater}
     */
    public DataParsingLayoutInflater getDataParsingLayoutInflater(@NonNull IdGenerator idGenerator, @Nullable ProteusLayoutInflater.ImageLoader loader, @Nullable ProteusLayoutInflater.Callback callback) {
        return new DataParsingLayoutInflater(parsers, formatters, idGenerator, loader, callback);
    }

    public DataParsingLayoutInflater getDataParsingLayoutInflater() {
        return getDataParsingLayoutInflater(new IdGeneratorImpl(), null, null);
    }

    /**
     * Returns a simple layout inflater. See {@link SimpleLayoutInflater}
     *
     * @return A new {@link SimpleLayoutInflater}
     */
    public SimpleLayoutInflater getSimpleLayoutInflater(@NonNull IdGenerator idGenerator, @Nullable ProteusLayoutInflater.ImageLoader loader, @Nullable ProteusLayoutInflater.Callback callback) {
        return new SimpleLayoutInflater(parsers, formatters, idGenerator, loader, callback);
    }

    public SimpleLayoutInflater getSimpleLayoutInflater() {
        return getSimpleLayoutInflater(new IdGeneratorImpl(), null, null);
    }
}
