package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.AttributeProcessor;


/**
 * Created by kiran.kumar on 12/05/14.
 */
public class ImageViewParser<T extends ImageView> extends WrappableParser<T> {
    private static final String TAG = ImageViewParser.class.getSimpleName();

    public ImageViewParser(Parser<T> parentParser)
    {
        super(ImageView.class,parentParser);
    }

    @Override
    protected void prepareHandlers(final Context context) {
        super.prepareHandlers(context);


        addHandler(Attributes.ImageView.Src, new AttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                try {
                    Resources r = context.getResources();
                    int drawableId = r.getIdentifier(attributeValue, "drawable", context.getPackageName());
                    Drawable drawable = r.getDrawable(drawableId);
                    view.setImageDrawable(drawable);
                }
                catch (Exception ex)
                {
                    Log.e(TAG,"exception while parsing image attribute. "+ex);
                }
            }
        });

        addHandler(Attributes.ImageView.ScaleType,new AttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                ImageView.ScaleType scaleType = null;
                if("center".equals(attributeValue))
                {
                    scaleType = ImageView.ScaleType.CENTER;
                }

            }
        });

        addHandler(Attributes.ImageView.AdjustViewBounds, new AttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                if("true".equals(attributeValue))
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
