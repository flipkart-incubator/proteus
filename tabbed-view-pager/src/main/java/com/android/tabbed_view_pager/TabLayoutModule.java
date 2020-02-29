package com.android.tabbed_view_pager;

import androidx.annotation.NonNull;

import com.android.tabbed_view_pager.adapter.SectionsPagerAdapter;
import com.android.tabbed_view_pager.adapter.ViewPagerAdapterFactory;
import com.android.tabbed_view_pager.widget.TabLayoutParser;
import com.android.tabbed_view_pager.widget.ViewPagerParser;
import com.flipkart.android.proteus.ProteusBuilder;

/**
 * Created by Prasad Rao on 28-02-2020 18:02
 **/
public class TabLayoutModule implements ProteusBuilder.Module {

    private static final String ADAPTER_SIMPLE_LIST = "SectionsPagerAdapter";

    @NonNull
    private ViewPagerAdapterFactory adapterFactory;

    public static TabLayoutModule create() {
        return new Builder().build();
    }

    public TabLayoutModule(@NonNull ViewPagerAdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    @Override
    public void registerWith(ProteusBuilder builder) {
        builder.register(new ViewPagerParser(adapterFactory));
        builder.register(new TabLayoutParser());
    }

    public static class Builder {
        @NonNull
        private final ViewPagerAdapterFactory adapterFactory = new ViewPagerAdapterFactory();

        void register(@NonNull String type, @NonNull SectionsPagerAdapter.Builder builder) {
            adapterFactory.register(type, builder);
        }

        TabLayoutModule build() {
            registerDefaultAdapters();
            return new TabLayoutModule(adapterFactory);
        }

        private void registerDefaultAdapters() {
            register(ADAPTER_SIMPLE_LIST, SectionsPagerAdapter.BUILDER);
        }
    }
}
