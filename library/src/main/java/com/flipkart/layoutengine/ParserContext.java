package com.flipkart.layoutengine;

import com.flipkart.layoutengine.builder.LayoutBuilder;
import com.flipkart.layoutengine.toolbox.Styles;

/**
 * @author kirankumar
 */
public class ParserContext implements Cloneable {

    private LayoutBuilder layoutBuilder;
    private DataContext dataContext;
    private Styles styles;
    private boolean hasDataContext = true;

    public ParserContext() {
        this.dataContext = new DataContext();
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
            context.setStyles(styles);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return context;
    }

    public Styles getStyles() {
        return styles;
    }

    public void setStyles(Styles styles) {
        this.styles = styles;
    }

    public boolean hasDataContext() {
        return hasDataContext;
    }

    public void setHasDataContext(boolean hasDataContext) {
        this.hasDataContext = hasDataContext;
    }
}
