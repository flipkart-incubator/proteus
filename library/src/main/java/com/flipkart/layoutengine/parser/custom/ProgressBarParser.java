package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.Gravity;
import android.widget.ProgressBar;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.builder.LayoutBuilder;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.JsonDataProcessor;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;
import com.flipkart.layoutengine.toolbox.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Aditya Sharat
 */
public class ProgressBarParser<T extends ProgressBar> extends WrappableParser<T> {

    public ProgressBarParser(Parser<T> wrappedParser) {
        super(ProgressBar.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(final Context context) {
        super.prepareHandlers(context);
        addHandler(Attributes.ProgressBar.Max, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setMax((int) ParseHelper.parseDouble(attributeValue));
            }
        });
        addHandler(Attributes.ProgressBar.Progress, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setProgress((int) ParseHelper.parseDouble(attributeValue));
            }
        });
        addHandler(Attributes.ProgressBar.ProgressTint, new JsonDataProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, LayoutBuilder layoutBuilder, String attributeKey, JsonElement attributeValue, T view, JsonObject layout) {
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
