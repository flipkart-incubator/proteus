package com.flipkart.layoutengine.toolbox;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;

import com.flipkart.layoutengine.ImageLoaderCallBack;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Useful for asynchronous/synchronous loading of network images.
 * This is different from {@link com.android.volley.toolbox.NetworkImageView} in a way that this helper can be used for any view.
 * All you have to do is implement the callback to set any property which accepts drawable as a param.
 * Pass the drawable callback to get the loaded drawable.
 */
public class NetworkDrawableHelper {

    private final View view;
    private final DrawableCallback callback;
    private final Context context;
    private final BitmapLoader bitmapLoader;

    /**
     * @param context         Android Context
     * @param view            The view which is going to show the drawable. This view will be used to start and stop image loading when view is added and removed from window. Only used when <code>loadImmediately</code> is set to false. Will also work if view is set to null.
     * @param url             The url to load
     * @param loadImmediately Set this to true to load the image on the calling thread (synchronous). If false, volley's thread will be used.
     * @param callback        Implement this to get a hold of the loaded bitmap or the error reason.
     */
    public NetworkDrawableHelper(final Context context, final View view, final String url, boolean loadImmediately, DrawableCallback callback, BitmapLoader bitmapLoader) {
        this.view = view;
        this.callback = callback;
        this.context = context;
        this.bitmapLoader = bitmapLoader;
        init(url, loadImmediately);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private void init(final String url, boolean loadImmediately) {

        // CountDownLatch is used to support loadImmediately param.
        // Since ImageLoader doesnt support synchronous loading, we use CountDownLatch to simulate it.
        final CountDownLatch lock;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1 || view == null) {
            // older androids dont support addOnAttachStateChangeListener
            loadImmediately = true;
        }

        if (loadImmediately) {
            startSyncLoad(url);

        } else {
            lock = null;
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(final View view) {
                    startAsyncLoad(url);
                }

                @Override
                public void onViewDetachedFromWindow(View view) {
                    cancelLoad();
                }
            });
        }

    }

    /**
     * Warning : This method will only work if called from non main thread.
     * @param url
     */
    private void startSyncLoad(String url) {
        Future<Bitmap> future = bitmapLoader.getBitmap(url);
        try {
            Bitmap bitmap = future.get(10, TimeUnit.SECONDS );
            callback.onDrawableLoad(url,convertBitmapToDrawable(bitmap));
        } catch (Exception e) {
            e.printStackTrace();
            callback.onDrawableError(url,e.getLocalizedMessage());
        }
    }

    private Drawable convertBitmapToDrawable(Bitmap bitmapOriginal) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = bitmapOriginal.getWidth();
        int height = bitmapOriginal.getHeight();

        float scaleWidth = displayMetrics.scaledDensity;
        float scaleHeight = displayMetrics.scaledDensity;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOriginal, 0, 0, width, height, matrix, true);

        return new BitmapDrawable(context.getResources(),resizedBitmap);
    }


    private void cancelLoad() {
    }

    private void startAsyncLoad(final String url) {

        bitmapLoader.getBitmap(url, new ImageLoaderCallBack() {
            @Override
            public void onResponse(Bitmap bitmap) {
                if (bitmap == null) return;
                if (callback != null) {
                    callback.onDrawableLoad(url, convertBitmapToDrawable(bitmap));
                }
            }

            @Override
            public void onErrorReceived(String errorMessage) {
                if (callback != null) {
                    callback.onDrawableError(url, errorMessage);
                }

            }
        });

    }

    public interface DrawableCallback {
    public void onDrawableLoad(String url, Drawable drawable);

    public void onDrawableError(String url, String reason);
}
}
