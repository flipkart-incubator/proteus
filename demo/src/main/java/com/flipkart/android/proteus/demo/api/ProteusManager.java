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

import android.os.AsyncTask;
import android.util.Log;

import com.flipkart.android.proteus.Proteus;
import com.flipkart.android.proteus.ProteusBuilder;
import com.flipkart.android.proteus.Styles;
import com.flipkart.android.proteus.demo.CircleViewParser;
import com.flipkart.android.proteus.gson.ProteusTypeAdapterFactory;
import com.flipkart.android.proteus.support.design.DesignModule;
import com.flipkart.android.proteus.support.v4.SupportV4Module;
import com.flipkart.android.proteus.support.v7.CardViewModule;
import com.flipkart.android.proteus.support.v7.RecyclerViewModule;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import retrofit2.Retrofit;

public class ProteusManager {

  private final ProteusApi api;
  private final Proteus proteus;

  private ObjectValue data;
  private Layout rootLayout;
  private Map<String, Layout> layouts;
  private Styles styles;

  private Set<Listener> listeners = new HashSet<>();

  public ProteusManager(Retrofit retrofit) {
    this.api = retrofit.create(ProteusApi.class);
    proteus = new ProteusBuilder()
      .register(SupportV4Module.create())
      .register(RecyclerViewModule.create())
      .register(CardViewModule.create())
      .register(DesignModule.create())
      .register(new CircleViewParser())
      .build();

    ProteusTypeAdapterFactory.PROTEUS_INSTANCE_HOLDER.setProteus(proteus);
  }

  public void load() {
    new DataLoaderTask(this).execute();
  }

  public void update() {
    new DataLoaderTask(this).execute();
  }

  public Proteus getProteus() {
    return proteus;
  }

  public ObjectValue getData() {
    return data;
  }

  public Layout getRootLayout() {
    return rootLayout;
  }

  public Map<String, Layout> getLayouts() {
    return layouts;
  }

  public Styles getStyles() {
    return styles;
  }

  public void addListener(@NonNull Listener listener) {
    listeners.add(listener);
  }

  public void removeListener(@NonNull Listener listener) {
    listeners.remove(listener);
  }

  private void broadcast(@Nullable Exception e) {
    if (e == null) {
      notifySuccess();
    } else {
      notifyError(e);
    }
  }

  private void notifySuccess() {
    for (Listener listener : listeners) {
      listener.onLoad();
    }
  }

  private void notifyError(@NonNull Exception e) {
    for (Listener listener : listeners) {
      listener.onError(e);
    }
  }

  private static class DataLoaderTask extends AsyncTask<Void, Void, Exception> {

    private final ProteusManager manager;

    DataLoaderTask(ProteusManager manager) {
      this.manager = manager;
    }

    @Override
    protected Exception doInBackground(Void... params) {
      try {
        ProteusApi api = manager.api;
        manager.data = api.getUserData().execute().body();
        manager.rootLayout = api.getLayout().execute().body();
        manager.layouts = api.getLayouts().execute().body();
        manager.styles = api.getStyles().execute().body();
      } catch (Exception e) {
        Log.e(getClass().getSimpleName(), "ERROR: " + e.getMessage());
        return e;
      }
      return null;
    }

    @Override
    protected void onPostExecute(Exception e) {
      super.onPostExecute(e);
      manager.broadcast(e);
    }
  }

  public interface Listener {

    void onLoad();

    void onError(@NonNull Exception e);
  }
}
