package com.flipkart.layoutengine.view;

import android.view.View;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.binding.Binding;
import com.flipkart.layoutengine.provider.DataParsingAdapter;
import com.flipkart.layoutengine.provider.GsonProvider;
import com.flipkart.layoutengine.provider.Provider;
import com.flipkart.layoutengine.toolbox.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * A {@link ProteusView} implementation to update the data
 * associated with a {@link android.view.View} built using a {@link com.flipkart.layoutengine.builder.LayoutBuilder}.
 *
 * @author Aditya Sharat {@literal <aditya.sharat@flipkart.com>}
 */
public class SimpleProteusView implements ProteusView {

    private static final Character PREFIX = DataParsingAdapter.PREFIX;
    //private static final String TAG = SimpleProteusView.class.getSimpleName();

    /**
     * Holds a map of Strings to {@link com.flipkart.layoutengine.binding.Binding}
     */
    private Map<String, Binding> bindings;
    private View view;

    public SimpleProteusView(View view, Map<String, Binding> bindings) {
        this.view = view;
        this.bindings = bindings;
    }

    @Override
    public View getView() {
        return this.view;
    }

    @Override
    public Map<String, Binding> getBindings() {
        return this.bindings;
    }

    @Override
    public View updateView(JsonObject data) {

        for (Map.Entry<String, Binding> bindingEntry : this.bindings.entrySet()) {
            this.handleBinding(bindingEntry, data);
        }
        return this.getView();
    }

    private void handleBinding(Map.Entry<String, Binding> bindingEntry, JsonObject data) {
        JsonObject temp = new JsonObject();
        temp.addProperty("value", bindingEntry.getKey());

        JsonElement dataAttribute = temp.get("value");
        Binding binding = bindingEntry.getValue();
        ParserContext context = binding.getParserContext();
        int index = binding.getIndex();

        context.setDataProvider(new GsonProvider(data));

        JsonElement dataValue = getElementFromData(dataAttribute, context.getDataProvider(), index);

        binding.getParserContext().getLayoutBuilder().handleAttribute(
                binding.getLayoutHandler(),
                context,
                binding.getAttributeKey(),
                null,
                dataValue,
                binding.getView(),
                binding.getParentView(),
                index);
    }

    public JsonElement getElementFromData(JsonElement element, Provider dataProvider, int childIndex) {
        return Utils.getElementFromData(PREFIX, element, dataProvider, childIndex);
    }
}
