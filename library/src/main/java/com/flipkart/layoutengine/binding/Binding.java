package com.flipkart.layoutengine.binding;

import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.LayoutHandler;

/**
 * Created by kirankumar on 23/07/14.
 */
public class Binding {
    private int index;
    private ParserContext parserContext;
    private LayoutHandler layoutHandler;
    private String attributeKey;
    private String attributeValue;
    private View view;
    private ViewGroup parentView;

    public Binding(ParserContext parserContext, LayoutHandler layoutHandler, String attributeKey, String attributeValue, View view, ViewGroup parentView, int index) {
        this.parserContext = parserContext.clone();
        this.layoutHandler = layoutHandler;
        this.attributeKey = attributeKey;
        this.attributeValue = attributeValue;
        this.view = view;
        this.parentView = parentView;
        this.index = index;
    }

    public ParserContext getParserContext() {
        return parserContext;
    }

    public void setParserContext(ParserContext parserContext) {
        this.parserContext = parserContext;
    }

    public LayoutHandler getLayoutHandler() {
        return this.layoutHandler;
    }

    public int getIndex() {
        return this.index;
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
