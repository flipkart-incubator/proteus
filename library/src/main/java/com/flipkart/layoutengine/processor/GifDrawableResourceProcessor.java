package com.flipkart.layoutengine.processor;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.URLUtil;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.toolbox.NetworkDrawableHelper;
import com.flipkart.layoutengine.toolbox.NetworkGifDrawableHelper;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

import pl.droidsonroids.gif.GifDrawable;


/**
 * Created by himanshu.neema on 24/11/15.
 */
public abstract class GifDrawableResourceProcessor<V extends View> extends AttributeProcessor<V> {

    private static final String TAG = GifDrawableResourceProcessor.class.getSimpleName();
    protected Context context;
    private Logger logger = LoggerFactory.getLogger(GifDrawableResourceProcessor.class);


    public GifDrawableResourceProcessor(Context context) {
        this.context = context;
    }

    @Override
    public void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue,
                       V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
        if (attributeValue.isJsonPrimitive()) {
            handleString(parserContext, attributeKey, attributeValue.getAsString(), view, proteusView, parent, layout, index);
        } else if (attributeValue.isJsonObject()) {
            logger.warn(TAG, "Not implemented for json");
            return;
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("Resource for key: " + attributeKey
                        + " must be a primitive or an object. value -> " + attributeValue.toString());
            }
        }
    }

    protected void handleString(ParserContext parserContext, String attributeKey, String attributeValue, final V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
        boolean synchronousRendering = parserContext.getLayoutBuilder().isSynchronousRendering();

        if (ParseHelper.isLocalResourceAttribute(attributeValue)) {
            int attributeId = ParseHelper.getAttributeId(context, attributeValue);
            if (0 != attributeId) {
                TypedArray ta = context.obtainStyledAttributes(new int[]{attributeId});
                Drawable drawable = ta.getDrawable(0 /* index */);
                ta.recycle();
                setDrawable(view, drawable);
            }
        } else if (ParseHelper.isColor(attributeValue)) {
            setDrawable(view, new ColorDrawable(ParseHelper.parseColor(attributeValue)));
        } else if (ParseHelper.isLocalDrawableResource(attributeValue)) {
            try {
                Resources r = context.getResources();
                int drawableId = r.getIdentifier(attributeValue, "drawable", context.getPackageName());
                Drawable drawable = new GifDrawable(r, drawableId);
                setDrawable(view, drawable);
            } catch (Exception ex) {
                System.out.println("Could not load local resource " + attributeValue);
            }
        } else if (URLUtil.isValidUrl(attributeValue)) {

            NetworkGifDrawableHelper.GifDrawableCallback callback = new NetworkGifDrawableHelper.GifDrawableCallback() {
                @Override
                public void onDrawableLoad(String url, GifDrawable drawable) {
                    setDrawable(view, drawable);
                }

                @Override
                public void onDrawableError(String url, String reason, GifDrawable errorDrawable) {
                    System.out.println("Could not load " + url + " : " + reason);
                    if (errorDrawable != null)
                        setDrawable(view, errorDrawable);
                }
            };

            new NetworkGifDrawableHelper(context, view, attributeValue, synchronousRendering,
                    callback, parserContext.getLayoutBuilder().getNetworkGifDrawableHelper(), layout);
        }

    }

    public abstract void setDrawable(V view, Drawable drawable);
}
