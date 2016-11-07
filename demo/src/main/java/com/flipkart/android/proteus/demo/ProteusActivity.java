/*
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.android.proteus.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;

import com.flipkart.android.proteus.LayoutParser;
import com.flipkart.android.proteus.builder.DataAndViewParsingLayoutInflater;
import com.flipkart.android.proteus.builder.LayoutBuilderFactory;
import com.flipkart.android.proteus.json.JsonLayoutParser;
import com.flipkart.android.proteus.toolbox.BitmapLoader;
import com.flipkart.android.proteus.toolbox.EventType;
import com.flipkart.android.proteus.toolbox.ImageLoaderCallback;
import com.flipkart.android.proteus.toolbox.LayoutBuilderCallback;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.view.ProteusView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;


public class ProteusActivity extends AppCompatActivity {
    private Gson gson;

    /**
     * Simple implementation of BitmapLoader for loading images from url in background.
     */
    private BitmapLoader bitmapLoader = new BitmapLoader() {
        @Override
        public Future<Bitmap> getBitmap(String imageUrl, ProteusView view) {
            return null;
        }

        @Override
        public void getBitmap(ProteusView view, String imageUrl, final ImageLoaderCallback callback, LayoutParser parser) {
            URL url;

            try {
                url = new URL(imageUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return;
            }

            new AsyncTask<URL, Integer, Bitmap>() {
                @Override
                protected Bitmap doInBackground(URL... params) {
                    try {
                        return BitmapFactory.decodeStream(params[0].openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                protected void onPostExecute(Bitmap result) {
                    callback.onResponse(result);
                }
            }.execute(url);

        }
    };

    /**
     * Implementation of LayoutBuilderCallback. This is where we get callbacks from proteus regarding
     * errors and events.
     */
    private LayoutBuilderCallback callback = new LayoutBuilderCallback() {


        @Override
        public void onUnknownAttribute(ProteusView view, String attribute, LayoutParser parser) {

        }

        @Nullable
        @Override
        public ProteusView onUnknownViewType(String type, View parent, LayoutParser layout, JsonObject data, Styles styles, int index) {
            return null;
        }

        @Override
        public JsonObject onLayoutRequired(String type, LayoutParser parent) {
            return null;
        }

        @Override
        public void onViewBuiltFromViewProvider(ProteusView view, View parent, String type, int index) {

        }

        @Override
        public View onEvent(ProteusView view, EventType eventType, LayoutParser value) {
            return null;
        }

        @Override
        public PagerAdapter onPagerAdapterRequired(ProteusView parent, List<ProteusView> children, LayoutParser layout) {
            return null;
        }

        @Override
        public Adapter onAdapterRequired(ProteusView parent, List<ProteusView> children, LayoutParser layout) {
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init gson instance
        gson = new Gson();

        // Deserialize json data to objects. We will need this data for inflating proteus view.
        // This data should come from remote server if we wish to change layouts without app updates.
        Styles styles = gson.fromJson(getJsonFromFile(R.raw.styles).getAsJsonObject(), Styles.class);
        Map<String, JsonObject> layoutProvider = getProviderFromFile(R.raw.layout_provider);
        JsonObject pageLayout = getJsonFromFile(R.raw.page_layout).getAsJsonObject();
        JsonObject data = getJsonFromFile(R.raw.data_init).getAsJsonObject();

        // Init dataAndViewParsingLayoutBuilder and set layoutProvider,
        // layoutBuilderCallback and bitmapLoader we initialised before.
        DataAndViewParsingLayoutInflater builder = new LayoutBuilderFactory().getDataAndViewParsingLayoutBuilder(null);
        builder.setListener(callback);
        builder.setBitmapLoader(bitmapLoader);

        // Make a container layout with activity context and layoutParams for it.
        FrameLayout container = new FrameLayout(ProteusActivity.this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        // Get instance of proteusView from dataAndViewParsingLayoutBuilder
        ProteusView proteusView = builder.build(container, new JsonLayoutParser(pageLayout), data, styles, 0);

        // Add proteusView and layoutParams to container layout.
        container.addView((View) proteusView, layoutParams);

        // Set container layout to activity content view.
        setContentView(container);
    }

    private JsonElement getJsonFromFile(int resId) {
        InputStream inputStream = getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return gson.fromJson(reader, JsonElement.class);
    }

    private Map<String, JsonObject> getProviderFromFile(int resId) {
        InputStream inputStream = getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return gson.fromJson(reader, (new TypeToken<Map<String, JsonObject>>() {
        }).getType());
    }
}
