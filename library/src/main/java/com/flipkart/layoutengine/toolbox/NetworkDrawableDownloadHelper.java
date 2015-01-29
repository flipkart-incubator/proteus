package com.flipkart.layoutengine.toolbox;

import com.flipkart.layoutengine.ImageLoaderCallBack;

/**
 * Created by prateek.dixit on 1/29/15.
 */
public interface NetworkDrawableDownloadHelper {
    public com.android.volley.toolbox.RequestFuture<android.graphics.Bitmap> getDrawableFuture(String imageUrl);
    public void setBitmap(String imageUrl, ImageLoaderCallBack imageLoaderCallBack);
}
