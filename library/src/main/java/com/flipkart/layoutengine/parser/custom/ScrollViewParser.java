package com.flipkart.layoutengine.parser.custom;

import android.widget.ScrollView;

import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class ScrollViewParser<T extends ScrollView> extends WrappableParser<T> {
    public ScrollViewParser(Parser<T> wrappedParser) {
        super(ScrollView.class, wrappedParser);
    }
}
