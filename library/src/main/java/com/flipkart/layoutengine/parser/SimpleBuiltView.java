package com.flipkart.layoutengine.parser;

import android.util.Log;
import android.view.View;

import com.flipkart.layoutengine.binding.Binding;
import com.google.gson.JsonElement;

import java.util.Map;

/**
 * A {@link BuiltView} implementation to update the data
 * associated with a {@link android.view.View} built using a {@link com.flipkart.layoutengine.builder.LayoutBuilder}.
 *
 * @author Aditya Sharat {@literal <aditya.sharat@flipkart.com>}
 */
public class SimpleBuiltView implements BuiltView {

    private LayoutHandler layoutHandler;
    private Map<String, Binding> mapOfBindings;
    private View view;

    public SimpleBuiltView(View view, Map<String, Binding> mapOfBindings, LayoutHandler layoutHandler) {
        this.view = view;
        this.mapOfBindings = mapOfBindings;
        this.layoutHandler = layoutHandler;
    }

    @Override
    public View getView() {
        return this.view;
    }

    @Override
    public View updateView(JsonElement data) {

        for (Map.Entry<String, Binding> bindingEntry : this.mapOfBindings.entrySet()) {
            Log.d("bindings", bindingEntry.getKey() + "/" + bindingEntry.getValue());
            this.handleBinding(bindingEntry, data);
        }
        return this.getView();
    }

    private void handleBinding(Map.Entry<String, Binding> bindingEntry, JsonElement data) {
        String dataAttribute = bindingEntry.getKey();
        Binding binding = bindingEntry.getValue();

        this.layoutHandler.handleAttribute(binding.getParserContext(),
                binding.getAttributeKey(),
                null,
                data,
                binding.getParentView(),
                binding.getIndex());
    }
}
