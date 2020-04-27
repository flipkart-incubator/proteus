/*
 * Copyright 2019 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.android.proteus.support.design;

import androidx.annotation.NonNull;

import com.flipkart.android.proteus.ProteusBuilder;
import com.flipkart.android.proteus.support.design.adapter.SectionsPagerAdapter;
import com.flipkart.android.proteus.support.design.adapter.ViewPagerAdapterFactory;
import com.flipkart.android.proteus.support.design.parser.AppCompatEditTextParser;
import com.flipkart.android.proteus.support.design.parser.TextInputEditTextParser;
import com.flipkart.android.proteus.support.design.parser.TextInputLayoutParser;
import com.flipkart.android.proteus.support.design.widget.AppBarLayoutParser;
import com.flipkart.android.proteus.support.design.widget.BottomNavigationViewParser;
import com.flipkart.android.proteus.support.design.widget.CollapsingToolbarLayoutParser;
import com.flipkart.android.proteus.support.design.widget.CoordinatorLayoutParser;
import com.flipkart.android.proteus.support.design.widget.FloatingActionButtonParser;
import com.flipkart.android.proteus.support.design.widget.TabLayoutParser;
import com.flipkart.android.proteus.support.design.widget.ViewPagerParser;

/**
 * DesignModule
 *
 * @author adityasharat
 */

public class DesignModule implements ProteusBuilder.Module {

    private static final String ADAPTER_SIMPLE_LIST = "SectionsPagerAdapter";

    @NonNull private ViewPagerAdapterFactory adapterFactory;

    private DesignModule(@NonNull ViewPagerAdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    public static DesignModule create() {
        return new Builder().build();
    }

    @Override
    public void registerWith(ProteusBuilder builder) {
        builder.register(new ViewPagerParser(adapterFactory))
            .register(new TabLayoutParser())
            .register(new AppBarLayoutParser())
            .register(new BottomNavigationViewParser())
            .register(new CollapsingToolbarLayoutParser())
            .register(new CoordinatorLayoutParser())
            .register(new FloatingActionButtonParser())
            .register(new AppCompatEditTextParser())
            .register(new TextInputEditTextParser())
            .register(new TextInputLayoutParser());
        DesignModuleAttributeHelper.register(builder);
    }

    public static class Builder {
        @NonNull private final ViewPagerAdapterFactory adapterFactory =
            new ViewPagerAdapterFactory();

        DesignModule build() {
            adapterFactory.register(DesignModule.ADAPTER_SIMPLE_LIST, SectionsPagerAdapter.BUILDER);
            return new DesignModule(adapterFactory);
        }
    }
}
