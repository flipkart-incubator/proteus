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

package com.flipkart.android.proteus.demo.api;

import com.flipkart.android.proteus.Styles;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * ProteusApi
 *
 * @author aditya.sharat
 */

public interface ProteusApi {

  @GET("user.json")
  Call<ObjectValue> getUserData();

  @GET("styles.json")
  Call<Styles> getStyles();

  @GET("layout.json")
  Call<Layout> getLayout();

  @GET("layouts.json")
  Call<Map<String, Layout>> getLayouts();
}