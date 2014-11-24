package com.flipkart.layoutengine.parser.custom;

import android.content.Context;

import com.android.volley.toolbox.NetworkImageView;
import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.AttributeProcessor;
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
    protected void prepareHandlers(final Context context) {
        super.prepareHandlers(context);
        addHandler(Attributes.NetworkImageView.ImageUrl, new AttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setImageUrl(attributeValue, VolleySingleton.getInstance(context).getImageLoader());
            }
        });
    }
}
