package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.widget.TextView;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.AttributeProcessor;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class TextViewParser extends WrappableParser<TextView> {

    public TextViewParser(Parser<TextView> wrappedParser) {
        super(TextView.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(Context context) {
        super.prepareHandlers(context);
        addHandler(Attributes.TextView.Text, new AttributeProcessor<TextView>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, TextView view) {
                view.setText(attributeValue);
            }
        });

        addHandler(Attributes.TextView.TextSize,new AttributeProcessor<TextView>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, TextView view) {
                view.setTextSize(ParseHelper.parseDimension(attributeValue));
            }
        });
        addHandler(Attributes.TextView.Gravity,new AttributeProcessor<TextView>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, TextView view) {
                view.setGravity(ParseHelper.parseGravity(attributeValue));
            }
        });

        addHandler(Attributes.TextView.TextColor,new AttributeProcessor<TextView>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, TextView view) {
                view.setTextColor(ParseHelper.parseColor(attributeValue));
            }
        });



    }
}
