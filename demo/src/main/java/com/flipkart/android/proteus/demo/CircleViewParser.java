package com.flipkart.android.proteus.demo;

import android.view.ViewGroup;

import com.flipkart.android.proteus.demo.customviews.CircleView;
import com.flipkart.android.proteus.parser.Attributes;
import com.flipkart.android.proteus.parser.Parser;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.view.ProteusView;
import com.google.gson.JsonObject;

/**
 * CircleViewParser
 *
 * @author aditya.sharat
 */

public class CircleViewParser extends WrappableParser<CircleView> {

    public CircleViewParser(Parser<CircleView> wrappedParser) {
        super(wrappedParser);
    }

    @Override
    public ProteusView createView(ViewGroup parent, JsonObject layout, JsonObject data, Styles styles, int index) {
        return new CircleView(parent.getContext());
    }

    @Override
    protected void prepareHandlers() {
        super.prepareHandlers();
        addHandler(new Attributes.Attribute("color"), new StringAttributeProcessor<CircleView>() {
            @Override
            public void handle(String key, String value, CircleView view) {
                view.setColor(value);
            }
        });
    }
}

