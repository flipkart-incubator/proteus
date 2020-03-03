package com.flipkart.android.proteus.support.design.widget;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.processor.ColorResourceProcessor;
import com.flipkart.android.proteus.processor.DimensionAttributeProcessor;
import com.flipkart.android.proteus.processor.DrawableResourceProcessor;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.support.design.parser.TabsAttributeParser;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.google.android.material.tabs.TabLayout;

/**
 * Created by Prasad Rao on 28-02-2020 18:18
 **/
public class TabLayoutParser<V extends TabLayout> extends ViewTypeParser<V> {

    @NonNull
    @Override
    public String getType() {
        return "TabLayout";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "HorizontalScrollView";
    }

    @NonNull
    @Override
    public ProteusView createView(@NonNull ProteusContext context, @NonNull Layout layout, @NonNull ObjectValue data,
        @Nullable ViewGroup parent, int dataIndex) {
        return new ProteusTabLayout(context);
    }

    @Override
    protected void addAttributeProcessors() {
        addAttributeProcessor("tabPadding", new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setPaddingRelative((int) dimension, (int) dimension, (int) dimension, (int) dimension);
            }
        });

        addAttributeProcessor("tabBackground", new DrawableResourceProcessor<V>() {
            @Override
            public void setDrawable(V view, Drawable drawable) {

            }
        });

        addAttributeProcessor("tabTextColors", new ColorResourceProcessor<V>() {
            @Override
            public void setColor(V view, int color) {
                throw new IllegalArgumentException("itemIconTint must be a color state list");
            }

            @Override
            public void setColor(V view, ColorStateList colors) {
                view.setTabTextColors(colors);
            }
        });

        addAttributeProcessor("tabMode", new StringAttributeProcessor<V>() {
            @Override
            public void setString(V view, String value) {
                view.setTabMode(TabsAttributeParser.getTabMode(value));
            }
        });

        addAttributeProcessor("backgroundColor", new ColorResourceProcessor<V>() {
            @Override
            public void setColor(V view, int color) {
                view.setBackgroundColor(color);
            }

            @Override
            public void setColor(V view, ColorStateList colors) {
                view.setBackgroundColor(colors.getDefaultColor());
            }
        });

        addAttributeProcessor("selectedTabIndicatorColor", new ColorResourceProcessor<V>() {
            @Override
            public void setColor(V view, int color) {
                view.setSelectedTabIndicatorColor(color);
            }

            @Override
            public void setColor(V view, ColorStateList colors) {
                view.setSelectedTabIndicatorColor(colors.getDefaultColor());
            }
        });

    }
}
