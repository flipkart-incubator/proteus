package com.flipkart.layoutengine;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by prateek.dixit on 1/29/15.
 */
public interface ImageLoaderCallBack {

    /**
     * Used for asynchronous image loading and when response is not null
     *
     * @param bitmap
     */
    void onResponse(Bitmap bitmap);

    void handled();

    /**
     * Used when response returned from client is due to an error
     *
     * @param errorMessage
     */
    void onErrorReceived(String errorMessage, Drawable errorDrawable);
}
