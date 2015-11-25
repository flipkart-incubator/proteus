package com.flipkart.layoutengine.toolbox;

import android.view.View;

import com.flipkart.layoutengine.GifLoaderCallback;
import com.google.gson.JsonObject;

import java.util.concurrent.Future;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by himanshu.neema on 24/11/15.
 */
public interface GifLoader {

    /**
     * Useful for Synchronous download of gif. Use the returned {@link java.util.concurrent.Future#get()} to block on the download.
     *
     * @param imageUrl
     * @return
     */
    Future<GifDrawable> getGifDrawable(String imageUrl, View view);

    /**
     * Useful for asynchronous download of gif.
     *
     * @param imageUrl
     * @param gifLoaderCallback
     * @param layout
     */
    void getGifDrawable(String imageUrl, GifLoaderCallback gifLoaderCallback, View view, JsonObject layout);
}
