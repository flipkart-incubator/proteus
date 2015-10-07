package com.flipkart.layoutengine.processor;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author kirankumar
 * @author aditya.sharat
 */
public abstract class StringAttributeProcessor<E> extends AttributeProcessor<E> {
    /**
     * @param parserContext  ParserContext
     * @param attributeKey   Attribute Key
     * @param attributeValue Attribute Value
     * @param view           View
     * @param proteusView    ProteusView
     * @param parent         Parent ProteusView
     * @param layout         Layout
     * @param index          index
     */
    @Override
    public void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue,
                       E view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
        if (attributeValue.isJsonPrimitive()) {
            handle(parserContext, attributeKey, attributeValue.getAsString(), view, proteusView, parent, layout, index);
        } else {
            handle(parserContext, attributeKey, attributeValue.toString(), view, proteusView, parent, layout, index);
        }
    }

    /**
     * @param parserContext  ParserContext
     * @param attributeKey   Attribute Key
     * @param attributeValue Attribute Value
     * @param view           View
     * @param proteusView    ProteusView
     * @param parent         Parent ProteusView
     * @param layout         Layout
     * @param index          index
     */
    public abstract void handle(ParserContext parserContext, String attributeKey, String attributeValue,
                                E view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index);

}
