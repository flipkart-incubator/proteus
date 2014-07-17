package com.flipkart.layoutengine.parser.custom;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.ViewParser;
import com.flipkart.layoutengine.parser.WrappableParser;

import java.util.List;

/**
 * Created by kiran.kumar on 13/05/14.
 */
public class ViewPagerParser extends WrappableParser<ViewPager> {

    public ViewPagerParser(Parser<ViewPager> wrappedParser) {
        super(ViewPager.class, wrappedParser);
    }

    @Override
    public void addChildren(Activity activity, ViewPager parent, final List<View> children) {
        //not calling super since it calls addChild(). Addchild() on viewpager wont work.

        PagerAdapter adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return children.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = children.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View)object);
            }
        };
        parent.setAdapter(adapter);


    }
}
