package com.flipkart.android.proteus.processor;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.View;

import com.flipkart.android.proteus.parser.ParseHelper;
import com.google.gson.JsonElement;

/**
 * @author kirankumar
 * @author aditya.sharat
 */
public abstract class StringAttributeProcessor<V extends View> extends AttributeProcessor<V> {
    /**
     * @param view View
     */
    @Override
    public void handle(String key, JsonElement value, V view) {
        if (value.isJsonPrimitive()) {
            handle(key, getStringFromAttribute(view, value.getAsString()), view);
        } else {
            handle(key, value.toString(), view);
        }
    }

    private String getStringFromAttribute(V view, String attributeValue) {
        String result;
        if (ParseHelper.isLocalResourceAttribute(attributeValue)) {
            int attributeId = ParseHelper.getAttributeId(view.getContext(), attributeValue);
            if (0 != attributeId) {
                TypedArray ta = view.getContext().obtainStyledAttributes(new int[]{attributeId});
                result = ta.getString(0/* index */);
                ta.recycle();
            } else {
                result = "";
            }
        } else if (ParseHelper.isLocalDrawableResource(attributeValue)) {
            try {
                Resources r = view.getContext().getResources();
                int stringId = r.getIdentifier(attributeValue, "string", view.getContext().getPackageName());
                result = r.getString(stringId);
            } catch (Exception ex) {
                result = "";
                System.out.println("Could not load local resource " + attributeValue);
            }
        } else {
            result = attributeValue;
        }
        return result;
    }

    /**
     * @param attributeKey   Attribute Key
     * @param attributeValue Attribute Value
     * @param view           View
     */
    public abstract void handle(String attributeKey, String attributeValue, V view);

}
