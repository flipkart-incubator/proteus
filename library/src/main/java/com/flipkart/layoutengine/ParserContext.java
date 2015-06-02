package com.flipkart.layoutengine;

import com.flipkart.layoutengine.builder.LayoutBuilder;
import com.flipkart.layoutengine.provider.Provider;
import com.google.gson.JsonObject;

/**
 * Created by kirankumar on 02/07/14.
 */
public class ParserContext implements Cloneable {

    private Provider dataProvider;
    private LayoutBuilder layoutBuilder;
    private JsonObject dataContext;

    public Provider getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(Provider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public LayoutBuilder getLayoutBuilder() {
        return layoutBuilder;
    }

    public void setLayoutBuilder(LayoutBuilder layoutBuilder) {
        this.layoutBuilder = layoutBuilder;
    }

    public JsonObject getDataContext() {
        return dataContext;
    }

    public void setDataContext(JsonObject dataContext) {
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
