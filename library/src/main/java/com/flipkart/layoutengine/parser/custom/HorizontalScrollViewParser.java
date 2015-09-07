package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.widget.HorizontalScrollView;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class HorizontalScrollViewParser<T extends HorizontalScrollView> extends WrappableParser<T> {
    public HorizontalScrollViewParser(Parser<T> parentParser) {
        super(HorizontalScrollView.class, parentParser);
    }

    @Override
    protected void prepareHandlers(Context context) {
        super.prepareHandlers(context);
        addHandler(Attributes.HorizontalScrollView.FillViewPort, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                boolean fillViewPort = ParseHelper.parseBoolean(attributeValue);
                view.setFillViewport(fillViewPort);
            }
        });
        addHandler(Attributes.ScrollView.Scrollbars, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
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