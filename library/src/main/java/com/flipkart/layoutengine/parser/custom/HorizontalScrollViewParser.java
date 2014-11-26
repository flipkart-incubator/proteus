package com.flipkart.layoutengine.parser.custom;

import android.widget.HorizontalScrollView;

import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class HorizontalScrollViewParser<T extends HorizontalScrollView> extends WrappableParser<T> {
    public HorizontalScrollViewParser(Parser<T> parentParser)
    {
        super(HorizontalScrollView.class,parentParser);
    }

}
