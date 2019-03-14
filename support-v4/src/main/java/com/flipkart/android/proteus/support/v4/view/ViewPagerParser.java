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

package com.flipkart.android.proteus.support.v4.view;

import android.view.ViewGroup;

import com.flipkart.android.proteus.DataContext;
import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.managers.AdapterBasedViewManager;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * ViewPagerParser
 *
 * @author adityasharat
 */

public class ViewPagerParser<T extends ViewPager> extends ViewTypeParser<T> {

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
  public ProteusView createView(@NonNull ProteusContext context, @NonNull Layout layout, @NonNull ObjectValue data, @Nullable ViewGroup parent, int dataIndex) {
    return new ProteusViewPager(context);
  }

  @NonNull
  @Override
  public ProteusView.Manager createViewManager(@NonNull ProteusContext context, @NonNull ProteusView view, @NonNull Layout layout, @NonNull ObjectValue data, @Nullable ViewTypeParser caller, @Nullable ViewGroup parent, int dataIndex) {
    DataContext dataContext = createDataContext(context, layout, data, parent, dataIndex);
    return new AdapterBasedViewManager(context, null != caller ? caller : this, view.getAsView(), layout, dataContext);
  }

  @Override
  protected void addAttributeProcessors() {

  }
}
