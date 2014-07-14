package com.flipkart.layoutengine;

import com.flipkart.layoutengine.provider.Provider;

/**
 * Created by kirankumar on 02/07/14.
 */
public class ParserContext implements Cloneable {

    private Provider dataProvider;

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

}
