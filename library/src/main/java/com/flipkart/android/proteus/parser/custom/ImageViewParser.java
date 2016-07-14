package com.flipkart.android.proteus.parser.custom;

import android.graphics.drawable.Drawable;

import com.flipkart.android.proteus.parser.Attributes;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.parser.Parser;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.processor.DrawableResourceProcessor;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.view.ImageView;


/**
 * Created by kiran.kumar on 12/05/14.
 */
public class ImageViewParser<T extends ImageView> extends WrappableParser<T> {

    public ImageViewParser(Parser<T> parentParser) {
        super(ImageView.class, parentParser);
    }

    @Override
    protected void prepareHandlers() {
        super.prepareHandlers();

        addHandler(Attributes.ImageView.Src, new DrawableResourceProcessor<T>() {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                view.setImageDrawable(drawable);
            }
        });

        addHandler(Attributes.ImageView.ScaleType, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                ImageView.ScaleType scaleType = null;
                scaleType = ParseHelper.parseScaleType(attributeValue);
                if (scaleType != null)
                    view.setScaleType(scaleType);
            }
        });

        addHandler(Attributes.ImageView.AdjustViewBounds, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                if ("true".equals(attributeValue)) {
                    view.setAdjustViewBounds(true);
                } else {
                    view.setAdjustViewBounds(false);
                }
            }
        });
    }
}
