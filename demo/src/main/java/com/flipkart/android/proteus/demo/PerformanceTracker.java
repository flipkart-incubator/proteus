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

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * PerformanceTracker
 *
 * @author aditya.sharat
 */
public class PerformanceTracker {

    private static final String PREF_NAME = "performance";

    private static final String KEY = "data";

    private static PerformanceTracker tracker;
    private final SharedPreferences preferences;
    private Gson gson;
    private Values values;

    private PerformanceTracker(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        if (preferences.contains(KEY)) {
            values = gson.fromJson(preferences.getString(KEY, null), Values.class);
        } else {
            values = new Values();
            apply();
        }
    }

    public static PerformanceTracker instance(Context context) {
        if (tracker == null) {
            tracker = new PerformanceTracker(context);
        }
        return tracker;
    }

    public void updateProteusRenderTime(long time) {
        values.addProteus(time);
    }

    public void updateNativeRenderTime(long time) {
        values.addNative(time);
    }

    public Pair<Long, Long> getAverageProteusRenderTime() {
        return getAverage(values.getProteus());
    }

    public Pair<Long, Long> getAverageNativeRenderTime() {
        return getAverage(values.getNative());
    }

    private Pair<Long, Long> getAverage(List<Long> values) {
        long n = 0;
        long total = 0;
        for (long t : values) {
            total += t;
            n++;
        }
        if (n == 0) {
            n = 1;
        }

        if (values.size() > 0) {
            total = total - values.get(0);
            n = n - 1;
        }
        if (n == 0) {
            n = 1;
        }
        return new Pair<>(total / n, n);
    }

    public void apply() {
        preferences.edit().putString(KEY, gson.toJson(values)).apply();
    }

    public static class Values {

        private List<Long> proteus;
        private List<Long> _native;

        public Values() {
            proteus = new ArrayList<>();
            _native = new ArrayList<>();
        }

        public void addProteus(long time) {
            proteus.add(time);
        }

        public void addNative(long time) {
            _native.add(time);
        }

        protected List<Long> getProteus() {
            return proteus;
        }

        protected List<Long> getNative() {
            return _native;
        }

    }
}
