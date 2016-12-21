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

package com.flipkart.android.proteus.builder;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.LayoutParser;
import com.flipkart.android.proteus.toolbox.IdGenerator;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.view.ProteusView;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * A layout builder which can parse @data and @view blocks before passing it on to
 * {@link SimpleLayoutInflater}
 */
public class DataAndViewParsingLayoutInflater extends DataParsingLayoutInflater {

    private Map<String, Object> layouts;

    protected DataAndViewParsingLayoutInflater(Map<String, Object> layouts, @NonNull IdGenerator idGenerator) {
        super(idGenerator);
        this.layouts = layouts;
    }

    @Override
    protected ProteusView onUnknownViewEncountered(String type, ViewGroup parent, LayoutParser include, JsonObject data, int index, Styles styles) {
        Object layout = null;
        if (layouts != null) {
            layout = layouts.get(type);
        }
        if (layout != null) {
            ProteusView view = build(parent, include.merge(layout), data, styles, index);
            onViewBuiltFromViewProvider(view, type, parent, index);
            return view;
        }
        return super.onUnknownViewEncountered(type, parent, include, data, index, styles);
    }

    private void onViewBuiltFromViewProvider(ProteusView view, String type, View parent, int childIndex) {
        if (listener != null) {
            listener.onViewBuiltFromViewProvider(view, parent, type, childIndex);
        }
    }

    public void setLayouts(Map<String, Object> layouts) {
        this.layouts = layouts;
    }
}
