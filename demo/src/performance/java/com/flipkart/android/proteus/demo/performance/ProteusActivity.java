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

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.flipkart.android.proteus.Proteus;
import com.flipkart.android.proteus.ProteusBuilder;
import com.flipkart.android.proteus.ProteusLayoutInflater;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.demo.R;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;


public class ProteusActivity extends BaseActivity {

    private ProteusView proteusView;
    private Gson gson;
    private ProteusLayoutInflater builder;
    private FrameLayout container;
    private Layout pageLayout;
    private ObjectValue data;
    private ViewGroup.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gson = new Gson();
        Map<String, Layout> layoutProvider = getProviderFromFile(R.raw.layout_provider);
        pageLayout = getPageLayout(R.raw.page_layout);

        data = getJsonFromFile(R.raw.data_init);

        Proteus proteus = new ProteusBuilder().build();

        builder = proteus.createContext(this).getInflater();

        container = new FrameLayout(ProteusActivity.this);
        layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        super.onCreate(savedInstanceState);
    }

    @Override
    View createAndBindView() {
        proteusView = builder.inflate(pageLayout, data, container, -1);
        return proteusView.getAsView();
    }

    @Override
    void attachView(View view) {
        container.addView(proteusView.getAsView(), layoutParams);
        setContentView(container);
    }

    @Override
    void onBuildComplete(long time) {
        com.flipkart.android.proteus.demo.performance.PerformanceTracker.instance(this).updateProteusRenderTime(time);
    }

    private ObjectValue getJsonFromFile(int resId) {
        InputStream inputStream = getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return gson.fromJson(reader, ObjectValue.class);
    }

    private Layout getPageLayout(int resId) {
        InputStream inputStream = getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return gson.fromJson(reader, Layout.class);
    }

    private Map<String, Layout> getProviderFromFile(int resId) {
        InputStream inputStream = getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return gson.fromJson(reader, (new TypeToken<Map<String, JsonObject>>() {
        }).getType());
    }
}
