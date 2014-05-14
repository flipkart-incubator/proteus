package com.flipkart.layoutengine.parser.custom;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.flipkart.layoutengine.parser.ViewParser;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class TextViewParser extends ViewParser<TextView> {
    public TextViewParser(Class<View> viewClass) {
        super(viewClass);
    }

    public TextViewParser() {
        super(TextView.class);
    }

    @Override
    protected void prepareHandlers(Activity activity) {
        super.prepareHandlers(activity);
        addHandler("text", new AttributeProcessor<TextView>() {
            @Override
            public void handle(String attributeValue, TextView view) {
                view.setText(attributeValue);
            }
        });
    }
}
