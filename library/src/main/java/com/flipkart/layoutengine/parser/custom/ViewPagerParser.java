package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

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
    public void addChildren(Context context, T parent, final List<ProteusView> children) {
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
                ProteusView view = children.get(position);
                container.addView(view.getView());
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
