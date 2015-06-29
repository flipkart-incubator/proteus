package com.flipkart.layoutengine.view;

import android.util.Log;
import android.view.View;

import com.flipkart.layoutengine.DataContext;
import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.binding.Binding;
import com.flipkart.layoutengine.exceptions.InvalidDataPathException;
import com.flipkart.layoutengine.exceptions.JsonNullException;
import com.flipkart.layoutengine.exceptions.NoSuchDataPathException;
import com.flipkart.layoutengine.provider.JsonProvider;
import com.flipkart.layoutengine.toolbox.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * A {@link ProteusView} implementation to update the data associated with a {@link android.view.View}
 * built using a {@link com.flipkart.layoutengine.builder.LayoutBuilder}.
 *
 * @author Aditya Sharat {@literal <aditya.sharat@flipkart.com>}
 */
public class DataProteusView extends SimpleProteusView {

    public static final String TAG = Utils.getTagPrefix() + DataProteusView.class.getSimpleName();
    private boolean isViewUpdating = false;

    /**
     * This Array holds a to the {@link Binding}s of this {@link DataProteusView}.
     */
    private ArrayList<Binding> bindings;
    private ParserContext parserContext;

    public DataProteusView(ProteusView proteusView) {
        super(proteusView.getView(), proteusView.getIndex(), proteusView.getParent());
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

        updateDataContext(data);

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

    private void updateDataContext(JsonObject newData) {
        JsonObject oldData = parserContext.getDataContext().getDataProvider().getData().getAsJsonObject();
        Utils.merge(oldData, newData);
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
            try {
                parserContext.getLayoutBuilder().handleAttribute(
                        binding.getLayoutHandler(),
                        parserContext,
                        binding.getAttributeKey(),
                        null,
                        Utils.getStringAsJsonElement(binding.getAttributeValue()),
                        this,
                        parent,
                        index);
            } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException e) {
                Log.e(TAG + "#handleBinding(regexp)", e.getMessage());
            }
        } else {
            JsonElement dataValue;
            try {
                dataValue = getElementFromData(binding.getBindingName(),
                        parserContext.getDataContext().getDataProvider(), index);
            } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException e) {
                Log.e(TAG + "#handleBinding()", e.getMessage());
                return;
            }
            try {
                parserContext.getLayoutBuilder().handleAttribute(
                        binding.getLayoutHandler(),
                        parserContext,
                        binding.getAttributeKey(),
                        null,
                        dataValue,
                        this,
                        parent,
                        index);
            } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException e) {
                Log.e(TAG + "#handleBinding()", e.getMessage());
            }
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
        return parserContext.getDataContext().get(dataPath, childIndex);
    }

    public void set(String dataPath, JsonElement newValue, int childIndex) {
        if (dataPath == null) {
            return;
        }

        String aliasedDataPath = DataContext.getAliasedDataPath(dataPath,
                parserContext.getDataContext().getReverseScopeMap(), true);

        JsonElement parent = null;
        try {
            parent = getElementFromData(aliasedDataPath.substring(0, aliasedDataPath.lastIndexOf(".")),
                    parserContext.getDataContext().getDataProvider(), childIndex);
        } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException e) {
            Log.e(TAG + "#set()", e.getMessage());
        }
        if (parent == null || !parent.isJsonObject()) {
            return;
        }

        String propertyName = aliasedDataPath.substring(aliasedDataPath.lastIndexOf(".") + 1, aliasedDataPath.length());
        parent.getAsJsonObject().add(propertyName, newValue);

        updateView(aliasedDataPath);
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
                DataProteusView dataProteusView = (DataProteusView) proteusView;
                String aliasedDataPath = DataContext.getAliasedDataPath(dataPath,
                        dataProteusView.getParserContext().getDataContext().getReverseScopeMap(), false);
                dataProteusView.updateView(aliasedDataPath);
            }
        }

        this.isViewUpdating = false;
    }

    private JsonElement getElementFromData(String dataPath, JsonProvider dataProvider, int childIndex)
            throws JsonNullException, NoSuchDataPathException, InvalidDataPathException {
        return Utils.getElementFromData(dataPath, dataProvider, childIndex);
    }
}
