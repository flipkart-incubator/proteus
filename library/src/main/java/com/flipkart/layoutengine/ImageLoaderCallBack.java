package com.flipkart.layoutengine;

import android.graphics.Bitmap;

/**
 * Created by prateek.dixit on 1/29/15.
 */
public interface ImageLoaderCallBack {
    public void onResponse(Bitmap bitmap);
    public void onErrorReceived(String errorMessage);
}
