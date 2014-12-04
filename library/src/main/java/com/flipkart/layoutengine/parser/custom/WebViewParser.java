package com.flipkart.layoutengine.parser.custom;

import android.webkit.WebView;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.AttributeProcessor;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class WebViewParser<T extends WebView> extends WrappableParser<T> {
    public WebViewParser(Parser<T> wrappedParser) {
        super(WebView.class, wrappedParser);
    }

    @Override
    protected void addHandler(Attributes.Attribute key, AttributeProcessor<T> handler) {
        super.addHandler(key, handler);
        addHandler(Attributes.Webview.Url,new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.loadUrl(attributeValue);
            }
        });
    }


}
