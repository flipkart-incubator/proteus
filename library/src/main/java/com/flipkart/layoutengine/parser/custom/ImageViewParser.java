package com.flipkart.layoutengine.parser.custom;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.flipkart.layoutengine.parser.ViewParser;
import com.android.volley.toolbox.NetworkImageView;
import com.flipkart.layoutengine.toolbox.VolleySingleton;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class ImageViewParser extends ViewParser<NetworkImageView> {
    public ImageViewParser(Class<View> viewClass) {
        super(viewClass);
    }

    public ImageViewParser() {
        super(NetworkImageView.class);
    }

    @Override
    protected void prepareHandlers(final Activity activity) {
        super.prepareHandlers(activity);
        addHandler("imageUrl", new AttributeProcessor<NetworkImageView>() {
            @Override
            public void handle(String attributeKey, String attributeValue, NetworkImageView view) {
                view.setImageUrl(attributeValue, VolleySingleton.getInstance(activity.getApplicationContext()).getImageLoader());
            }
        });

        addHandler("scaleType",new AttributeProcessor<NetworkImageView>() {
            @Override
            public void handle(String attributeKey, String attributeValue, NetworkImageView view) {
                ImageView.ScaleType scaleType = null;
                if("center".equals(attributeValue))
                {
                    scaleType = ImageView.ScaleType.CENTER;
                }

            }
        });

        addHandler("aspectRatio", new AttributeProcessor<NetworkImageView>() {
            @Override
            public void handle(String attributeKey, String attributeValue, NetworkImageView view) {
                if("preserve".equals(attributeValue))
                {
                    view.setAdjustViewBounds(true);
                }
                else
                {
                    view.setAdjustViewBounds(false);
                }
            }
        });
    }
}
