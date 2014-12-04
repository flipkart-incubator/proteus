package com.flipkart.layoutengine.parser.custom;

import android.content.Context;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;
import com.flipkart.layoutengine.widgets.AspectRatioFrameLayout;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class FrameLayoutParser<T extends AspectRatioFrameLayout> extends WrappableParser<T>{

    public FrameLayoutParser(Parser<T> parentParser)
    {
        super(AspectRatioFrameLayout.class,parentParser);
    }

    @Override
    protected void prepareHandlers(Context context) {
        super.prepareHandlers(context);
        addHandler(Attributes.FrameLayout.HeightRatio,new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setAspectRatioHeight(Integer.parseInt(attributeValue));

            }
        });
        addHandler(Attributes.FrameLayout.WidthRatio,new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setAspectRatioWidth(Integer.parseInt(attributeValue));

            }
        });
    }
}
