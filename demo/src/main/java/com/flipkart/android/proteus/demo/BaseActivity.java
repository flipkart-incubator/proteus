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

        LogbackConfigureHelper.configure();

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
