package com.android.tabbed_view_pager.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.android.tabbed_view_pager.widget.ProteusViewPager;
import com.flipkart.android.proteus.DataContext;
import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusLayoutInflater;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.flipkart.android.proteus.value.Value;

import java.util.Map;

/**
 * Created by Prasad Rao on 28-02-2020 18:19
 **/
public class SectionsPagerAdapter extends PagerAdapter {

    private static final String ATTRIBUTE_ITEM_LAYOUT = "item-layout";
    private static final String ATTRIBUTE_ITEM_COUNT = "item-count";

    public interface Builder<A extends SectionsPagerAdapter> {
        @NonNull
        A create(@NonNull ProteusViewPager view, @NonNull ObjectValue config);
    }

    private ProteusLayoutInflater inflater;
    private ObjectValue data;
    private Layout layout;
    private Map<String, Value> scope;
    private int count;

    private SectionsPagerAdapter(ProteusLayoutInflater inflater, ObjectValue data, Layout layout, int count) {
        this.inflater = inflater;
        this.data = data;
        this.layout = new Layout(layout.type, layout.attributes, null, layout.extras);
        this.scope = layout.data;
        this.count = count;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "Tab " + position;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ProteusView view = inflater.inflate(layout, new ObjectValue());
        DataContext context = DataContext.create(view.getViewManager().getContext(), data, position, scope);
        view.getViewManager().update(context.getData());
        View asView = view.getAsView();
        container.addView(asView);
        return asView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ViewGroup) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }

    public static SectionsPagerAdapter create(@NonNull ProteusViewPager view, @NonNull ObjectValue config) {
        Layout layout = config.getAsObject().getAsLayout(ATTRIBUTE_ITEM_LAYOUT);
        Integer count = config.getAsObject().getAsInteger(ATTRIBUTE_ITEM_COUNT);
        ObjectValue data = view.getViewManager().getDataContext().getData();
        ProteusContext context = (ProteusContext) view.getContext();

        return new SectionsPagerAdapter(context.getInflater(), data, layout, count != null ? count : 0);
    }

    public static final Builder<SectionsPagerAdapter> BUILDER = SectionsPagerAdapter::create;

    //
    //        public interface Builder<A extends SectionsPagerAdapter> {
    //            @NonNull
    //            A create(@NonNull ProteusViewPager view, @NonNull ObjectValue config);
    //        }
    //
    //        private ProteusLayoutInflater inflater;
    //        private ObjectValue data;
    //        private Layout layout;
    //        private Map<String, Value> scope;
    //        private ProteusContext context;
    //        private int count;
    //
    //        private SectionsPagerAdapter(ProteusLayoutInflater inflater, ObjectValue data, Layout layout,
    //            ProteusContext context, int count) {
    //            super(((AppCompatActivity) context.getBaseContext()).getSupportFragmentManager(),
    //                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    //            );
    //            this.inflater = inflater;
    //            this.data = data;
    //            this.layout = new Layout(layout.type, layout.attributes, null, layout.extras);
    //            this.scope = layout.data;
    //            this.context = context;
    //            this.count = count;
    //        }
    //
    //        @NonNull
    //        @Override
    //        public Fragment getItem(int position) {
    //            ProteusView view = inflater.inflate(layout, new ObjectValue());
    //            DataContext context = DataContext.create(this.context, data, position, scope);
    //            view.getViewManager().update(context.getData());
    //            return PlaceHolderFragment.newInstance(view);
    //        }
    //
    //        @Override
    //        public int getCount() {
    //            return count;
    //        }
    //
    //        @Override
    //        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
    //            return false;
    //        }
    //
    //        public static SectionsPagerAdapter create(@NonNull ProteusViewPager view, @NonNull ObjectValue config) {
    //            Layout layout = config.getAsObject().getAsLayout(ATTRIBUTE_ITEM_LAYOUT);
    //            Integer count = config.getAsObject().getAsInteger(ATTRIBUTE_ITEM_COUNT);
    //            ObjectValue data = view.getViewManager().getDataContext().getData();
    //            ProteusContext context = (ProteusContext) view.getContext();
    //
    //            return new SectionsPagerAdapter(context.getInflater(), data, layout, context, count != null ? count
    //            : 0);
    //        }
    //
    //        public static final Builder<SectionsPagerAdapter> BUILDER = SectionsPagerAdapter::create;
}
