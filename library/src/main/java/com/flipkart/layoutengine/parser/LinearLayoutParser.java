package com.flipkart.layoutengine.parser;

import android.widget.LinearLayout;

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
    protected void prepareHandlers() {
        super.prepareHandlers();
        addHandler("orientation",new AttributeProcessor<LinearLayout>() {
            @Override
            public void handle(String attributeValue, LinearLayout view) {
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
