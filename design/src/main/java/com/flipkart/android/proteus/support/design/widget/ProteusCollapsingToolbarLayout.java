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

package com.flipkart.android.proteus.support.design.widget;

import android.view.View;

import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusView;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.annotation.NonNull;

/**
 * ProteusCollapsingToolbarLayout
 *
 * @author adityasharat
 */

public class ProteusCollapsingToolbarLayout extends CollapsingToolbarLayout implements ProteusView {

  private Manager manager;

  public ProteusCollapsingToolbarLayout(ProteusContext context) {
    super(context);
  }

  @Override
  public Manager getViewManager() {
    return manager;
  }

  @Override
  public void setViewManager(@NonNull Manager manager) {
    this.manager = manager;
  }

  @NonNull
  @Override
  public View getAsView() {
    return this;
  }
}
