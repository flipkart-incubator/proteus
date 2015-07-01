package com.flipkart.layoutengine.binding;

import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;

/**
 * Created by kirankumar on 23/07/14.
 */
public class Binding {
    private ParserContext parserContext;
    private String attributeKey;
    private String attributeValue;
    private View view;
    private ViewGroup parentView;

    public Binding(ParserContext parserContext, String attributeKey, String attributeValue, View view, ViewGroup parentView)
    {
        this.parserContext = parserContext;
        this.attributeKey = attributeKey;
        this.attributeValue = attributeValue;
        this.view = view;
        this.parentView = parentView;
    }

    public ParserContext getParserContext() {
        return parserContext;
    }

    public void setParserContext(ParserContext parserContext) {
        this.parserContext = parserContext;
    }

    public String getAttributeKey() {
        return attributeKey;
    }

    public void setAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public ViewGroup getParentView() {
        return parentView;
    }

    public void setParentView(ViewGroup parentView) {
        this.parentView = parentView;
    }
}
