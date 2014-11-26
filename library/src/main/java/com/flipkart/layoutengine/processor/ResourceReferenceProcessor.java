package com.flipkart.layoutengine.processor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.webkit.URLUtil;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.toolbox.NetworkDrawableHelper;
import com.flipkart.layoutengine.toolbox.VolleySingleton;

/**
 * Use this as the base processor for references like @drawable or remote resources with http:// urls.
 */
public abstract class ResourceReferenceProcessor<T extends View> extends AttributeProcessor<T> {

    private Context context;

    public ResourceReferenceProcessor(Context context) {
        this.context = context;
    }

    @Override
    public void handle(ParserContext parserContext, String attributeKey, final String attributeValue, final T view) {
        boolean synchronousRendering = parserContext.getLayoutBuilder().isSynchronousRendering();
        ImageLoader imageLoader = VolleySingleton.getInstance(context).getImageLoader();
        RequestQueue requestQueue = VolleySingleton.getInstance(context).getRequestQueue();
        if (ParseHelper.isColor(attributeValue)) {
            view.setBackgroundColor(ParseHelper.parseColor(attributeValue));
        }
        else if(ParseHelper.isLocalResource(attributeValue))
        {
            try {
                Resources r = context.getResources();
                int drawableId = r.getIdentifier(attributeValue, "drawable", context.getPackageName());
                Drawable drawable = r.getDrawable(drawableId);
                setDrawable(view,drawable);
            }
            catch (Exception ex)
            {
                System.out.println("Could not load local resource " +attributeValue);
            }
        } else if(URLUtil.isValidUrl(attributeValue)) {
            NetworkDrawableHelper.DrawableCallback callback = new NetworkDrawableHelper.DrawableCallback() {
                @Override
                public void onDrawableLoad(String url, final Drawable drawable) {
                    setDrawable(view,drawable);
                }

                @Override
                public void onDrawableError(String url, String reason) {
                    System.out.println("Could not load " + url + " : " + reason);
                }
            };
            new NetworkDrawableHelper(context, view, imageLoader,requestQueue, attributeValue, synchronousRendering, callback);
        }

    }

    public abstract void setDrawable(T view, Drawable drawable);

}
