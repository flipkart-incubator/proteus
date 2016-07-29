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

package com.flipkart.android.proteus.parser.custom;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.DataContext;
import com.flipkart.android.proteus.builder.LayoutBuilder;
import com.flipkart.android.proteus.parser.Attributes;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.parser.Parser;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.view.ProteusAspectRatioFrameLayout;
import com.flipkart.android.proteus.view.ProteusView;
import com.flipkart.android.proteus.view.manager.ProteusViewManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ViewGroupParser<T extends ViewGroup> extends WrappableParser<T> {

    private static final String LAYOUT_MODE_CLIP_BOUNDS = "clipBounds";
    private static final String LAYOUT_MODE_OPTICAL_BOUNDS = "opticalBounds";

    public ViewGroupParser(Parser<T> wrappedParser) {
        super(wrappedParser);
    }

    @Override
    public ProteusView createView(ViewGroup parent, JsonObject layout, JsonObject data, Styles styles, int index) {
        return new ProteusAspectRatioFrameLayout(parent.getContext());
    }

    @Override
    protected void prepareHandlers() {
        super.prepareHandlers();
        addHandler(Attributes.ViewGroup.ClipChildren, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                boolean clipChildren = ParseHelper.parseBoolean(attributeValue);
                view.setClipChildren(clipChildren);
            }
        });

        addHandler(Attributes.ViewGroup.ClipToPadding, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                boolean clipToPadding = ParseHelper.parseBoolean(attributeValue);
                view.setClipToPadding(clipToPadding);
            }
        });

        addHandler(Attributes.ViewGroup.LayoutMode, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    if (LAYOUT_MODE_CLIP_BOUNDS.equals(attributeValue)) {
                        view.setLayoutMode(ViewGroup.LAYOUT_MODE_CLIP_BOUNDS);
                    } else if (LAYOUT_MODE_OPTICAL_BOUNDS.equals(attributeValue)) {
                        view.setLayoutMode(ViewGroup.LAYOUT_MODE_OPTICAL_BOUNDS);
                    }
                }
            }
        });

        addHandler(Attributes.ViewGroup.SplitMotionEvents, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                boolean splitMotionEvents = ParseHelper.parseBoolean(attributeValue);
                view.setMotionEventSplittingEnabled(splitMotionEvents);
            }
        });
    }

    @Override
    public boolean handleChildren(ProteusView view) {
        ProteusViewManager viewManager = view.getViewManager();
        LayoutBuilder layoutBuilder = viewManager.getLayoutBuilder();
        DataContext dataContext = viewManager.getDataContext();
        JsonObject layout = viewManager.getLayout();

        if (dataContext == null || layout == null) {
            return false;
        }

        JsonObject data = dataContext.getData();
        JsonElement element = layout.get(ProteusConstants.CHILDREN);
        JsonArray children;
        ProteusView child;

        if (!(element instanceof JsonArray) || layoutBuilder == null) {
            return false;
        }

        children = element.getAsJsonArray();
        for (int index = 0; index < children.size(); index++) {
            child = layoutBuilder.build((ViewGroup) view, children.get(index).getAsJsonObject(), data, viewManager.getDataContext().getIndex(), view.getViewManager().getStyles());
            addView(view, child);
        }

        return true;
    }

    @Override
    public boolean addView(ProteusView parent, ProteusView view) {
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).addView((View) view);
            return true;
        }
        return false;
    }
}
