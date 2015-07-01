package com.flipkart.layoutengine;

import com.flipkart.layoutengine.builder.LayoutBuilder;
import com.flipkart.layoutengine.provider.Provider;

/**
 * Created by kirankumar on 02/07/14.
 */
public class ParserContext implements Cloneable {

    private Provider dataProvider;

    private LayoutBuilder layoutBuilder;

    public Provider getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(Provider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public ParserContext clone(){
        ParserContext context = null;
        try {
            context = (ParserContext) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return context;
    }


    public LayoutBuilder getLayoutBuilder() {
        return layoutBuilder;
    }

    public void setLayoutBuilder(LayoutBuilder layoutBuilder) {
        this.layoutBuilder = layoutBuilder;
    }
}
