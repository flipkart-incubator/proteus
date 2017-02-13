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

package com.flipkart.android.proteus.parser.custom;


import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.flipkart.android.proteus.AttributeProcessor;
import com.flipkart.android.proteus.AttributeResource;
import com.flipkart.android.proteus.Layout;
import com.flipkart.android.proteus.ObjectValue;
import com.flipkart.android.proteus.ProteusLayoutInflater;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.Resource;
import com.flipkart.android.proteus.StyleResource;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.processor.ColorResourceProcessor;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.toolbox.Attributes;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.view.ProteusProgressBar;
import com.google.gson.JsonObject;

/**
 * @author Aditya Sharat
 */
public class ProgressBarParser<T extends ProgressBar> extends ViewTypeParser<T> {

    @Override
    public ProteusView createView(ProteusLayoutInflater inflater, ViewGroup parent, Layout layout, JsonObject data, Styles styles, ProteusLayoutInflater.Callback callback, ProteusLayoutInflater.ImageLoader loader, int index) {
        return new ProteusProgressBar(parent.getContext());
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor(Attributes.ProgressBar.Max, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                view.setMax((int) ParseHelper.parseDouble(value));
            }
        });
        addAttributeProcessor(Attributes.ProgressBar.Progress, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                view.setProgress((int) ParseHelper.parseDouble(value));
            }
        });

        addAttributeProcessor(Attributes.ProgressBar.ProgressTint, new AttributeProcessor<T>() {
            @Override
            public void handleValue(T view, Value value) {
                if (!value.isObject()) {
                    return;
                }
                int background = Color.TRANSPARENT;
                int progress = Color.TRANSPARENT;
                ObjectValue object = value.getAsObject();
                String string = object.getAsString("background");
                if (string != null) {
                    background = ParseHelper.parseColor(string);
                }
                string = object.getAsString("progress");
                if (string != null) {
                    progress = ParseHelper.parseColor(string);
                }

                view.setProgressDrawable(getLayerDrawable(progress, background));
            }

            @Override
            public void handleResource(T view, Resource resource) {
                Drawable d = resource.getDrawable(view.getContext());
                view.setProgressDrawable(d);
            }

            @Override
            public void handleAttributeResource(T view, AttributeResource attribute) {
                TypedArray a = attribute.apply(view.getContext());
                view.setProgressDrawable(a.getDrawable(0));
            }

            @Override
            public void handleStyleResource(T view, StyleResource style) {
                TypedArray a = style.apply(view.getContext());
                view.setProgressDrawable(a.getDrawable(0));
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addAttributeProcessor(Attributes.ProgressBar.SecondaryProgressTint, new ColorResourceProcessor<T>() {
                @Override
                public void setColor(T view, int color) {

                }

                @Override
                public void setColor(T view, ColorStateList colors) {
                    //noinspection AndroidLintNewApi
                    view.setSecondaryProgressTintList(colors);
                }
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addAttributeProcessor(Attributes.ProgressBar.IndeterminateTint, new ColorResourceProcessor<T>() {
                @Override
                public void setColor(T view, int color) {

                }

                @Override
                public void setColor(T view, ColorStateList colors) {
                    //noinspection AndroidLintNewApi
                    view.setIndeterminateTintList(colors);
                }
            });
        }
    }

    private Drawable getLayerDrawable(int progress, int background) {
        ShapeDrawable shape = new ShapeDrawable();
        shape.getPaint().setStyle(Paint.Style.FILL);
        shape.getPaint().setColor(background);

        ShapeDrawable shapeD = new ShapeDrawable();
        shapeD.getPaint().setStyle(Paint.Style.FILL);
        shapeD.getPaint().setColor(progress);
        ClipDrawable clipDrawable = new ClipDrawable(shapeD, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        return new LayerDrawable(new Drawable[]{shape, clipDrawable});
    }
}
