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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * BaseActivity
 *
 * @author aditya.sharat
 */
public abstract class BaseActivity extends AppCompatActivity {

    public BaseActivity() {
        PerformanceTracker.instance(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long startTime = System.currentTimeMillis();

        View view = createAndBindView();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        onBuildComplete(elapsedTime);

        Toast.makeText(this, "render time: " + elapsedTime, Toast.LENGTH_SHORT).show();

        attachView(view);
    }

    abstract View createAndBindView();

    abstract void attachView(View view);

    abstract void onBuildComplete(long time);

    @Override
    protected void onPause() {
        PerformanceTracker.instance(this).apply();
        super.onPause();
    }
}
