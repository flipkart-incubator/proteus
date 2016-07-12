package com.flipkart.proteus.parser.custom;

import android.graphics.drawable.Drawable;

import com.flipkart.proteus.parser.Attributes;
import com.flipkart.proteus.parser.Parser;
import com.flipkart.proteus.parser.WrappableParser;
import com.flipkart.proteus.processor.DrawableResourceProcessor;
import com.flipkart.proteus.processor.StringAttributeProcessor;
import com.flipkart.proteus.view.CheckBox;

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
