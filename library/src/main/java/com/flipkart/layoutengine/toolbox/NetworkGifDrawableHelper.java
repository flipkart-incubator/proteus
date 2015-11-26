package com.flipkart.layoutengine.toolbox;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import android.view.View;

import com.flipkart.layoutengine.GifLoaderCallback;

import com.google.gson.JsonObject;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by himanshu.neema on 24/11/15.
 */
public class NetworkGifDrawableHelper {

    private final View view;
    private final GifDrawableCallback callback;
    private final Context context;
    private final GifLoader gifLoader;
    private final JsonObject layout;

    /**
     * @param context         Android Context
     * @param view            The view which is going to show the drawable. This view will be used to start and stop image loading when view is added and removed from window. Only used when <code>loadImmediately</code> is set to false. Will also work if view is set to null.
     * @param url             The url to load
     * @param loadImmediately Set this to true to load the image on the calling thread (synchronous). If false, volley's thread will be used.
     * @param callback        Implement this to get a hold of the loaded bitmap or the error reason.
     * @param layout
     */
    public NetworkGifDrawableHelper(final Context context, final View view, final String url,
                                 boolean loadImmediately, GifDrawableCallback callback, GifLoader gifLoader,
                                 JsonObject layout) {
        this.view = view;
        this.callback = callback;
        this.context = context;
        this.gifLoader = gifLoader;
        this.layout = layout;
        init(url, loadImmediately);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private void init(final String url, boolean loadImmediately) {

        // CountDownLatch is used to support loadImmediately param.
        // Since ImageLoader doesnt support synchronous loading, we use CountDownLatch to simulate it.
        final CountDownLatch lock;

        if (view == null) {
            loadImmediately = true;
        }

        if (loadImmediately) {
            startSyncLoad(url, view);
        } else {
            lock = null;
            startAsyncLoad(url, view);
        }
    }

    /**
     * Warning : This method will only work if called from non main thread.
     *
     * @param url
     */
    private void startSyncLoad(String url, View view) {
        Future<GifDrawable> future = gifLoader.getGifDrawable(url, view);
        try {
            GifDrawable gifDrawable = future.get(10, TimeUnit.SECONDS);
            callback.onDrawableLoad(url, gifDrawable);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onDrawableError(url, e.getLocalizedMessage(), null);
        }
    }

    private void startAsyncLoad(final String url, View view) {

        gifLoader.getGifDrawable(url, new GifLoaderCallback() {
            @Override
            public void onResponse(GifDrawable gifDrawable) {
                if (gifDrawable == null) return;
                if (callback != null) {
                    callback.onDrawableLoad(url, gifDrawable);
                }
            }

            @Override
            public void onErrorReceived(String errorMessage, GifDrawable errorDrawable) {
                if (callback != null) {
                    callback.onDrawableError(url, errorMessage, errorDrawable);
                }
            }
        }, view, layout);
    }

    public interface GifDrawableCallback {
        void onDrawableLoad(String url, GifDrawable drawable);

        void onDrawableError(String url, String reason, GifDrawable errorDrawable);
    }

}
