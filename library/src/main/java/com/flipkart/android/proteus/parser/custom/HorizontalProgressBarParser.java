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

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.Gravity;
import android.view.ViewGroup;

import com.flipkart.android.proteus.Layout;
import com.flipkart.android.proteus.Object;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.parser.Attributes;
import com.flipkart.android.proteus.parser.BaseTypeParser;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.processor.AttributeProcessor;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.view.ProteusHorizontalProgressBar;
import com.flipkart.android.proteus.view.ProteusView;
import com.flipkart.android.proteus.view.custom.HorizontalProgressBar;
import com.google.gson.JsonObject;

/**
 * HorizontalProgressBarParser
 *
 * @author Aditya Sharat
 */
public class HorizontalProgressBarParser<T extends HorizontalProgressBar> extends WrappableParser<T> {

    public HorizontalProgressBarParser(BaseTypeParser<T> wrappedParser) {
        super(wrappedParser);
    }

    @Override
    public ProteusView createView(ViewGroup parent, Layout layout, JsonObject data, Styles styles, int index) {
        return new ProteusHorizontalProgressBar(parent.getContext());
    }

    @Override
    protected void registerAttributeProcessors() {
        super.registerAttributeProcessors();

        addAttributeProcessor(Attributes.ProgressBar.ProgressTint, new AttributeProcessor<T>() {
            @Override
            public void handle(T view, Value value) {
                if (!value.isObject()) {
                    return;
                }
                int background = Color.TRANSPARENT;
                int progress = Color.TRANSPARENT;
                Object object = value.getAsObject();
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
        });
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
