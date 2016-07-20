package com.flipkart.layoutengine.processor;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author kirankumar
 * @author aditya.sharat
 */
public abstract class StringAttributeProcessor<E extends View> extends AttributeProcessor<E> {
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
            handle(parserContext, attributeKey, getStringFromAttribute(view, attributeValue.getAsString()), view, proteusView, parent, layout, index);
        } else {
            handle(parserContext, attributeKey, attributeValue.toString(), view, proteusView, parent, layout, index);
        }
    }

    private String getStringFromAttribute(E view, String attributeValue)
    {
        String result;
        if (ParseHelper.isLocalResourceAttribute(attributeValue)) {
            int attributeId = ParseHelper.getAttributeId(view.getContext(), attributeValue);
            if (0 != attributeId) {
                TypedArray ta = view.getContext().obtainStyledAttributes(new int[]{attributeId});
                result = ta.getString(0/* index */);
                ta.recycle();
            }
            else
            {
                result = "";
            }
        }
        else if (ParseHelper.isLocalDrawableResource(attributeValue)) {
            try {
                Resources r = view.getContext().getResources();
                int stringId = r.getIdentifier(attributeValue, "string", view.getContext().getPackageName());
                result = r.getString(stringId);
            } catch (Exception ex) {
                result = "";
                System.out.println("Could not load local resource " + attributeValue);
            }
        }
        else {
            result = attributeValue;
        }
        return result;
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
