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

package com.flipkart.android.proteus.support.design.widget;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.ViewGroup;

import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.processor.BooleanAttributeProcessor;
import com.flipkart.android.proteus.processor.DimensionAttributeProcessor;
import com.flipkart.android.proteus.processor.DrawableResourceProcessor;
import com.flipkart.android.proteus.processor.GravityAttributeProcessor;
import com.flipkart.android.proteus.processor.NumberAttributeProcessor;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;

/**
 * CollapsingToolbarLayoutParser
 *
 * @author adityasharat
 */

public class CollapsingToolbarLayoutParser<V extends CollapsingToolbarLayout> extends ViewTypeParser<V> {

    @NonNull
    @Override
    public String getType() {
        return "CollapsingToolbarLayout";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "FrameLayout";
    }

    @NonNull
    @Override
    public ProteusView createView(@NonNull ProteusContext context, @NonNull Layout layout, @NonNull ObjectValue data, @Nullable ViewGroup parent, int dataIndex) {
        return new ProteusCollapsingToolbarLayout(context);
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor("collapsedTitleGravity", new GravityAttributeProcessor<V>() {
            @Override
            public void setGravity(V view, @Gravity int gravity) {
                view.setCollapsedTitleGravity(gravity);
            }
        });

        addAttributeProcessor("contentScrim", new DrawableResourceProcessor<V>() {
            @Override
            public void setDrawable(V view, Drawable drawable) {
                view.setContentScrim(drawable);
            }
        });

        addAttributeProcessor("expandedTitleGravity", new GravityAttributeProcessor<V>() {
            @Override
            public void setGravity(V view, @Gravity int gravity) {
                view.setExpandedTitleGravity(gravity);
            }
        });

        addAttributeProcessor("expandedTitleMargin", new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setExpandedTitleMargin((int) dimension, (int) dimension, (int) dimension, (int) dimension);
            }
        });

        addAttributeProcessor("expandedTitleMarginBottom", new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setExpandedTitleMarginBottom((int) dimension);
            }
        });

        addAttributeProcessor("expandedTitleMarginEnd", new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setExpandedTitleMarginEnd((int) dimension);
            }
        });

        addAttributeProcessor("expandedTitleMarginStart", new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setExpandedTitleMarginStart((int) dimension);
            }
        });

        addAttributeProcessor("expandedTitleMarginTop", new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setExpandedTitleMarginTop((int) dimension);
            }
        });

        addAttributeProcessor("scrimAnimationDuration", new NumberAttributeProcessor<V>() {
            @Override
            public void setNumber(V view, @NonNull Number value) {
                view.setScrimAnimationDuration(value.longValue());
            }
        });


        addAttributeProcessor("scrimVisibleHeightTrigger", new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setScrimVisibleHeightTrigger((int) dimension);
            }
        });

        addAttributeProcessor("statusBarScrim", new DrawableResourceProcessor<V>() {
            @Override
            public void setDrawable(V view, Drawable drawable) {
                view.setStatusBarScrim(drawable);
            }
        });

        addAttributeProcessor("title", new StringAttributeProcessor<V>() {
            @Override
            public void setString(V view, String value) {
                view.setTitle(value);
            }
        });

        addAttributeProcessor("titleEnabled", new BooleanAttributeProcessor<V>() {
            @Override
            public void setBoolean(V view, boolean value) {
                view.setTitleEnabled(value);
            }
        });
    }
}
