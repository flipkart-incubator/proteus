package com.flipkart.proteus.parser.custom;

import com.flipkart.proteus.parser.Attributes;
import com.flipkart.proteus.parser.ParseHelper;
import com.flipkart.proteus.parser.Parser;
import com.flipkart.proteus.parser.WrappableParser;
import com.flipkart.proteus.processor.StringAttributeProcessor;
import com.flipkart.proteus.view.AspectRatioFrameLayout;


/**
 * Created by kiran.kumar on 12/05/14.
 */
public class FrameLayoutParser<T extends AspectRatioFrameLayout> extends WrappableParser<T> {

    public FrameLayoutParser(Parser<T> parentParser) {
        super(AspectRatioFrameLayout.class, parentParser);
    }

    @Override
    protected void prepareHandlers() {
        super.prepareHandlers();
        addHandler(Attributes.FrameLayout.HeightRatio, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setAspectRatioHeight(ParseHelper.parseInt(attributeValue));

            }
        });
        addHandler(Attributes.FrameLayout.WidthRatio, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setAspectRatioWidth(ParseHelper.parseInt(attributeValue));

            }
        });
    }
}
