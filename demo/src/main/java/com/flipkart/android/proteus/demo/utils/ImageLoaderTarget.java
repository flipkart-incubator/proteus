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

package com.flipkart.android.proteus.demo.utils;

import android.graphics.drawable.Drawable;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flipkart.android.proteus.value.DrawableValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ImageLoaderTarget extends SimpleTarget<Drawable> {

  @NonNull
  private final DrawableValue.AsyncCallback callback;

  public ImageLoaderTarget(@NonNull DrawableValue.AsyncCallback callback) {
    this.callback = callback;
  }

  @Override
  public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
    callback.setDrawable(resource);
  }
}
