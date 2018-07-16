/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 *
 * Copyright (c) 2018 Flipkart Internet Pvt. Ltd.
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

package com.flipkart.android.proteus.demo.tasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.flipkart.android.proteus.demo.R;
import com.flipkart.android.proteus.value.DrawableValue;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

public class ImageLoaderTask extends AsyncTask<String, Integer, Bitmap> {

    @NonNull
    private final WeakReference<Activity> activityReference;
    @NonNull
    private final DrawableValue.AsyncCallback callback;

    public ImageLoaderTask(@NonNull Activity activity, @NonNull DrawableValue.AsyncCallback callback) {
        this.activityReference = new WeakReference<>(activity);
        this.callback = callback;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        URL url;

        try {
            url = new URL(params[0]);
            if (isNetworkAvailable()) {
                return BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } else {
                Log.e("PROTEUS", "No network");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void onPostExecute(@Nullable Bitmap result) {
        Activity activity = this.activityReference.get();
        if (activity != null) {
            if (result != null) {
                callback.setBitmap(result);
            } else {
                callback.setDrawable(activity.getResources().getDrawable(R.drawable.ic_launcher));
            }
        }
    }

    private boolean isNetworkAvailable() {
        Activity activity = this.activityReference.get();
        if (activity != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        }
        return false;
    }
}
