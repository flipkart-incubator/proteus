package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.ResourceReferenceProcessor;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonObject;


/**
 * Created by kiran.kumar on 12/05/14.
 */
public class ImageViewParser<T extends ImageView> extends WrappableParser<T> {
    private static final String TAG = ImageViewParser.class.getSimpleName();

    public ImageViewParser(Parser<T> parentParser) {
        super(ImageView.class, parentParser);
    }

    @Override
    protected void prepareHandlers(final Context context) {
        super.prepareHandlers(context);

        addHandler(Attributes.ImageView.Src, new ResourceReferenceProcessor<T>(context) {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                view.setImageDrawable(drawable);
            }
        });

        addHandler(Attributes.ImageView.ScaleType, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                ImageView.ScaleType scaleType = null;
                scaleType = ParseHelper.parseScaleType(attributeValue);
                if (scaleType != null)
                    view.setScaleType(scaleType);
            }
        });

        addHandler(Attributes.ImageView.AdjustViewBounds, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                if ("true".equals(attributeValue)) {
                    view.setAdjustViewBounds(true);
                } else {
                    view.setAdjustViewBounds(false);
                }
            }
        });
    }
}
