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

import com.flipkart.android.proteus.ProteusBuilder;
import com.flipkart.android.proteus.support.design.widget.AppBarLayoutParser;
import com.flipkart.android.proteus.support.design.widget.BottomNavigationViewParser;
import com.flipkart.android.proteus.support.design.widget.CollapsingToolbarLayoutParser;
import com.flipkart.android.proteus.support.design.widget.CoordinatorLayoutParser;
import com.flipkart.android.proteus.support.design.widget.FloatingActionButtonParser;

/**
 * DesignModule
 *
 * @author adityasharat
 */

public class DesignModule implements ProteusBuilder.Module {

  private DesignModule() {
  }

  public static DesignModule create() {
    return new DesignModule();
  }

  @Override
  public void registerWith(ProteusBuilder builder) {
    builder.register(new AppBarLayoutParser());
    builder.register(new BottomNavigationViewParser());
    builder.register(new CollapsingToolbarLayoutParser());
    builder.register(new CoordinatorLayoutParser());
    builder.register(new FloatingActionButtonParser());
    DesignModuleAttributeHelper.register(builder);
  }

}
