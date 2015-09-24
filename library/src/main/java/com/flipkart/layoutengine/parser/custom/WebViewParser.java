package com.flipkart.layoutengine.parser.custom;

import android.content.Context;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;
import com.google.gson.JsonObject;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class WebViewParser<T extends android.webkit.WebView> extends WrappableParser<T> {
    public WebViewParser(Parser<T> wrappedParser) {
        super(android.webkit.WebView.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(final Context context) {
        super.prepareHandlers(context);
        addHandler(Attributes.WebView.Url, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, JsonObject layout) {
                view.loadUrl(attributeValue);
            }
        });
        addHandler(Attributes.WebView.HTML, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, JsonObject layout) {
                view.loadData(attributeValue, "text/html", "UTF-8");
            }
        });
    }
}
