package com.flipkart.layoutengine.parser.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.LinearLayout;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.ResourceReferenceProcessor;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;
import com.google.gson.JsonObject;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class LinearLayoutParser<T extends LinearLayout> extends WrappableParser<T> {
    public LinearLayoutParser(Parser<T> wrappedParser) {
        super(LinearLayout.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(Context context) {
        super.prepareHandlers(context);
        addHandler(Attributes.LinearLayout.Orientation, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, JsonObject layout) {
                if ("horizontal".equals(attributeValue)) {
                    view.setOrientation(LinearLayout.HORIZONTAL);
                } else {
                    view.setOrientation(LinearLayout.VERTICAL);
                }
            }
        });

        addHandler(Attributes.View.Gravity, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, JsonObject layout) {

                view.setGravity(ParseHelper.parseGravity(attributeValue));

            }
        });

        addHandler(Attributes.LinearLayout.Divider, new ResourceReferenceProcessor<T>(context) {
            @SuppressLint("NewApi")
            @Override
            public void setDrawable(T view, Drawable drawable) {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                    view.setDividerDrawable(drawable);
                }
            }
        });

        addHandler(Attributes.LinearLayout.DividerPadding, new StringAttributeProcessor<T>() {
            @SuppressLint("NewApi")
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, JsonObject layout) {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                    view.setDividerPadding(ParseHelper.parseInt(attributeValue));
                }
            }
        });

        addHandler(Attributes.LinearLayout.ShowDividers, new StringAttributeProcessor<T>() {
            @SuppressLint("NewApi")
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, JsonObject layout) {

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
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, JsonObject layout) {
                view.setWeightSum(ParseHelper.parseFloat(attributeValue));
            }
        });
    }
}
