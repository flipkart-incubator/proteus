package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.net.Uri;
import android.widget.VideoView;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonObject;

/**
 * Created by himanshu.neema on 23/11/15.
 */
public class VideoViewParser<T extends VideoView> extends WrappableParser<T> {

    public VideoViewParser(Parser<T> wrappedParser) {
        super(VideoView.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(Context context) {
        super.prepareHandlers(context);

        addHandler(Attributes.VideoView.Uri, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                view.setVideoURI(Uri.parse(attributeValue));
            }
        });
    }
}
