package com.flipkart.layoutengine;

import com.flipkart.layoutengine.builder.LayoutBuilder;

/**
 * @author kirankumar
 */
public class ParserContext implements Cloneable {

    private LayoutBuilder layoutBuilder;
    private DataContext dataContext;

    public ParserContext() {
        this.dataContext = new DataContext(null, null, null, null, 0);
    }

    public LayoutBuilder getLayoutBuilder() {
        return layoutBuilder;
    }

    public void setLayoutBuilder(LayoutBuilder layoutBuilder) {
        this.layoutBuilder = layoutBuilder;
    }

    public DataContext getDataContext() {
        return dataContext;
    }

    public void setDataContext(DataContext dataContext) {
        this.dataContext = dataContext;
    }

    @Override
    public ParserContext clone() {
        ParserContext context = null;
        try {
            context = (ParserContext) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return context;
    }
}
