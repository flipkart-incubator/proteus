package com.flipkart.layoutengine.parser.custom;

import android.app.Activity;
import android.widget.LinearLayout;

import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.ViewParser;
import com.flipkart.layoutengine.parser.WrappableParser;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class LinearLayoutParser extends WrappableParser<LinearLayout> {
    public LinearLayoutParser(Parser<LinearLayout> wrappedParser) {
        super(LinearLayout.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(Activity activity) {
        super.prepareHandlers(activity);
        addHandler("orientation",new AttributeProcessor<LinearLayout>() {
            @Override
            public void handle(String attributeKey, String attributeValue, LinearLayout view) {
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
