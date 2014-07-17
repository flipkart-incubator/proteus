package com.flipkart.layoutengine.parser.custom;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.ViewParser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.toolbox.VolleySingleton;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class NetworkImageViewParser<T extends NetworkImageView> extends WrappableParser<T> {
    private static final String TAG = NetworkImageViewParser.class.getSimpleName();


    public NetworkImageViewParser(Parser<T> wrappedParser) {
        super(NetworkImageView.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(final Activity activity) {
        super.prepareHandlers(activity);
        addHandler("imageUrl", new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setImageUrl(attributeValue, VolleySingleton.getInstance(activity).getImageLoader());
            }
        });
    }
}
