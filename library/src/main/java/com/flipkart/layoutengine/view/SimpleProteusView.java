package com.flipkart.layoutengine.view;

import android.util.Log;
import android.view.View;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.binding.Binding;
import com.flipkart.layoutengine.provider.GsonProvider;
import com.google.gson.JsonElement;

import java.util.Map;

/**
 * A {@link ProteusView} implementation to update the data
 * associated with a {@link android.view.View} built using a {@link com.flipkart.layoutengine.builder.LayoutBuilder}.
 *
 * @author Aditya Sharat {@literal <aditya.sharat@flipkart.com>}
 */
public class SimpleProteusView implements ProteusView {

    private Map<String, Binding> mapOfBindings;
    private View view;

    public SimpleProteusView(View view, Map<String, Binding> mapOfBindings) {
        this.view = view;
        this.mapOfBindings = mapOfBindings;
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
        ParserContext context = binding.getParserContext();

        context.setDataProvider(new GsonProvider(data));

        binding.getParserContext().getLayoutBuilder().handleAttribute(
                binding.getLayoutHandler(),
                context,
                binding.getAttributeKey(),
                null,
                data,
                binding.getView(),
                binding.getParentView(),
                binding.getIndex());
    }
}
