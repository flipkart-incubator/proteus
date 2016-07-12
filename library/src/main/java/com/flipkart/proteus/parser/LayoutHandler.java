package com.flipkart.proteus.parser;

import android.view.View;

import com.flipkart.proteus.processor.AttributeProcessor;
import com.flipkart.proteus.toolbox.Styles;
import com.flipkart.proteus.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author kiran.kumar
 */
public interface LayoutHandler<V extends View> {

    void prepareAttributeHandlers();

    void onBeforeCreateView(View parent, JsonObject layout, JsonObject data, int index, Styles styles);

    V createView(View parent, JsonObject layout, JsonObject data, int index, Styles styles);

    void onAfterCreateView(V view, JsonObject layout, JsonObject data, int index, Styles styles);

    void addHandler(Attributes.Attribute key, AttributeProcessor<V> handler);

    boolean handleAttribute(V view, String attribute, JsonElement value);

    boolean handleChildren(ProteusView view);

    boolean addView(ProteusView parent, ProteusView view);
}
