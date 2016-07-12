package com.flipkart.proteus.toolbox;

import android.graphics.Bitmap;
import android.view.View;

import com.flipkart.proteus.ImageLoaderCallback;
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
