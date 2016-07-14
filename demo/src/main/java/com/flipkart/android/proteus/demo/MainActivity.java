/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Flipkart Internet Pvt. Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.flipkart.android.proteus.demo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

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
