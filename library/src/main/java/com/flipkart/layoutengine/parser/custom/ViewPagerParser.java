package com.flipkart.layoutengine.parser.custom;


import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.view.ViewPager;

/**
 * Created by kiran.kumar on 13/05/14.
 */
public class ViewPagerParser<T extends ViewPager> extends WrappableParser<T> {

    public ViewPagerParser(Parser<T> wrappedParser) {
        super(ViewPager.class, wrappedParser);
    }
}
