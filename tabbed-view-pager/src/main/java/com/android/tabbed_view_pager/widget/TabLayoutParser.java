package com.android.tabbed_view_pager.widget;

import android.graphics.Color;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.processor.AttributeProcessor;
import com.flipkart.android.proteus.value.AttributeResource;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.flipkart.android.proteus.value.Resource;
import com.flipkart.android.proteus.value.StyleResource;
import com.flipkart.android.proteus.value.Value;
import com.google.android.material.tabs.TabLayout;

/**
 * Created by Prasad Rao on 28-02-2020 18:18
 **/
public class TabLayoutParser<V extends TabLayout> extends ViewTypeParser<V> {

    @NonNull
    @Override
    public String getType() {
        return "TabLayout";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "HorizontalScrollView";
    }

    @NonNull
    @Override
    public ProteusView createView(@NonNull ProteusContext context, @NonNull Layout layout, @NonNull ObjectValue data,
        @Nullable ViewGroup parent, int dataIndex) {
        ProteusTabLayout proteusTabLayout = new ProteusTabLayout(context);
        proteusTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        proteusTabLayout.setTabTextColors(Color.BLACK, Color.BLACK);
        //        proteusTabLayout.addTab(proteusTabLayout.newTab().setText("Tab 1"));
        //        proteusTabLayout.addTab(proteusTabLayout.newTab().setText("Tab 2"));
        return proteusTabLayout;
    }

    @Override
    protected void addAttributeProcessors() {
        addAttributeProcessor("view_page_id", new AttributeProcessor<V>() {

            @Override
            public void handleValue(V view, Value value) {

                //                ProteusView.Manager viewManager = ((ProteusView) view.getRootView()).getViewManager();
                //                View viewPager = viewManager.findViewById(value.getAsString());
                //                view.setupWithViewPager((ViewPager) viewPager);
                //                view.setupWithViewPager(view.getParent());
                //                if (value.isObject()) {
                //                    String type = value.getAsObject().getAsString(ATTRIBUTE_TYPE);
                //                    if (type != null) {
                //                        SectionsPagerAdapter adapter =
                //                            adapterFactory.create(type, (ProteusViewPager) view, value.getAsObject());
                //                        view.setAdapter(adapter);
                //                    }
                //                }
            }

            @Override
            public void handleResource(V view, Resource resource) {
                throw new IllegalArgumentException("View pager 'adapter' expects only object values");
            }

            @Override
            public void handleAttributeResource(V view, AttributeResource attribute) {
                throw new IllegalArgumentException("View pager 'adapter' expects only object values");
            }

            @Override
            public void handleStyleResource(V view, StyleResource style) {
                throw new IllegalArgumentException("View pager 'adapter' expects only object values");
            }
        });
    }
}
