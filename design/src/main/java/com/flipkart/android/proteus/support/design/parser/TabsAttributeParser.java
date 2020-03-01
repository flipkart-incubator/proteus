package com.flipkart.android.proteus.support.design.parser;

import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

/**
 * Created by Prasad Rao on 01-03-2020 09:48
 **/
public class TabsAttributeParser {
    private static final String SCROLLABLE = "scrollable";

    public static int getTabMode(@Nullable String value) {
        if (SCROLLABLE.equals(value)) {
            return TabLayout.MODE_SCROLLABLE;
        }
        return TabLayout.MODE_FIXED;
    }
}
