package com.flipkart.layoutengine.parser.custom;

import android.app.Activity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import com.flipkart.layoutengine.parser.ViewParser;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class HorizontalScrollViewParser extends ViewParser<HorizontalScrollView> {
    public HorizontalScrollViewParser(Class<View> viewClass) {
        super(viewClass);
    }

    public HorizontalScrollViewParser() {
        super(HorizontalScrollView.class);
    }

}
