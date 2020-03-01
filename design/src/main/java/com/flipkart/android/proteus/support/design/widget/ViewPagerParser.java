package com.flipkart.android.proteus.support.design.widget;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.flipkart.android.proteus.DataContext;
import com.flipkart.android.proteus.ProteusConstants;
import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.managers.AdapterBasedViewManager;
import com.flipkart.android.proteus.processor.AttributeProcessor;
import com.flipkart.android.proteus.support.design.adapter.SectionsPagerAdapter;
import com.flipkart.android.proteus.support.design.adapter.ViewPagerAdapterFactory;
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
public class ViewPagerParser<V extends ViewPager> extends ViewTypeParser<V> {
    private static final String ATTRIBUTE_ADAPTER = "adapter";

    private static final String ATTRIBUTE_TYPE = ProteusConstants.TYPE;

    @NonNull
    private final ViewPagerAdapterFactory adapterFactory;

    public ViewPagerParser(@NonNull ViewPagerAdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    @NonNull
    @Override
    public String getType() {
        return "ViewPager";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "ViewGroup";
    }

    @NonNull
    @Override
    public ProteusView createView(@NonNull ProteusContext context, @NonNull Layout layout, @NonNull ObjectValue data,
        @Nullable ViewGroup parent, int dataIndex) {
        ProteusViewPager proteusViewPager = new ProteusViewPager(context);
        if (parent != null) {
            ((TabLayout) parent.findViewWithTag("tabLayout")).setupWithViewPager(proteusViewPager);
        }
        return proteusViewPager;
    }

    @NonNull
    @Override
    public ProteusView.Manager createViewManager(@NonNull ProteusContext context, @NonNull ProteusView view,
        @NonNull Layout layout, @NonNull ObjectValue data, @Nullable ViewTypeParser caller, @Nullable ViewGroup parent,
        int dataIndex) {
        DataContext dataContext = createDataContext(context, layout, data, parent, dataIndex);
        return new AdapterBasedViewManager(context,
            null != caller ? caller : this,
            view.getAsView(),
            layout,
            dataContext
        );
    }

    @Override
    protected void addAttributeProcessors() {
        addAttributeProcessor(ATTRIBUTE_ADAPTER, new AttributeProcessor<V>() {

            @Override
            public void handleValue(V view, Value value) {
                if (value.isObject()) {
                    String type = value.getAsObject().getAsString(ATTRIBUTE_TYPE);
                    if (type != null) {
                        SectionsPagerAdapter adapter =
                            adapterFactory.create(type, (ProteusViewPager) view, value.getAsObject());
                        view.setAdapter(adapter);
                    }
                }
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

        addAttributeProcessor(ATTRIBUTE_ADAPTER, new AttributeProcessor<V>() {

            @Override
            public void handleValue(V view, Value value) {
                if (value.isObject()) {
                    String type = value.getAsObject().getAsString(ATTRIBUTE_TYPE);
                    if (type != null) {
                        SectionsPagerAdapter adapter =
                            adapterFactory.create(type, (ProteusViewPager) view, value.getAsObject());
                        view.setAdapter(adapter);
                    }
                }
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
