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

package com.flipkart.android.proteus.toolbox;

import android.graphics.Bitmap;
import android.view.View;

import com.flipkart.android.proteus.ImageLoaderCallback;
import com.google.gson.JsonObject;

import java.util.concurrent.Future;

/**
 * Used for loading bitmap from network. This is used by layoutengine whenever resources have to downloaded.
 */
public interface BitmapLoader {
    /**
     * Useful for Synchronous download of bitmap. Use the returned {@link java.util.concurrent.Future#get()} to block on the download.
     *
     * @param imageUrl
     * @return
     */
    Future<Bitmap> getBitmap(String imageUrl, View view);

    /**
     * Useful for asynchronous download of bitmap.
     *
     * @param imageUrl
     * @param imageLoaderCallback
     * @param layout
     */
    void getBitmap(String imageUrl, ImageLoaderCallback imageLoaderCallback, View view, JsonObject layout);
}
