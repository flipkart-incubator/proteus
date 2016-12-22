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

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import com.flipkart.android.proteus.LayoutParser;
import com.flipkart.android.proteus.parser.Attributes;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.parser.BaseTypeParser;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.processor.DimensionAttributeProcessor;
import com.flipkart.android.proteus.processor.DrawableResourceProcessor;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.view.ProteusFixedRatingBar;
import com.flipkart.android.proteus.view.ProteusView;
import com.flipkart.android.proteus.view.custom.FixedRatingBar;
import com.google.gson.JsonObject;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class RatingBarParser<T extends FixedRatingBar> extends WrappableParser<T> {

    public RatingBarParser(BaseTypeParser<T> wrappedParser) {
        super(wrappedParser);
    }

    @Override
    public ProteusView createView(ViewGroup parent, LayoutParser layout, JsonObject data, Styles styles, int index) {
        return new ProteusFixedRatingBar(parent.getContext());
    }

    @Override
    protected void registerAttributeProcessors() {
        super.registerAttributeProcessors();
        addAttributeProcessor(Attributes.RatingBar.NumStars, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setNumStars(ParseHelper.parseInt(attributeValue));
            }
        });
        addAttributeProcessor(Attributes.RatingBar.Rating, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setRating(ParseHelper.parseFloat(attributeValue));
            }
        });
        addAttributeProcessor(Attributes.RatingBar.IsIndicator, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setIsIndicator(ParseHelper.parseBoolean(attributeValue));
            }
        });
        addAttributeProcessor(Attributes.RatingBar.StepSize, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setStepSize(ParseHelper.parseFloat(attributeValue));
            }
        });
        addAttributeProcessor(Attributes.RatingBar.MinHeight, new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, String key, float dimension, LayoutParser parser) {
                view.setMinimumHeight((int) dimension);
            }
        });
        addAttributeProcessor(Attributes.RatingBar.ProgressDrawable, new DrawableResourceProcessor<T>() {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                drawable = view.getTiledDrawable(drawable, false);
                view.setProgressDrawable(drawable);
            }
        });
    }
}
