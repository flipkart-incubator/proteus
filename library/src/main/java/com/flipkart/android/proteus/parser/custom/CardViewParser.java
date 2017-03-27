package com.flipkart.android.proteus.parser.custom;

/**
 * Created by mac on 3/24/17.
 */

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.parser.Attributes;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.parser.Parser;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.processor.DimensionAttributeProcessor;
import com.flipkart.android.proteus.processor.JsonDataProcessor;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.toolbox.Utils;
import com.flipkart.android.proteus.view.ProteusCardView;
import com.flipkart.android.proteus.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CardViewParser<T extends CardViewParser> extends WrappableParser<View> {

    public CardViewParser(Parser<View> wrappedParser) {
        super(wrappedParser);
    }

    @Override
    public ProteusView createView(ViewGroup parent, JsonObject layout, JsonObject data, Styles styles, int index) {
        return new ProteusCardView(parent.getContext());
    }

    @Override
    protected void prepareHandlers() {
        super.prepareHandlers();
        addHandler(Attributes.CardView.CardBackgroundColor, new JsonDataProcessor<View>() {
            @Override
            public void handle(String key, JsonElement value, View view) {
                int background = Color.TRANSPARENT;
                if (value != null) {
                    background = ParseHelper.parseColor(value.getAsString());
                    ((CardView) view).setCardBackgroundColor(background);
                }
            }
        });
        addHandler(Attributes.CardView.CardCornerRadius, new DimensionAttributeProcessor<View>() {

            @Override
            public void setDimension(float dimension, View view, String key, JsonElement value) {
                ((CardView) view).setRadius(dimension);
            }
        });
        addHandler(Attributes.CardView.CardElevation, new DimensionAttributeProcessor<View>() {

            @Override
            public void setDimension(float dimension, View view, String key, JsonElement value) {
                ((CardView) view).setCardElevation(dimension);
            }
        });
        addHandler(Attributes.CardView.CardContentPadding, new DimensionAttributeProcessor<View>() {

            @Override
            public void setDimension(float dimension, View view, String key, JsonElement value) {
                ((CardView) view).setContentPadding((int) dimension, (int) dimension, (int) dimension, (int) dimension);
            }
        });
        addHandler(Attributes.CardView.CardContentPaddingTop, new DimensionAttributeProcessor<View>() {

            @Override
            public void setDimension(float dimension, View view, String key, JsonElement value) {
                ((CardView) view).setContentPadding(((CardView) view).getContentPaddingLeft(), (int) dimension, ((CardView) view).getContentPaddingRight(), ((CardView) view).getContentPaddingBottom());
            }
        });
        addHandler(Attributes.CardView.CardContentPaddingBottom, new DimensionAttributeProcessor<View>() {

            @Override
            public void setDimension(float dimension, View view, String key, JsonElement value) {
                ((CardView) view).setContentPadding(((CardView) view).getContentPaddingLeft(), ((CardView) view).getContentPaddingTop(), ((CardView) view).getContentPaddingRight(), (int) dimension);
            }
        });
        addHandler(Attributes.CardView.CardContentPaddingRight, new DimensionAttributeProcessor<View>() {

            @Override
            public void setDimension(float dimension, View view, String key, JsonElement value) {
                ((CardView) view).setContentPadding(((CardView) view).getContentPaddingLeft(), ((CardView) view).getContentPaddingTop(), (int) dimension, ((CardView) view).getContentPaddingBottom());
            }
        });
        addHandler(Attributes.CardView.CardContentPaddingLeft, new DimensionAttributeProcessor<View>() {

            @Override
            public void setDimension(float dimension, View view, String key, JsonElement value) {
                ((CardView) view).setContentPadding((int) dimension, ((CardView) view).getContentPaddingTop(), ((CardView) view).getContentPaddingRight(), ((CardView) view).getContentPaddingBottom());
            }
        });
        addHandler(Attributes.CardView.CardMaxElevation, new DimensionAttributeProcessor<View>() {

            @Override
            public void setDimension(float dimension, View view, String key, JsonElement value) {
                ((CardView) view).setMaxCardElevation(dimension);
            }
        });
        addHandler(Attributes.CardView.CardPreventCornerOverlap, new JsonDataProcessor<View>() {

            @Override
            public void handle(String key, JsonElement value, View view) {
                boolean preventCornerOverlap = false;
                if (value != null) {
                    preventCornerOverlap = ParseHelper.parseBoolean(value.getAsString());
                }
                ((CardView) view).setPreventCornerOverlap(preventCornerOverlap);
            }
        });
        addHandler(Attributes.CardView.CardUseCompatPadding, new JsonDataProcessor<View>() {

            @Override
            public void handle(String key, JsonElement value, View view) {
                boolean useCompatPadding = false;
                if (value != null) {
                    useCompatPadding = ParseHelper.parseBoolean(value.getAsString());
                }
                ((CardView) view).setUseCompatPadding(useCompatPadding);
            }
        });
    }
}
