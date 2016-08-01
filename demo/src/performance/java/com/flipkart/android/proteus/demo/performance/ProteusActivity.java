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

package com.flipkart.android.proteus.demo.performance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;

import com.flipkart.android.proteus.EventType;
import com.flipkart.android.proteus.ImageLoaderCallback;
import com.flipkart.android.proteus.builder.DataAndViewParsingLayoutBuilder;
import com.flipkart.android.proteus.builder.LayoutBuilderCallback;
import com.flipkart.android.proteus.builder.LayoutBuilderFactory;
import com.flipkart.android.proteus.demo.R;
import com.flipkart.android.proteus.toolbox.BitmapLoader;
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


public class ProteusActivity extends BaseActivity {

    private ProteusView proteusView;
    private Gson gson;
    private DataAndViewParsingLayoutBuilder builder;
    private FrameLayout container;
    private JsonObject pageLayout;
    private JsonObject data;
    private ViewGroup.LayoutParams layoutParams;
    private Styles styles;
    private BitmapLoader bitmapLoader = new BitmapLoader() {
        @Override
        public Future<Bitmap> getBitmap(String imageUrl, View view) {
            return null;
        }

        @Override
        public void getBitmap(String imageUrl, final ImageLoaderCallback callback, View view, JsonObject layout) {
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
    private LayoutBuilderCallback callback = new LayoutBuilderCallback() {

        @Override
        public void onUnknownAttribute(String attribute, JsonElement value, ProteusView view) {
            Log.i("unknown-attribute", attribute + " in " + view.getViewManager().getLayout().toString());
        }

        @Nullable
        @Override
        public ProteusView onUnknownViewType(String type, View parent, JsonObject layout, JsonObject data, int index, Styles styles) {
            return null;
        }

        @Override
        public JsonObject onLayoutRequired(String type, ProteusView parent) {
            return null;
        }

        @Override
        public void onViewBuiltFromViewProvider(ProteusView view, View parent, String type, int index) {

        }

        @Override
        public View onEvent(ProteusView view, JsonElement value, EventType eventType) {
            Log.d("event", value.toString());
            return (View) view;
        }

        @Override
        public PagerAdapter onPagerAdapterRequired(ProteusView parent, List<ProteusView> children, JsonObject layout) {
            return null;
        }

        @Override
        public Adapter onAdapterRequired(ProteusView parent, List<ProteusView> children, JsonObject layout) {
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gson = new Gson();
        styles = gson.fromJson(getJsonFromFile(R.raw.styles).getAsJsonObject(), Styles.class);
        Map<String, JsonObject> layoutProvider = getProviderFromFile(R.raw.layout_provider);
        pageLayout = getJsonFromFile(R.raw.page_layout).getAsJsonObject();

        data = getJsonFromFile(R.raw.data_init).getAsJsonObject();

        builder = new LayoutBuilderFactory().getDataAndViewParsingLayoutBuilder(layoutProvider);
        builder.setListener(callback);
        builder.setBitmapLoader(bitmapLoader);

        container = new FrameLayout(ProteusActivity.this);
        layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        super.onCreate(savedInstanceState);
    }

    @Override
    View createAndBindView() {
        proteusView = builder.build(container, pageLayout, data, 0, styles);
        return (View) proteusView;
    }

    @Override
    void attachView(View view) {
        container.addView((View) proteusView, layoutParams);
        setContentView(container);
    }

    @Override
    void onBuildComplete(long time) {
        com.flipkart.android.proteus.demo.performance.PerformanceTracker.instance(this).updateProteusRenderTime(time);
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
