/*
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.android.proteus.parser.custom;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.builder.LayoutBuilder;
import com.flipkart.android.proteus.parser.Attributes;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.parser.Parser;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.processor.AttributeProcessor;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Result;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.toolbox.Utils;
import com.flipkart.android.proteus.view.ProteusAspectRatioFrameLayout;
import com.flipkart.android.proteus.view.ProteusView;
import com.flipkart.android.proteus.view.manager.ProteusViewManager;
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

        addHandler(Attributes.ViewGroup.Children, new AttributeProcessor<T>() {
            @Override
            public void handle(String key, JsonElement value, T view) {
                handleChildren((ProteusView) view);
            }
        });
    }

    @Override
    public boolean handleChildren(ProteusView view) {
        ProteusViewManager viewManager = view.getViewManager();
        LayoutBuilder builder = viewManager.getLayoutBuilder();
        JsonObject layout = viewManager.getLayout();
        JsonElement children = layout.get(ProteusConstants.CHILDREN);
        JsonObject data = viewManager.getDataContext().getData();
        int dataIndex = viewManager.getDataContext().getIndex();
        Styles styles = view.getViewManager().getStyles();

        if (null != children && !children.isJsonNull()) {
            if (children.isJsonArray()) {
                ProteusView child;
                for (JsonElement jsonElement : children.getAsJsonArray()) {
                    child = builder.build((ViewGroup) view, jsonElement.getAsJsonObject(), data, dataIndex, styles);
                    addView(view, child);
                }
            } else if (children.isJsonObject()) {
                handleDataDrivenChildren(builder, view, viewManager, children.getAsJsonObject(), data, dataIndex);
            }
        }

        return true;
    }

    private void handleDataDrivenChildren(LayoutBuilder builder, ProteusView parent, ProteusViewManager viewManager, JsonObject children, JsonObject data, int dataIndex) {

        String dataPath = children.get(ProteusConstants.DATA).getAsString().substring(1);
        viewManager.setDataPathForChildren(dataPath);

        Result result = Utils.readJson(dataPath, data, dataIndex);
        JsonElement element = result.isSuccess() ? result.element : null;

        JsonObject childLayout = children.getAsJsonObject(ProteusConstants.LAYOUT);

        viewManager.setChildLayout(childLayout);

        if (null == element || element.isJsonNull()) {
            return;
        }

        int length = element.getAsJsonArray().size();

        ProteusView child;
        for (int index = 0; index < length; index++) {
            child = builder.build((ViewGroup) parent, childLayout, viewManager.getDataContext().getData(), index, viewManager.getStyles());
            if (child != null) {
                this.addView(parent, child);
            }
        }
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
