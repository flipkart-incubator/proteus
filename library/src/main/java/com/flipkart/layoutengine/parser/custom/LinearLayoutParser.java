package com.flipkart.layoutengine.parser.custom;

import android.app.Activity;
import android.widget.LinearLayout;

import com.flipkart.layoutengine.parser.ViewParser;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class LinearLayoutParser extends ViewParser<LinearLayout> {
    public LinearLayoutParser(Class viewClass) {
        super(viewClass);
    }

    public LinearLayoutParser()
    {
        super(LinearLayout.class);
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
