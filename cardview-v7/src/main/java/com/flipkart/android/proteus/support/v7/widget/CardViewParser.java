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

package com.flipkart.android.proteus.support.v7.widget;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;

import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.processor.BooleanAttributeProcessor;
import com.flipkart.android.proteus.processor.ColorResourceProcessor;
import com.flipkart.android.proteus.processor.DimensionAttributeProcessor;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;

/**
 * CardViewParser
 *
 * @author adityasharat
 */

public class CardViewParser<T extends CardView> extends ViewTypeParser<T> {

    @NonNull
    @Override
    public String getType() {
        return "CardView";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "FrameLayout";
    }

    @NonNull
    @Override
    public ProteusView createView(@NonNull ProteusContext context, @NonNull Layout layout, @NonNull ObjectValue data, @Nullable ViewGroup parent, int dataIndex) {
        return new ProteusCardView(context);
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor("cardBackgroundColor", new ColorResourceProcessor<T>() {
            @Override
            public void setColor(T view, int color) {
                view.setCardBackgroundColor(color);
            }

            @Override
            public void setColor(T view, ColorStateList colors) {
                view.setCardBackgroundColor(colors);
            }
        });

        addAttributeProcessor("cardCornerRadius", new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                view.setRadius(dimension);
            }
        });

        addAttributeProcessor("cardElevation", new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                view.setCardElevation(dimension);
            }
        });

        addAttributeProcessor("cardMaxElevation", new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                view.setMaxCardElevation(dimension);
            }
        });

        addAttributeProcessor("cardPreventCornerOverlap", new BooleanAttributeProcessor<T>() {
            @Override
            public void setBoolean(T view, boolean value) {
                view.setPreventCornerOverlap(value);
            }
        });

        addAttributeProcessor("cardUseCompatPadding", new BooleanAttributeProcessor<T>() {
            @Override
            public void setBoolean(T view, boolean value) {
                view.setUseCompatPadding(value);
            }
        });

        addAttributeProcessor("contentPadding", new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                view.setContentPadding((int) dimension, (int) dimension, (int) dimension, (int) dimension);
            }
        });

        addAttributeProcessor("contentPaddingBottom", new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                int t = view.getContentPaddingTop();
                int r = view.getContentPaddingRight();
                int l = view.getContentPaddingLeft();

                view.setContentPadding(l, t, r, (int) dimension);
            }
        });

        addAttributeProcessor("contentPaddingLeft", new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                int t = view.getContentPaddingTop();
                int r = view.getContentPaddingRight();
                int b = view.getContentPaddingBottom();

                view.setContentPadding((int) dimension, t, r, b);
            }
        });

        addAttributeProcessor("contentPaddingRight", new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                int t = view.getContentPaddingTop();
                int b = view.getContentPaddingBottom();
                int l = view.getContentPaddingLeft();

                view.setContentPadding(l, t, (int) dimension, b);
            }
        });

        addAttributeProcessor("contentPaddingTop", new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                int r = view.getContentPaddingRight();
                int b = view.getContentPaddingBottom();
                int l = view.getContentPaddingLeft();

                view.setContentPadding(l, (int) dimension, r, b);
            }
        });

    }
}
