package com.flipkart.android.proteus.parser.custom;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.Gravity;

import com.flipkart.android.proteus.parser.Attributes;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.parser.Parser;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.processor.JsonDataProcessor;
import com.flipkart.android.proteus.toolbox.Utils;
import com.flipkart.android.proteus.view.HorizontalProgressBar;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * HorizontalProgressBarParser
 *
 * @author Aditya Sharat
 */
public class HorizontalProgressBarParser<T extends HorizontalProgressBar> extends WrappableParser<T> {

    public HorizontalProgressBarParser(Parser<T> wrappedParser) {
        super(HorizontalProgressBar.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers() {
        super.prepareHandlers();

        addHandler(Attributes.ProgressBar.ProgressTint, new JsonDataProcessor<T>() {
            @Override
            public void handle(String key, JsonElement attributeValue, T view) {
                if (!attributeValue.isJsonObject() || attributeValue.isJsonNull()) {
                    return;
                }
                JsonObject data = attributeValue.getAsJsonObject();
                int background = Color.TRANSPARENT;
                int progress = Color.TRANSPARENT;

                String value = Utils.getPropertyAsString(data, "background");
                if (value != null) {
                    background = ParseHelper.parseColor(value);
                }
                value = Utils.getPropertyAsString(data, "progress");
                if (value != null) {
                    progress = ParseHelper.parseColor(value);
                }

                view.setProgressDrawable(getLayerDrawable(progress, background));
            }
        });
    }

    private Drawable getLayerDrawable(int progress, int background) {
        ShapeDrawable shape = new ShapeDrawable();
        shape.getPaint().setStyle(Paint.Style.FILL);
        shape.getPaint().setColor(background);

        ShapeDrawable shapeD = new ShapeDrawable();
        shapeD.getPaint().setStyle(Paint.Style.FILL);
        shapeD.getPaint().setColor(progress);
        ClipDrawable clipDrawable = new ClipDrawable(shapeD, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        return new LayerDrawable(new Drawable[]{shape, clipDrawable});
    }
}
