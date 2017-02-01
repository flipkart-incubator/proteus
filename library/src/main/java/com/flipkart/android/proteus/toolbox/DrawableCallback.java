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

import com.flipkart.android.proteus.ProteusView;

/**
 * DrawableCallback
 *
 * @author aditya.sharat
 */
public abstract class DrawableCallback {

    public DrawableCallback(final ProteusView view, final String url, BitmapLoader loader) {
        loader.getBitmap(view, url, new ImageLoaderCallback() {
            @Override
            public void onResponse(Bitmap bitmap) {
                if (bitmap == null) {
                    return;
                }
                onDrawableLoad(url, convertBitmapToDrawable(bitmap, (View) view));
            }

            @Override
            public void onErrorReceived(String errorMessage, Drawable errorDrawable) {
                onDrawableError(url, errorMessage, errorDrawable);
            }
        });
    }

    public abstract void onDrawableLoad(String url, Drawable drawable);

    public abstract void onDrawableError(String url, String reason, Drawable error);

    private Drawable convertBitmapToDrawable(Bitmap original, View view) {

        DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
        int width = original.getWidth();
        int height = original.getHeight();

        float scaleWidth = displayMetrics.scaledDensity;
        float scaleHeight = displayMetrics.scaledDensity;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(original, 0, 0, width, height, matrix, true);

        return new BitmapDrawable(view.getContext().getResources(), resizedBitmap);
    }

}
