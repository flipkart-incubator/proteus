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
 * associated with a {@link android.view.View} built using a
 * {@link com.flipkart.layoutengine.builder.LayoutBuilder}.
 *
 * @author Aditya Sharat {@literal <aditya.sharat@flipkart.com>}
 */
public class DataProteusView extends SimpleProteusView {

    //private static final String TAG = DataProteusView.class.getSimpleName();
    private static final Character PREFIX = DataParsingAdapter.PREFIX;
    private boolean isViewUpdating = false;

    /**
     * This Map holds a references to the {@link com.flipkart.layoutengine.binding.Binding} between
     * the view and data. This map is used to update the data associated with the
     * {@link android.view.View}
     * example:
     * <pre>
     * {@literal <}"$product.name", bindingObjectOfThisProperty{@literal >}
     * </pre>
     */
    protected ArrayList<Binding> bindings;

    public DataProteusView(ProteusView proteusView) {
        super(proteusView.getView());
        addChildren(proteusView.getChildren());
    }

    public void addBinding(Binding binding) {
        if (this.bindings == null) {
            this.bindings = new ArrayList<>();
        }
        this.bindings.add(binding);
    }

    @Override
    protected View updateViewImpl(JsonObject data) {
        this.isViewUpdating = true;
        if (this.bindings != null) {
            for (Binding binding : this.bindings) {
                this.handleBinding(binding, data);
            }
        }

        if (getChildren() != null) {
            for (ProteusView proteusView : getChildren()) {
                proteusView.updateView(data);
            }
        }

        this.isViewUpdating = false;
        return this.getView();
    }

    /**
     * Updates the Binding with new data. It uses a {@link com.flipkart.layoutengine.binding.Binding}
     * to get the associated {@link android.view.View}, {@link com.flipkart.layoutengine.builder.LayoutBuilder},
     * and {@link com.flipkart.layoutengine.parser.LayoutHandler} to update the value of the bound
     * attribute with the new value fetched from the new data object passed.
     *
     * @param binding The property name to update mapped to its
     *                {@link com.flipkart.layoutengine.binding.Binding}
     */
    private void handleBinding(Binding binding, JsonObject data) {
        JsonObject temp = new JsonObject();
        temp.addProperty("value", binding.getBindingName());
        JsonElement dataAttribute = temp.get("value");

        ParserContext context = binding.getParserContext();

        int index = binding.getIndex();

        setDataProvider(context, data, binding.getDataContext(), index);

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

    protected void setDataProvider(ParserContext context, JsonObject data, String dataContext, int childIndex) {
        if (dataContext == null) {
            context.setDataProvider(new GsonProvider(data));
            context.setDataContext(null);
        } else {
            JsonElement newData = getElementFromData(data, context.getDataProvider(), childIndex);
            context.setDataProvider(new GsonProvider(newData));
            context.setDataContext(dataContext);
        }
    }

    public JsonElement getElementFromData(JsonElement element, Provider dataProvider, int childIndex) {
        return Utils.getElementFromData(PREFIX, element, dataProvider, childIndex);
    }

    public boolean isViewUpdating() {
        return isViewUpdating;
    }
}
