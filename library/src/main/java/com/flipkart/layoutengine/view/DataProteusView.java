package com.flipkart.layoutengine.view;

import android.view.View;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.binding.Binding;
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

    private boolean isViewUpdating = false;

    /**
     * This Array holds a references to the {@link com.flipkart.layoutengine.binding.Binding} of
     * this {@link DataProteusView}. This array is used to update the data associated with the
     * {@link android.view.View}
     */
    private ArrayList<Binding> bindings;
    private ParserContext parserContext;

    public DataProteusView(ProteusView proteusView) {
        super(proteusView.getView(), proteusView.getIndex());
        this.children = proteusView.getChildren();
        if (proteusView instanceof DataProteusView) {
            parserContext = ((DataProteusView) proteusView).getParserContext();
        }
    }

    @Override
    public void replaceView(ProteusView view) {
        if (view instanceof DataProteusView) {
            DataProteusView dataProteusView = (DataProteusView) view;
            this.bindings = dataProteusView.getBindings();
        }
        super.replaceView(view);
    }

    public void addBinding(Binding binding) {
        if (this.bindings == null) {
            this.bindings = new ArrayList<>();
        }
        this.bindings.add(binding);
    }

    @Override
    protected View updateDataImpl(JsonObject data) {
        this.isViewUpdating = true;
        updateParserContext(data);
        if (this.bindings != null) {
            for (Binding binding : this.bindings) {
                this.handleBinding(binding);
            }
        }

        if (getChildren() != null) {
            for (ProteusView proteusView : getChildren()) {
                proteusView.updateData(data);
            }
        }
        this.isViewUpdating = false;
        return this.getView();
    }

    private void updateParserContext(JsonObject data) {

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
    private void handleBinding(Binding binding) {

        if (binding.hasRegEx()) {
            parserContext.getLayoutBuilder().handleAttribute(
                    binding.getLayoutHandler(),
                    parserContext,
                    binding.getAttributeKey(),
                    null,
                    Utils.getStringAsJsonElement(binding.getAttributeValue()),
                    this,
                    binding.getParentView(),
                    index);
        } else {
            JsonElement dataValue = getElementFromData(binding.getBindingName(), parserContext.getDataProvider(), index);
            parserContext.getLayoutBuilder().handleAttribute(
                    binding.getLayoutHandler(),
                    parserContext,
                    binding.getAttributeKey(),
                    null,
                    dataValue,
                    this,
                    binding.getParentView(),
                    index);
        }

    }

    public boolean isViewUpdating() {
        return isViewUpdating;
    }

    public ArrayList<Binding> getBindings() {
        return bindings;
    }

    public void setParserContext(ParserContext parserContext) {
        this.parserContext = parserContext;
    }

    public ParserContext getParserContext() {
        return parserContext;
    }

    public JsonElement get(String dataPath, int childIndex) {
        return getElementFromData(dataPath, parserContext.getDataProvider(), childIndex);
    }

    public void set(String dataPath, JsonElement newValue, int childIndex) {
        if (dataPath == null) {
            return;
        }
        JsonElement parent = getElementFromData(dataPath.substring(0, dataPath.lastIndexOf(".")), parserContext.getDataProvider(), childIndex);
        if (!parent.isJsonObject()) {
            return;
        }
        String propertyName = dataPath.substring(dataPath.lastIndexOf(".") + 1, dataPath.length());
        parent.getAsJsonObject().add(propertyName, newValue);

        updateView(dataPath);
    }

    public void set(String dataPath, String newValue, int childIndex) {
        set(dataPath, Utils.getStringAsJsonElement(newValue), childIndex);
    }

    public void set(String dataPath, Number newValue, int childIndex) {
        set(dataPath, Utils.getNumberAsJsonElement(newValue), childIndex);
    }

    public void set(String dataPath, boolean newValue, int childIndex) {
        set(dataPath, Utils.getBooleanAsJsonElement(newValue), childIndex);
    }

    private void updateView(String dataPath) {
        this.isViewUpdating = true;
        if (this.bindings != null) {
            for (Binding binding : this.bindings) {
                if (binding.getBindingName().equals(dataPath)) {
                    this.handleBinding(binding);
                }
            }
        }

        if (getChildren() != null) {
            for (ProteusView proteusView : getChildren()) {
                ((DataProteusView) proteusView).updateView(dataPath);
            }
        }

        this.isViewUpdating = false;
    }

    private JsonElement getElementFromData(String element, Provider dataProvider, int childIndex) {
        return Utils.getElementFromData(element, dataProvider, childIndex);
    }
}
