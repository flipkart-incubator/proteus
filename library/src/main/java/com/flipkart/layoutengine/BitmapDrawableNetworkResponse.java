package com.flipkart.layoutengine;

import android.graphics.Bitmap;

/**
 * Created by prateek.dixit on 1/29/15.
 */
public class BitmapDrawableNetworkResponse {
    private Bitmap bitmap;
    private String error;

    public BitmapDrawableNetworkResponse() {
    }

    public BitmapDrawableNetworkResponse(Bitmap bitmap, String error) {
        this.bitmap = bitmap;
        this.error = error;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
