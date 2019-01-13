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

package com.flipkart.android.proteus.demo;

import android.app.Application;

import com.flipkart.android.proteus.demo.api.ProteusManager;
import com.flipkart.android.proteus.demo.converter.GsonConverterFactory;
import com.flipkart.android.proteus.gson.ProteusTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;

public class DemoApplication extends Application {

  private static final String BASE_URL = "http://10.0.2.2:8080/data/";

  private Gson gson;
  private Retrofit retrofit;
  private ProteusManager proteusManager;

  @Override
  public void onCreate() {
    super.onCreate();

    // register the proteus type adapter to deserialize proteus resources
    ProteusTypeAdapterFactory adapter = new ProteusTypeAdapterFactory(this);
    gson = new GsonBuilder()
      .registerTypeAdapterFactory(adapter)
      .create();

    // add gson to retrofit to allow deserializing proteus resources when fetched via retrofit
    retrofit = new Retrofit.Builder()
      .baseUrl(BASE_URL)
      .addConverterFactory(GsonConverterFactory.create(getGson()))
      .build();

    // ProteusManager is a reference implementation to fetch and
    // update all proteus resources from a remote server
    proteusManager = new ProteusManager(getRetrofit());
  }

  public Gson getGson() {
    return gson;
  }

  public Retrofit getRetrofit() {
    return retrofit;
  }

  public ProteusManager getProteusManager() {
    return proteusManager;
  }
}
