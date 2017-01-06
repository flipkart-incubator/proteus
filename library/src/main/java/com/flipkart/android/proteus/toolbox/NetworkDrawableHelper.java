/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 *
 * Copyright (c) 2017 Flipkart Internet Pvt. Ltd.
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

package com.flipkart.android.proteus.toolbox;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;

import com.flipkart.android.proteus.Layout;
import com.flipkart.android.proteus.ProteusView;

/**
 * Useful for asynchronous/synchronous loading of network images.
 * This is different in a way that this helper can be used for any view.
 * All you have to do is implement the callback to set any property which accepts drawable as a param.
 * Pass the drawable callback to get the loaded drawable.
 */
public class NetworkDrawableHelper {

    /**
     * @param view     The view which is going to show the drawable. This view will be used to start and stop image loading when view is added and removed from window. Only used when <code>loadImmediately</code> is set to false. Will also work if view is set to null.
     * @param url      The url to load
     * @param callback Implement this to get a hold of the loaded bitmap or the error reason.
     * @param layout
     */
    public NetworkDrawableHelper(final ProteusView view, final String url, final BitmapLoader bitmapLoader, final DrawableCallback callback, Layout layout) {
        bitmapLoader.getBitmap(view, url, new ImageLoaderCallback() {
            @Override
            public void onResponse(Bitmap bitmap) {
                if (bitmap == null) {
                    return;
                }
                if (callback != null) {
                    callback.onDrawableLoad(url, convertBitmapToDrawable(bitmap, (View) view));
                }
            }

            @Override
            public void onErrorReceived(String errorMessage, Drawable errorDrawable) {
                if (callback != null) {
                    callback.onDrawableError(url, errorMessage, errorDrawable);
                }
            }
        }, null);
    }

    private Drawable convertBitmapToDrawable(Bitmap bitmapOriginal, View view) {

        DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
        int width = bitmapOriginal.getWidth();
        int height = bitmapOriginal.getHeight();

        float scaleWidth = displayMetrics.scaledDensity;
        float scaleHeight = displayMetrics.scaledDensity;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOriginal, 0, 0, width, height, matrix, true);

        return new BitmapDrawable(view.getContext().getResources(), resizedBitmap);
    }

    public interface DrawableCallback {

        void onDrawableLoad(String url, Drawable drawable);

        void onDrawableError(String url, String reason, Drawable errorDrawable);

    }
}
