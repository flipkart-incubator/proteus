package com.flipkart.android.proteus.parser.custom;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.flipkart.android.proteus.parser.Attributes;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.parser.Parser;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.processor.DimensionAttributeProcessor;
import com.flipkart.android.proteus.processor.DrawableResourceProcessor;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.view.LinearLayout;
import com.google.gson.JsonElement;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class LinearLayoutParser<T extends LinearLayout> extends WrappableParser<T> {
    public LinearLayoutParser(Parser<T> wrappedParser) {
        super(LinearLayout.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers() {
        super.prepareHandlers();
        addHandler(Attributes.LinearLayout.Orientation, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                if ("horizontal".equals(attributeValue)) {
                    view.setOrientation(LinearLayout.HORIZONTAL);
                } else {
                    view.setOrientation(LinearLayout.VERTICAL);
                }
            }
        });

        addHandler(Attributes.View.Gravity, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {

                view.setGravity(ParseHelper.parseGravity(attributeValue));

            }
        });

        addHandler(Attributes.LinearLayout.Divider, new DrawableResourceProcessor<T>() {
            @SuppressLint("NewApi")
            @Override
            public void setDrawable(T view, Drawable drawable) {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                    view.setDividerDrawable(drawable);
                }
            }
        });

        addHandler(Attributes.LinearLayout.DividerPadding, new DimensionAttributeProcessor<T>() {
            @SuppressLint("NewApi")
            @Override
            public void setDimension(float dimension, T view, String key, JsonElement value) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                    view.setDividerPadding((int) dimension);
                }
            }
        });

        addHandler(Attributes.LinearLayout.ShowDividers, new StringAttributeProcessor<T>() {
            @SuppressLint("NewApi")
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                    int dividerMode = ParseHelper.parseDividerMode(attributeValue);
                    // noinspection ResourceType
                    view.setShowDividers(dividerMode);
                }
            }
        });

        addHandler(Attributes.LinearLayout.WeightSum, new StringAttributeProcessor<T>() {
            @SuppressLint("NewApi")
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setWeightSum(ParseHelper.parseFloat(attributeValue));
            }
        });
    }
}
