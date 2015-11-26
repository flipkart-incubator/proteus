package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.GifDrawableResourceProcessor;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by himanshu.neema on 24/11/15.
 */
public class GifImageViewParser<T extends GifImageView> extends WrappableParser<T> {
    public GifImageViewParser(Parser<T> wrappedParser) {
        super(GifImageView.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(Context context) {
        super.prepareHandlers(context);

        addHandler(Attributes.GifImageView.Background, new GifDrawableResourceProcessor<T>(context) {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    //noinspection deprecation
                    view.setBackgroundDrawable(drawable);
                } else {
                    view.setBackground(drawable);
                }
            }
        });

        addHandler(Attributes.GifImageView.Src, new GifDrawableResourceProcessor<T>(context) {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                view.setImageDrawable(drawable);
            }
        });

    }
}
