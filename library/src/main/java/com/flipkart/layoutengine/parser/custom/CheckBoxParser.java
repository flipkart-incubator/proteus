package com.flipkart.layoutengine.parser.custom;

import android.graphics.drawable.Drawable;

import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.DrawableResourceProcessor;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;
import com.flipkart.layoutengine.view.CheckBox;

/**
 * Created by prateek.dixit on 1/8/15.
 */
public class CheckBoxParser<T extends CheckBox> extends WrappableParser<T> {

    public CheckBoxParser(Parser<T> wrappedParser) {
        super(CheckBox.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers() {
        super.prepareHandlers();

        addHandler(Attributes.CheckBox.Button, new DrawableResourceProcessor<T>() {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                view.setButtonDrawable(drawable);
            }
        });

        addHandler(Attributes.CheckBox.Checked, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setChecked(Boolean.parseBoolean(attributeValue));
            }
        });
    }
}
