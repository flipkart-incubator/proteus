package com.flipkart.layoutengine.parser.custom;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.flipkart.layoutengine.parser.ViewParser;


/**
 * Created by kiran.kumar on 12/05/14.
 */
public class ImageViewParser<T extends ImageView> extends ViewParser<T> {
    private static final String TAG = ImageViewParser.class.getSimpleName();

    public ImageViewParser(Class viewClass) {
        super(viewClass);
    }

    public ImageViewParser() {
        super(ImageView.class);
    }

    @Override
    protected void prepareHandlers(final Activity activity) {
        super.prepareHandlers(activity);


        addHandler("image", new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                try {
                    Resources r = activity.getResources();
                    int drawableId = r.getIdentifier(attributeValue, "drawable", activity.getPackageName());
                    Drawable drawable = r.getDrawable(drawableId);
                    view.setImageDrawable(drawable);
                }
                catch (Exception ex)
                {
                    Log.e(TAG,"exception while parsing image attribute. "+ex);
                }
            }
        });

        addHandler("scaleType",new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                ImageView.ScaleType scaleType = null;
                if("center".equals(attributeValue))
                {
                    scaleType = ImageView.ScaleType.CENTER;
                }

            }
        });

        addHandler("aspectRatio", new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
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
