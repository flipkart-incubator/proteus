package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.widget.LinearLayout;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.AttributeProcessor;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class LinearLayoutParser extends WrappableParser<LinearLayout> {
    public LinearLayoutParser(Parser<LinearLayout> wrappedParser) {
        super(LinearLayout.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(Context context) {
        super.prepareHandlers(context);
        addHandler(Attributes.LinearLayout.Orientation,new AttributeProcessor<LinearLayout>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, LinearLayout view) {
                if("horizontal".equals(attributeValue)) {
                    view.setOrientation(LinearLayout.HORIZONTAL);
                }
                else
                {
                    view.setOrientation(LinearLayout.VERTICAL);
                }
            }
        });
    }
}
