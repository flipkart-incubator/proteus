package com.flipkart.layoutengine.parser.custom;

import android.app.Activity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.ViewParser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.widgets.AspectRatioFrameLayout;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class HorizontalScrollViewParser extends WrappableParser<HorizontalScrollView> {
    public HorizontalScrollViewParser(Parser<HorizontalScrollView> parentParser)
    {
        super(HorizontalScrollView.class,parentParser);
    }

}
