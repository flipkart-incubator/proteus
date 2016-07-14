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
