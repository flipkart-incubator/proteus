package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.DrawableResourceProcessor;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class NetworkImageViewParser<T extends ImageView> extends WrappableParser<T> {
    private static final String TAG = NetworkImageViewParser.class.getSimpleName();


    public NetworkImageViewParser(Parser<T> wrappedParser) {
        super(ImageView.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(final Context context) {
        super.prepareHandlers(context);
        addHandler(Attributes.NetworkImageView.ImageUrl, new DrawableResourceProcessor<T>(context) {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                view.setImageDrawable(drawable);
            }
        });
    }
 }