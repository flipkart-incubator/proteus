package com.flipkart.android.proteus.parser.custom;


import com.flipkart.android.proteus.parser.Parser;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.view.ViewPager;

/**
 * Created by kiran.kumar on 13/05/14.
 */
public class ViewPagerParser<T extends ViewPager> extends WrappableParser<T> {

    public ViewPagerParser(Parser<T> wrappedParser) {
        super(ViewPager.class, wrappedParser);
    }
}
