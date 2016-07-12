package com.flipkart.proteus.parser.custom;


import com.flipkart.proteus.parser.Parser;
import com.flipkart.proteus.parser.WrappableParser;
import com.flipkart.proteus.view.ViewPager;

/**
 * Created by kiran.kumar on 13/05/14.
 */
public class ViewPagerParser<T extends ViewPager> extends WrappableParser<T> {

    public ViewPagerParser(Parser<T> wrappedParser) {
        super(ViewPager.class, wrappedParser);
    }
}
