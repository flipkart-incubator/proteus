package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.DrawableResourceProcessor;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;
import com.flipkart.layoutengine.view.ProteusView;
import com.flipkart.layoutengine.widgets.FixedRatingBar;
import com.google.gson.JsonObject;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class RatingBarParser<T extends FixedRatingBar> extends WrappableParser<T> {

    public RatingBarParser(Parser<T> wrappedParser) {
        super(FixedRatingBar.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(final Context context) {
        super.prepareHandlers(context);
        addHandler(Attributes.RatingBar.NumStars, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                view.setNumStars(ParseHelper.parseInt(attributeValue));
            }
        });
        addHandler(Attributes.RatingBar.Rating, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                view.setRating(ParseHelper.parseFloat(attributeValue));
            }
        });
        addHandler(Attributes.RatingBar.IsIndicator, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                view.setIsIndicator(ParseHelper.parseBoolean(attributeValue));
            }
        });
        addHandler(Attributes.RatingBar.StepSize, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                view.setStepSize(ParseHelper.parseFloat(attributeValue));
            }
        });
        addHandler(Attributes.RatingBar.MinHeight, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                view.setMinimumHeight(ParseHelper.parseDimension(attributeValue, context));
            }
        });
        addHandler(Attributes.RatingBar.ProgressDrawable, new DrawableResourceProcessor<T>(context) {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                drawable = view.tileify(drawable, false);
                view.setProgressDrawable(drawable);
            }
        });
    }
}
