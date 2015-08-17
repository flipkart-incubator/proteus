package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.graphics.PorterDuff;
import android.widget.ProgressBar;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;

/**
 * @author Aditya Sharat
 */
public class ProgressBarParser<T extends ProgressBar> extends WrappableParser<T> {

    public ProgressBarParser(Parser<T> wrappedParser) {
        super(ProgressBar.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(Context context) {
        super.prepareHandlers(context);
        addHandler(Attributes.ProgressBar.Max, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setMax((int) Double.parseDouble(attributeValue));
            }
        });
        addHandler(Attributes.ProgressBar.Progress, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setProgress((int) Double.parseDouble(attributeValue));
            }
        });
        addHandler(Attributes.ProgressBar.ProgressTint, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.getProgressDrawable().setColorFilter(ParseHelper.parseColor(attributeValue), PorterDuff.Mode.SRC_IN);
            }
        });
    }
}
