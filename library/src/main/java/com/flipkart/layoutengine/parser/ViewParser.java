package com.flipkart.layoutengine.parser;

import android.graphics.Color;
import android.view.View;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class ViewParser<T extends View> extends BaseParser<T> {


    public ViewParser(Class viewClass)
    {
        super(viewClass);
        prepareHandlers();
    }

    protected void prepareHandlers() {
        addHandler("backgroundColor",new AttributeProcessor<View>() {
            @Override
            public void handle(String attributeValue, View view) {
                view.setBackgroundColor(Color.parseColor(attributeValue));
            }
        });
    }
}
