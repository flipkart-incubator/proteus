package com.flipkart.layoutengine.parser.custom;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Created by kiran.kumar on 13/05/14.
 */
public class ViewPagerParser<T extends ViewPager> extends WrappableParser<T> {

    public ViewPagerParser(Parser<T> wrappedParser) {
        super(ViewPager.class, wrappedParser);
    }

    @Override
    public void addChildren(ParserContext parserContext, ProteusView parent,
                            final List<ProteusView> children, JsonObject viewLayout) {

        // not calling super since it calls addView(). addView() on viewpager wont work.
        PagerAdapter adapter = parserContext.getLayoutBuilder()
                .getListener()
                .onPagerAdapterRequired(parserContext, parent, children, viewLayout);

        ((T) parent.getView()).setAdapter(adapter);
    }
}
