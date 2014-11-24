package com.flipkart.layoutengine;

import com.flipkart.layoutengine.builder.SimpleLayoutBuilder;
import com.flipkart.layoutengine.provider.Provider;

/**
 * Created by kirankumar on 02/07/14.
 */
public class ParserContext implements Cloneable {

    private Provider dataProvider;

    private SimpleLayoutBuilder layoutBuilder;

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


    public SimpleLayoutBuilder getLayoutBuilder() {
        return layoutBuilder;
    }

    public void setLayoutBuilder(SimpleLayoutBuilder layoutBuilder) {
        this.layoutBuilder = layoutBuilder;
    }
}
