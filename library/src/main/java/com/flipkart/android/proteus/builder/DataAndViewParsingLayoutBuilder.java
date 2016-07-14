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

package com.flipkart.android.proteus.builder;

import android.support.annotation.NonNull;
import android.view.View;

import com.flipkart.android.proteus.toolbox.IdGenerator;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.toolbox.Utils;
import com.flipkart.android.proteus.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * A layout builder which can parse @data and @view blocks before passing it on to
 * {@link SimpleLayoutBuilder}
 */
public class DataAndViewParsingLayoutBuilder extends DataParsingLayoutBuilder {

    private Map<String, JsonObject> layouts;

    protected DataAndViewParsingLayoutBuilder(Map<String, JsonObject> layouts, @NonNull IdGenerator idGenerator) {
        super(idGenerator);
        this.layouts = layouts;
    }

    @Override
    protected ProteusView onUnknownViewEncountered(String type, View parent, JsonObject source, JsonObject data, int index, Styles styles) {
        JsonElement element = null;
        if (layouts != null) {
            element = layouts.get(type);
        }
        if (element != null && !element.isJsonNull()) {
            JsonObject layout = element.getAsJsonObject();
            layout = Utils.mergeLayouts(layout, source);
            ProteusView view = build(parent, layout, data, index, styles);
            onViewBuiltFromViewProvider(view, type, parent, index);
            return view;
        }
        return super.onUnknownViewEncountered(type, parent, source, data, index, styles);
    }

    private void onViewBuiltFromViewProvider(ProteusView view, String type, View parent, int childIndex) {
        if (listener != null) {
            listener.onViewBuiltFromViewProvider(view, parent, type, childIndex);
        }
    }
}
