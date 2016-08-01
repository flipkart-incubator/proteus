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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.flipkart.android.proteus.demo.R;

public class MainActivity extends AppCompatActivity {

    private static final String RENDER_NATIVE = "Average Native Render Time: ";
    private static final String RENDER_PROTEUS = "Average Proteus Render Time: ";

    private PerformanceTracker tracker;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tracker = PerformanceTracker.instance(this);
        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.inflate_proteus);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProteusActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        view = findViewById(R.id.inflate_native);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NativeActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        TextView tv = (TextView) findViewById(R.id.perf_native);
        Pair<Long, Long> value = tracker.getAverageNativeRenderTime();
        tv.setText(RENDER_NATIVE + value.first + " (" + value.second + ")");

        tv = (TextView) findViewById(R.id.perf_proteus);
        value = tracker.getAverageProteusRenderTime();
        tv.setText(RENDER_PROTEUS + value.first + " (" + value.second + ")");
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onPostResume() {
        super.onPostResume();
        TextView tv = (TextView) findViewById(R.id.perf_native);
        Pair<Long, Long> value = tracker.getAverageNativeRenderTime();
        tv.setText(RENDER_NATIVE + value.first + " (" + value.second + ")");

        tv = (TextView) findViewById(R.id.perf_proteus);
        value = tracker.getAverageProteusRenderTime();
        tv.setText(RENDER_PROTEUS + value.first + " (" + value.second + ")");
    }

    @Override
    protected void onPause() {
        tracker.apply();
        super.onPause();
    }
}
