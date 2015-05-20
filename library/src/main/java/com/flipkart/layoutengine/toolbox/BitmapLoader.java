package com.flipkart.layoutengine.toolbox;

import android.graphics.Bitmap;
import android.view.View;

import com.flipkart.layoutengine.ImageLoaderCallBack;

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
     * @param imageLoaderCallBack
     */
    void getBitmap(String imageUrl, ImageLoaderCallBack imageLoaderCallBack, View view);
}
