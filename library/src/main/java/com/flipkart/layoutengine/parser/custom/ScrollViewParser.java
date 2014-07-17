package com.flipkart.layoutengine.parser.custom;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.ViewParser;
import com.flipkart.layoutengine.parser.WrappableParser;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class ScrollViewParser extends WrappableParser<ScrollView> {
    public ScrollViewParser(Parser<ScrollView> wrappedParser) {
        super(ScrollView.class, wrappedParser);
    }
}
