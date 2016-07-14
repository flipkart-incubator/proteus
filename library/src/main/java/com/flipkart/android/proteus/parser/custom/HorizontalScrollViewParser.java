package com.flipkart.android.proteus.parser.custom;

import com.flipkart.android.proteus.parser.Attributes;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.parser.Parser;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.view.HorizontalScrollView;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class HorizontalScrollViewParser<T extends HorizontalScrollView> extends WrappableParser<T> {

    public HorizontalScrollViewParser(Parser<T> parentParser) {
        super(HorizontalScrollView.class, parentParser);
    }

    @Override
    protected void prepareHandlers() {
        super.prepareHandlers();
        addHandler(Attributes.HorizontalScrollView.FillViewPort, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                boolean fillViewPort = ParseHelper.parseBoolean(attributeValue);
                view.setFillViewport(fillViewPort);
            }
        });
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