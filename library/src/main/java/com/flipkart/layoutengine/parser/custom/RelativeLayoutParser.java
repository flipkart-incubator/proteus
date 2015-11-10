package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.widget.RelativeLayout;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonObject;

/**
 * Created by kirankumar on 10/07/14.
 */
public class RelativeLayoutParser<T extends RelativeLayout> extends WrappableParser<T> {

    public RelativeLayoutParser(Parser<T> parentParser) {
        super(RelativeLayout.class, parentParser);
    }

    @Override
    protected void prepareHandlers(Context context) {
        super.prepareHandlers(context);
        addHandler(Attributes.View.Gravity, new StringAttributeProcessor<T>() {

            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                view.setGravity(ParseHelper.parseGravity(attributeValue));
            }
        });
    }
}
