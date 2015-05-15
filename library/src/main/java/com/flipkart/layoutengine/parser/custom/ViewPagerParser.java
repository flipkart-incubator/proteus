package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.view.ProteusView;

import java.util.List;

/**
 * Created by kiran.kumar on 13/05/14.
 */
public class ViewPagerParser<T extends ViewPager> extends WrappableParser<T> {

    public ViewPagerParser(Parser<T> wrappedParser) {
        super(ViewPager.class, wrappedParser);
    }

    @Override
    public void addChildren(ParserContext parserContext, ProteusView<View> parent,
                            final List<ProteusView> children) {

        //not calling super since it calls addChild(). addchild() on viewpager wont work.
        PagerAdapter adapter = parserContext.getLayoutBuilder()
                .getListener()
                .onPagerAdapterRequired(parserContext, parent, children);

        ((T) parent.getView()).setAdapter(adapter);
    }
}
