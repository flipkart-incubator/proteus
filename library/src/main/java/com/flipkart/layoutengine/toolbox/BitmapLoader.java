package com.flipkart.layoutengine.toolbox;

import android.graphics.Bitmap;

import com.flipkart.layoutengine.ImageLoaderCallBack;

import java.util.concurrent.Future;

/**
 * Used for loading bitmap from network. This is used by layoutengine whenever resources have to downloaded.
 */
public interface BitmapLoader {
    /**
     * Useful for Synchronous download of bitmap. Use the returned {@link java.util.concurrent.Future#get()} to block on the download.
     * @param imageUrl
     * @return
     */
    public Future<Bitmap> getBitmap(String imageUrl);

    /**
     * Useful for asynchronous download of bitmap.
     * @param imageUrl
     * @param imageLoaderCallBack
     */
    public void getBitmap(String imageUrl, ImageLoaderCallBack imageLoaderCallBack);
}
