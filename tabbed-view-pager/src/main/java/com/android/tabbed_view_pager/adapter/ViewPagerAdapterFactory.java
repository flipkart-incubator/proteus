package com.android.tabbed_view_pager.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.tabbed_view_pager.widget.ProteusViewPager;
import com.flipkart.android.proteus.value.ObjectValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Prasad Rao on 28-02-2020 19:50
 **/
public class ViewPagerAdapterFactory {

    private Map<String, SectionsPagerAdapter.Builder> adapters = new HashMap<>();

    public void register(@NonNull String type, @NonNull SectionsPagerAdapter.Builder builder) {
        adapters.put(type, builder);
    }

    @Nullable
    public SectionsPagerAdapter.Builder remove(@NonNull String type) {
        return adapters.remove(type);
    }

    public SectionsPagerAdapter create(@NonNull String type, @NonNull ProteusViewPager view,
        @NonNull ObjectValue config) {
        return adapters.get(type).create(view, config);
    }
}
