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

package com.flipkart.android.proteus.parser.custom;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.Layout;
import com.flipkart.android.proteus.LayoutParser;
import com.flipkart.android.proteus.Object;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.builder.ProteusLayoutInflater;
import com.flipkart.android.proteus.parser.Attributes;
import com.flipkart.android.proteus.parser.BaseTypeParser;
import com.flipkart.android.proteus.parser.ParseHelper;
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

import java.util.Iterator;

public class ViewGroupParser<T extends ViewGroup> extends WrappableParser<T> {

    private static final String LAYOUT_MODE_CLIP_BOUNDS = "clipBounds";
    private static final String LAYOUT_MODE_OPTICAL_BOUNDS = "opticalBounds";

    public ViewGroupParser(BaseTypeParser<T> wrappedParser) {
        super(wrappedParser);
    }

    @Override
    public ProteusView createView(ViewGroup parent, Layout layout, JsonObject data, Styles styles, int index) {
        return new ProteusAspectRatioFrameLayout(parent.getContext());
    }

    @Override
    protected void registerAttributeProcessors() {
        super.registerAttributeProcessors();
        addAttributeProcessor(Attributes.ViewGroup.ClipChildren, new StringAttributeProcessor<T>() {
            @Override
            public void handle(T view, String value) {
                boolean clipChildren = ParseHelper.parseBoolean(value);
                view.setClipChildren(clipChildren);
            }
        });

        addAttributeProcessor(Attributes.ViewGroup.ClipToPadding, new StringAttributeProcessor<T>() {
            @Override
            public void handle(T view, String value) {
                boolean clipToPadding = ParseHelper.parseBoolean(value);
                view.setClipToPadding(clipToPadding);
            }
        });

        addAttributeProcessor(Attributes.ViewGroup.LayoutMode, new StringAttributeProcessor<T>() {
            @Override
            public void handle(T view, String value) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    if (LAYOUT_MODE_CLIP_BOUNDS.equals(value)) {
                        view.setLayoutMode(ViewGroup.LAYOUT_MODE_CLIP_BOUNDS);
                    } else if (LAYOUT_MODE_OPTICAL_BOUNDS.equals(value)) {
                        view.setLayoutMode(ViewGroup.LAYOUT_MODE_OPTICAL_BOUNDS);
                    }
                }
            }
        });

        addAttributeProcessor(Attributes.ViewGroup.SplitMotionEvents, new StringAttributeProcessor<T>() {
            @Override
            public void handle(T view, String value) {
                boolean splitMotionEvents = ParseHelper.parseBoolean(value);
                view.setMotionEventSplittingEnabled(splitMotionEvents);
            }
        });

        addAttributeProcessor(Attributes.ViewGroup.Children, new AttributeProcessor<T>() {
            @Override
            public void handle(T view, Value value) {
                handleChildren((ProteusView) view, value);
            }

            @Override
            public LayoutParser minify(ProteusLayoutInflater layoutInflater, String attribute, LayoutParser children) {
                if (children.isArray()) {
                    LayoutParser child;
                    int i = 0;
                    while (children.hasNext()) {
                        children.next();
                        child = layoutInflater.minify(children.clone());
                        children.set(i, child);
                        i++;
                    }
                }
                return children;
            }
        });
    }

    @Override
    public boolean handleChildren(ProteusView view, Value children) {
        ProteusViewManager viewManager = view.getViewManager();
        ProteusLayoutInflater layoutInflater = viewManager.getProteusLayoutInflater();
        JsonObject data = viewManager.getScope().getData();
        int dataIndex = viewManager.getScope().getIndex();
        Styles styles = view.getViewManager().getStyles();

        if (children.isArray()) {
            ProteusView child;
            Iterator<Value> iterator = children.getAsArray().iterator();
            while (iterator.hasNext()) {
                child = layoutInflater.build((ViewGroup) view, iterator.next().getAsLayout(), data, styles, dataIndex);
                addView(view, child);
            }
        } else if (children.isObject()) {
            handleDataDrivenChildren(layoutInflater, view, viewManager, children.getAsObject(), data, styles, dataIndex);
        }

        return true;
    }

    private void handleDataDrivenChildren(ProteusLayoutInflater layoutInflater, ProteusView parent, ProteusViewManager viewManager,
                                          Object children, JsonObject data, Styles styles, int dataIndex) {

        //noinspection ConstantConditions : We want to throw an exception (for now)
        String dataPath = children.getAsString(ProteusConstants.DATA).substring(1);
        viewManager.setDataPathForChildren(dataPath);

        Result result = Utils.readJson(dataPath, data, dataIndex);
        JsonElement element = result.isSuccess() ? result.element : null;

        Layout childLayout = children.getAsLayout(ProteusConstants.LAYOUT);

        viewManager.setChildLayout(childLayout);

        if (null == element || element.isJsonNull()) {
            return;
        }

        int length = element.getAsJsonArray().size();

        ProteusView child;
        for (int index = 0; index < length; index++) {
            child = layoutInflater.build((ViewGroup) parent, childLayout, data, styles, index);
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
