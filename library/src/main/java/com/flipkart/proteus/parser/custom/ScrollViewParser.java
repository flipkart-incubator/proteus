package com.flipkart.proteus.parser.custom;


import com.flipkart.proteus.parser.Attributes;
import com.flipkart.proteus.parser.Parser;
import com.flipkart.proteus.parser.WrappableParser;
import com.flipkart.proteus.processor.StringAttributeProcessor;
import com.flipkart.proteus.view.ScrollView;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class ScrollViewParser<T extends ScrollView> extends WrappableParser<T> {
    public ScrollViewParser(Parser<T> wrappedParser) {
        super(ScrollView.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers() {
        super.prepareHandlers();
        addHandler(Attributes.ScrollView.Scrollbars, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                if ("none".equals(attributeValue)) {
                    view.setHorizontalScrollBarEnabled(false);
                    view.setVerticalScrollBarEnabled(false);
                } else if ("horizontal".equals(attributeValue)) {
                    view.setHorizontalScrollBarEnabled(true);
                    view.setVerticalScrollBarEnabled(false);
                } else if ("vertical".equals(attributeValue)) {
                    view.setHorizontalScrollBarEnabled(false);
                    view.setVerticalScrollBarEnabled(true);
                } else {
                    view.setHorizontalScrollBarEnabled(false);
                    view.setVerticalScrollBarEnabled(false);
                }
            }
        });
    }
}
