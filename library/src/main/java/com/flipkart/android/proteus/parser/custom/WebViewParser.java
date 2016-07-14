package com.flipkart.android.proteus.parser.custom;

import com.flipkart.android.proteus.parser.Attributes;
import com.flipkart.android.proteus.parser.Parser;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.view.WebView;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class WebViewParser<T extends WebView> extends WrappableParser<T> {
    public WebViewParser(Parser<T> wrappedParser) {
        super(android.webkit.WebView.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers() {
        super.prepareHandlers();
        addHandler(Attributes.WebView.Url, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.loadUrl(attributeValue);
            }
        });
        addHandler(Attributes.WebView.HTML, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.loadData(attributeValue, "text/html", "UTF-8");
            }
        });
    }
}
