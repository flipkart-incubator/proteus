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

import java.util.ArrayList;

/**
 * A {@link ProteusView} implementation to update the data
 * associated with a {@link android.view.View} built using a {@link com.flipkart.layoutengine.builder.LayoutBuilder}.
 *
 * @author Aditya Sharat {@literal <aditya.sharat@flipkart.com>}
 */
public class DataProteusView extends SimpleProteusView {

    private static final Character PREFIX = DataParsingAdapter.PREFIX;
    //private static final String TAG = DataProteusView.class.getSimpleName();

    /**
     * This Map holds a references to the {@link com.flipkart.layoutengine.binding.Binding} between
     * the view and data. This map is used to update the data associated with the {@link android.view.View}
     * example:
     * <pre>
     * {@literal <}"$product.name", bindingObjectOfThisProperty{@literal >}
     * </pre>
     */
    protected ArrayList<Binding> bindings;

    public DataProteusView(ProteusView proteusView) {
        super(proteusView.getView());
    }

    public void addBinding(Binding binding) {
        if (this.bindings == null) {
            this.bindings = new ArrayList<>();
        }
        this.bindings.add(binding);
    }

    @Override
    protected View updateViewImpl(JsonObject data) {
        for (Binding binding : this.bindings) {
            this.handleBinding(binding, data);
        }
        return this.getView();
    }

    /**
     * Updates the Binding with new data. It uses a {@link com.flipkart.layoutengine.binding.Binding}
     * to get the associated {@link android.view.View}, {@link com.flipkart.layoutengine.builder.LayoutBuilder},
     * and {@link com.flipkart.layoutengine.parser.LayoutHandler} to update the value of the bound attribute with
     * the new value fetched from the new data object passed.
     *
     * @param binding The property name to update mapped to its {@link com.flipkart.layoutengine.binding.Binding}
     */
    private void handleBinding(Binding binding, JsonObject data) {
        JsonObject temp = new JsonObject();
        temp.addProperty("value", binding.getBindingName());

        JsonElement dataAttribute = temp.get("value");
        ParserContext context = binding.getParserContext();
        int index = binding.getIndex();

        context.setDataProvider(new GsonProvider(data));

        JsonElement dataValue = this.getElementFromData(dataAttribute, context.getDataProvider(), index);

        binding.getParserContext().getLayoutBuilder().handleAttribute(
                binding.getLayoutHandler(),
                context,
                binding.getAttributeKey(),
                null,
                dataValue,
                binding.getProteusView(),
                binding.getParentView(),
                index);
    }

    public JsonElement getElementFromData(JsonElement element, Provider dataProvider, int childIndex) {
        return Utils.getElementFromData(PREFIX, element, dataProvider, childIndex);
    }
}
