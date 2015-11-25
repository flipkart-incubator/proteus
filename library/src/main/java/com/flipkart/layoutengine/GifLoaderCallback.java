package com.flipkart.layoutengine;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by himanshu.neema on 24/11/15.
 */
public interface GifLoaderCallback {
    /**
     * Used for asynchronous image loading and when response is not null
     *
     * @param gifDrawable
     */
    void onResponse(GifDrawable gifDrawable);

    /**
     * Used when response returned from client is due to an error
     *
     * @param errorMessage
     */
    void onErrorReceived(String errorMessage, GifDrawable errorDrawable);
}
