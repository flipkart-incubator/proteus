package com.flipkart.layoutengine.binding;

import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.view.DataProteusView;
import com.flipkart.layoutengine.view.ProteusView;

/**
 * @author kirankumar
 */
public class Binding {
    private int index;
    private ParserContext parserContext;
    private LayoutHandler layoutHandler;
    private String bindingName;
    private String attributeKey;
    private String attributeValue;
    private ProteusView<View> proteusView;
    private ViewGroup parentView;
    private boolean hasRegEx;

    public Binding(ParserContext parserContext, LayoutHandler layoutHandler, String bindingName,
                   String attributeKey, String attributeValue, DataProteusView view,
                   ViewGroup parentView, int index, boolean hasRegEx) {
        this.parserContext = parserContext.clone();
        this.layoutHandler = layoutHandler;
        this.bindingName = bindingName;
        this.attributeKey = attributeKey;
        this.attributeValue = attributeValue;
        this.proteusView = view;
        this.parentView = parentView;
        this.index = index;
        this.hasRegEx = hasRegEx;
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

    public ProteusView<View> getProteusView() {
        return proteusView;
    }

    public void setProteusView(ProteusView<View> proteusView) {
        this.proteusView = proteusView;
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

    public String getBindingName() {
        return bindingName;
    }

    public void setBindingName(String bindingName) {
        this.bindingName = bindingName;
    }

    public boolean hasRegEx() {
        return hasRegEx;
    }

    public void hasRegEx(boolean hasRegEx) {
        this.hasRegEx = hasRegEx;
    }
}
