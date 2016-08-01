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
