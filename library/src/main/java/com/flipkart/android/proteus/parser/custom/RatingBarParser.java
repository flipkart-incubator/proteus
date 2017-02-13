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

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import com.flipkart.android.proteus.Layout;
import com.flipkart.android.proteus.ProteusLayoutInflater;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.processor.BooleanAttributeProcessor;
import com.flipkart.android.proteus.processor.DimensionAttributeProcessor;
import com.flipkart.android.proteus.processor.DrawableResourceProcessor;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.toolbox.Attributes;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.view.ProteusFixedRatingBar;
import com.flipkart.android.proteus.view.custom.FixedRatingBar;
import com.google.gson.JsonObject;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class RatingBarParser<T extends FixedRatingBar> extends ViewTypeParser<T> {

    @Override
    public ProteusView createView(ProteusLayoutInflater.Internal inflater, Layout layout, JsonObject data, ViewGroup parent, Styles styles, int index) {
        return new ProteusFixedRatingBar(parent.getContext());
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor(Attributes.RatingBar.NumStars, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                view.setNumStars(ParseHelper.parseInt(value));
            }
        });
        addAttributeProcessor(Attributes.RatingBar.Rating, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                view.setRating(ParseHelper.parseFloat(value));
            }
        });
        addAttributeProcessor(Attributes.RatingBar.IsIndicator, new BooleanAttributeProcessor<T>() {
            @Override
            public void setBoolean(T view, boolean value) {
                view.setIsIndicator(value);
            }
        });
        addAttributeProcessor(Attributes.RatingBar.StepSize, new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                view.setStepSize(ParseHelper.parseFloat(value));
            }
        });
        addAttributeProcessor(Attributes.RatingBar.MinHeight, new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
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
