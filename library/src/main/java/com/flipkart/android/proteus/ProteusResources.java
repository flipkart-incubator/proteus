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
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.Value;

import java.util.Map;

/**
 * ProteusResources
 *
 * @author adityasharat
 */

public class ProteusResources {

    @NonNull
    private final Map<String, ViewTypeParser> parsers;

    @Nullable
    private final LayoutManager layoutManager;

    @Nullable
    private final FormatterManager formatterManager;

    @Nullable
    private final StyleManager styleManager;

    ProteusResources(@NonNull Map<String, ViewTypeParser> parsers, @Nullable LayoutManager layoutManager,
                     @Nullable FormatterManager formatterManager, @Nullable StyleManager styleManager) {
        this.parsers = parsers;
        this.layoutManager = layoutManager;
        this.formatterManager = formatterManager;
        this.styleManager = styleManager;
    }

    @Nullable
    public Formatter getFormatter(@NonNull String name) {
        return null != formatterManager ? formatterManager.get(name) : null;
    }

    @Nullable
    public Layout getLayout(@NonNull String name) {
        return null != layoutManager ? layoutManager.get(name) : null;
    }

    @NonNull
    public Map<String, ViewTypeParser> getParsers() {
        return parsers;
    }

    @Nullable
    public Map<String, Value> getStyle(String name) {
        return null != styleManager ? styleManager.get(name) : null;
    }

    public static abstract class LayoutManager {

        @Nullable
        protected abstract Map<String, Layout> getLayouts();

        @Nullable
        private Layout get(@NonNull String name) {
            return null != getLayouts() ? getLayouts().get(name) : null;
        }
    }

    public static abstract class FormatterManager {

        @Nullable
        protected abstract Map<String, Formatter> getFormatters();

        @Nullable
        private Formatter get(@NonNull String name) {
            return null != getFormatters() ? getFormatters().get(name) : null;
        }
    }

    public static abstract class StyleManager {
        @Nullable
        protected abstract Styles getStyles();

        @Nullable
        private Map<String, Value> get(@NonNull String name) {
            return null != getStyles() ? getStyles().get(name) : null;
        }
    }

}
