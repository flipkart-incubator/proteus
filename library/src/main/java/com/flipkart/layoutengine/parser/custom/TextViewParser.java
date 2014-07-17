package com.flipkart.layoutengine.parser.custom;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.ViewParser;
import com.flipkart.layoutengine.parser.WrappableParser;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class TextViewParser extends WrappableParser<TextView> {

    public TextViewParser(Parser<TextView> wrappedParser) {
        super(TextView.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(Activity activity) {
        super.prepareHandlers(activity);
        addHandler("text", new AttributeProcessor<TextView>() {
            @Override
            public void handle(String attributeKey, String attributeValue, TextView view) {
                view.setText(attributeValue);
            }
        });

        addHandler("textSize",new AttributeProcessor<TextView>() {
            @Override
            public void handle(String attributeKey, String attributeValue, TextView view) {
                view.setTextSize(Float.parseFloat(attributeValue));
            }
        });
        addHandler("textAlign",new AttributeProcessor<TextView>() {
            @Override
            public void handle(String attributeKey, String attributeValue, TextView view) {
                view.setGravity(ParseHelper.parseGravity(attributeValue));
            }
        });

        addHandler("textColor",new AttributeProcessor<TextView>() {
            @Override
            public void handle(String attributeKey, String attributeValue, TextView view) {
                view.setTextColor(Color.parseColor(attributeValue));
            }
        });
    }
}
