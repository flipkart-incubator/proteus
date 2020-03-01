package com.flipkart.android.proteus.support.design.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusLayoutInflater;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.exceptions.ProteusInflateException;
import com.flipkart.android.proteus.support.design.widget.ProteusViewPager;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.flipkart.android.proteus.value.Value;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Prasad Rao on 28-02-2020 18:19
 **/
public class SectionsPagerAdapter extends PagerAdapter {

    private static final String CHILDREN = "children";
    private static final String CHILDREN_COUNT = "children-count";
    private static final String ITEMS = "items";
    private static final String TITLE = "title";

    public interface Builder<A extends SectionsPagerAdapter> {
        @NonNull
        A create(@NonNull ProteusViewPager view, @NonNull ObjectValue config);
    }

    private ProteusLayoutInflater inflater;
    private ObjectValue data;
    private List<Layout> layouts;
    private int count;

    private SectionsPagerAdapter(ProteusLayoutInflater inflater, ObjectValue data, List<Layout> layouts, int count) {
        this.inflater = inflater;
        this.data = data;
        this.layouts = layouts;
        this.count = count;
    }

    @Override
    public int getCount() {
        return count;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ProteusView view = inflater.inflate(layouts.get(position), data, container, position);
        View asView = view.getAsView();
        container.addView(asView);
        return asView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ViewGroup) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }

    private static SectionsPagerAdapter create(@NonNull ProteusViewPager view, @NonNull ObjectValue config) {
        final int count = config.getAsObject().getAsInteger(CHILDREN_COUNT, 0);
        final Value children = config.getAsObject().get(CHILDREN);

        List<Layout> layouts = new ArrayList<>(count);
        if (children.isArray()) {
            Iterator<Value> iterator = children.getAsArray().iterator();
            while (iterator.hasNext()) {
                Value element = iterator.next();
                if (!element.isLayout()) {
                    throw new ProteusInflateException("attribute  'children' must be an array of 'Layout' objects");
                }
                Layout asLayout = element.getAsLayout();
                layouts.add(asLayout);
            }
        }

        ObjectValue data = view.getViewManager().getDataContext().getData();
        ProteusContext context = (ProteusContext) view.getContext();

        return new SectionsPagerAdapter(context.getInflater(), data, layouts, count);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return data.getAsArray(ITEMS).get(position).getAsObject().getAsString(TITLE);
    }

    public static final Builder<SectionsPagerAdapter> BUILDER = SectionsPagerAdapter::create;
}
