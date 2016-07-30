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

package com.flipkart.android.proteus;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by prateek.dixit on 1/29/15.
 */
public interface ImageLoaderCallback {

    /**
     * Called when asynchronously loading an image and when response is not null
     *
     * @param bitmap
     */
    void onResponse(Bitmap bitmap);

    /**
     * Called when response returned from client is due to an error
     *
     * @param errorMessage
     */
    void onErrorReceived(String errorMessage, Drawable errorDrawable);
}
