/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 *
 * Copyright (c) 2017 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.flipkart.android.proteus.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.flipkart.android.proteus.Proteus;
import com.flipkart.android.proteus.ProteusBuilder;
import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusLayoutInflater;
import com.flipkart.android.proteus.LayoutManager;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.demo.converter.GsonConverterFactory;
import com.flipkart.android.proteus.demo.models.JsonResource;
import com.flipkart.android.proteus.gson.ProteusTypeAdapterFactory;
import com.flipkart.android.proteus.toolbox.DrawableCallback;
import com.flipkart.android.proteus.toolbox.EventType;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.Value;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;

import com.flipkart.android.proteus.StyleManager;


public class ProteusActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2:8080/data/";

    private Proteus proteus;
    private Retrofit retrofit;
    private JsonResource resources;

    private ViewGroup container;

    private ProteusLayoutInflater layoutInflater;

    private JsonObject data;
    private Layout layout;

    private Styles styles;
    private Map<String, Layout> layouts;

    private StyleManager styleManager = new StyleManager() {

        @Nullable
        @Override
        protected Styles getStyles() {
            return styles;
        }
    };

    private LayoutManager layoutManager = new LayoutManager() {

        @Nullable
        @Override
        protected Map<String, Layout> getLayouts() {
            return layouts;
        }
    };

    /**
     * Simple implementation of ImageLoader for loading images from url in background.
     */
    private ProteusLayoutInflater.ImageLoader loader = new ProteusLayoutInflater.ImageLoader() {
        @Override
        public void getBitmap(ProteusView view, String url, final DrawableCallback callback) {
            URL _url;

            try {
                _url = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return;
            }

            new AsyncTask<URL, Integer, Bitmap>() {
                @Override
                protected Bitmap doInBackground(URL... params) {
                    if (isNetworkAvailable()) {
                        try {
                            return BitmapFactory.decodeStream(params[0].openConnection().getInputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("PROTEUS", "No network");
                    }
                    return null;
                }

                protected void onPostExecute(@Nullable Bitmap result) {
                    if (result != null) {
                        callback.setBitmap(result);
                    } else {
                        callback.setDrawable(ProteusActivity.this.getResources().getDrawable(R.drawable.ic_launcher));
                    }
                }
            }.execute(_url);
        }
    };

    /**
     * Implementation of Callback. This is where we get callbacks from proteus regarding
     * errors and events.
     */
    private ProteusLayoutInflater.Callback callback = new ProteusLayoutInflater.Callback() {

        @NonNull
        @Override
        public ProteusView onUnknownViewType(ProteusContext context, String type, Layout layout, JsonObject data, int index) {
            //noinspection ConstantConditions because we want to crash here
            return null;
        }

        @NonNull
        @Override
        public View onEvent(ProteusView view, EventType eventType, Value value) {
            //noinspection ConstantConditions because we want to crash here
            return null;
        }

        @Override
        @Nullable
        public PagerAdapter onPagerAdapterRequired(ProteusView parent, List<ProteusView> children, Layout layout) {
            return null;
        }

        @Override
        @Nullable
        public Adapter onAdapterRequired(ProteusView parent, List<ProteusView> children, Layout layout) {
            return null;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (null == retrofit) {
            ProteusTypeAdapterFactory factory = new ProteusTypeAdapterFactory(this);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(factory)
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        if (null == resources) {
            resources = retrofit.create(JsonResource.class);
        }

        proteus = new ProteusBuilder()
                .register("CircleView", new CircleViewParser(), "View")
                .build();

        layoutInflater = proteus.createContext(this, loader, callback, layoutManager, styleManager).getInflater();

        ProteusTypeAdapterFactory.PROTEUS_INSTANCE_HOLDER.setProteus(proteus);

        fetch();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_proteus);

        // set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // setBoolean refresh button click
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetch();
            }
        });

        container = (ViewGroup) findViewById(R.id.content_main);
    }

    private void render() {
        container.removeAllViews();
        // Inflate a new view using proteus
        long start = System.currentTimeMillis();
        ProteusView view = layoutInflater.inflate(layout, data, container, 0);
        System.out.println(System.currentTimeMillis() - start);
        container.addView(view.getAsView());
    }

    private void fetch() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {

                    Call<JsonObject> callData = resources.get("user.json");
                    data = callData.execute().body();

                    Call<Layout> callLayout = resources.getLayout();
                    layout = callLayout.execute().body();

                    Call<Map<String, Layout>> layoutsCall = resources.getLayouts();
                    layouts = layoutsCall.execute().body();

                    Call<Styles> stylesCall = resources.getStyles();
                    styles = stylesCall.execute().body();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    render();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically setBoolean clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.render) {
            render();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
