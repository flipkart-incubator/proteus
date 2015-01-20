package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.CheckBox;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.ResourceReferenceProcessor;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;

/**
 * Created by prateek.dixit on 1/8/15.
 */
public class CheckBoxParser <T extends CheckBox> extends WrappableParser<T> {

    public CheckBoxParser(Parser<T> wrappedParser) {
        super(CheckBox.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(Context context) {
        super.prepareHandlers(context);
        addHandler(Attributes.CheckBox.Text, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setText(attributeValue);
            }
        });

        addHandler(Attributes.TextView.TextColor, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setTextColor(ParseHelper.parseColor(attributeValue));
            }
        });

        addHandler(Attributes.CheckBox.Button , new ResourceReferenceProcessor<T>(context) {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                view.setButtonDrawable(drawable);
            }
        });

        addHandler(Attributes.CheckBox.Checked, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setChecked(Boolean.parseBoolean(attributeValue));
            }
        });
    }
}
