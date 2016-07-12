package com.flipkart.proteus.parser.custom;


import com.flipkart.proteus.parser.Attributes;
import com.flipkart.proteus.parser.ParseHelper;
import com.flipkart.proteus.parser.Parser;
import com.flipkart.proteus.parser.WrappableParser;
import com.flipkart.proteus.processor.StringAttributeProcessor;
import com.flipkart.proteus.view.RelativeLayout;

/**
 * Created by kirankumar on 10/07/14.
 */
public class RelativeLayoutParser<T extends RelativeLayout> extends WrappableParser<T> {

    public RelativeLayoutParser(Parser<T> parentParser) {
        super(RelativeLayout.class, parentParser);
    }

    @Override
    protected void prepareHandlers() {
        super.prepareHandlers();
        addHandler(Attributes.View.Gravity, new StringAttributeProcessor<T>() {

            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setGravity(ParseHelper.parseGravity(attributeValue));
            }
        });
    }
}
