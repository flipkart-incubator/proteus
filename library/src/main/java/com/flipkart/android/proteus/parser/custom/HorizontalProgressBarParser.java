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

import com.flipkart.android.proteus.LayoutParser;
import com.flipkart.android.proteus.parser.Attributes;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.parser.Parser;
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

    public HorizontalProgressBarParser(Parser<T> wrappedParser) {
        super(wrappedParser);
    }

    @Override
    public ProteusView createView(ViewGroup parent, LayoutParser layout, JsonObject data, Styles styles, int index) {
        return new ProteusHorizontalProgressBar(parent.getContext());
    }

    @Override
    protected void prepareHandlers() {
        super.prepareHandlers();

        addHandler(Attributes.ProgressBar.ProgressTint, new AttributeProcessor<T>() {
            @Override
            public void handle(T view, String key, LayoutParser parser) {
                if (!parser.isObject()) {
                    return;
                }
                parser = parser.peek();
                int background = Color.TRANSPARENT;
                int progress = Color.TRANSPARENT;

                String value = parser.getString("background");
                if (value != null) {
                    background = ParseHelper.parseColor(value);
                }
                value = parser.getString("progress");
                if (value != null) {
                    progress = ParseHelper.parseColor(value);
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
