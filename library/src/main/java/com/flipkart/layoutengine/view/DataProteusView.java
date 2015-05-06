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
    private GsonProvider dataProvider;

    public DataProteusView(ProteusView proteusView) {
        super(proteusView.getView());
        this.children = proteusView.getChildren();
        if (proteusView instanceof DataProteusView) {
            dataProvider = ((DataProteusView)proteusView).getDataProvider();
        }
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

        JsonElement dataAttribute = Utils.getStringAsJsonElement(binding.getBindingName());

        ParserContext context = binding.getParserContext();

        int index = binding.getIndex();

        context = setCorrectDataProvider(context, data, index);

        JsonElement dataValue = getElementFromData(dataAttribute, context.getDataProvider(), index);

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

    private ParserContext setCorrectDataProvider(ParserContext context, JsonObject data, int childIndex) {
        if (context.getDataContext() != null) {
            JsonElement newData = getElementFromData(data, context.getDataProvider(), childIndex);
            context.setDataProvider(new GsonProvider(newData));
            context.setDataContext(context.getDataContext());
        } else {
            context.getDataProvider().setRoot(data);
            context.setDataContext(null);
        }
        return context;
    }

    public JsonElement getElementFromData(JsonElement element, Provider dataProvider, int childIndex) {
        return Utils.getElementFromData(PREFIX, element, dataProvider, childIndex);
    }

    public boolean isViewUpdating() {
        return isViewUpdating;
    }

    public void setDataProvider(Provider dataProvider) {
        this.dataProvider = (GsonProvider) dataProvider;
    }

    public GsonProvider getDataProvider() {
        return dataProvider;
    }

    public JsonElement get(String dataPath, int childIndex) {
        JsonElement jsonDataPath = Utils.getStringAsJsonElement(dataPath);
        getElementFromData(jsonDataPath, dataProvider, childIndex);
        return null;
    }

    public void set(String dataPath, String newValue, int childIndex) {
        if (dataPath == null) {
            return;
        }
        JsonElement jsonParentDataPath = Utils.getStringAsJsonElement(dataPath.substring(0, dataPath.lastIndexOf(".")));
        JsonElement parent = getElementFromData(jsonParentDataPath, dataProvider, childIndex);
        if (!parent.isJsonObject()) {
            return;
        }
        String propertyName = dataPath.substring(dataPath.lastIndexOf(".") + 1, dataPath.length());
        parent.getAsJsonObject().add(propertyName, Utils.getStringAsJsonElement(newValue));

        updateView(dataPath);
    }

    public void set(String dataPath, Number newValue, int childIndex) {
        if (dataPath == null) {
            return;
        }
        JsonElement jsonParentDataPath = Utils.getStringAsJsonElement(dataPath.substring(0, dataPath.lastIndexOf(".")));
        JsonElement parent = getElementFromData(jsonParentDataPath, dataProvider, childIndex);
        if (!parent.isJsonObject()) {
            return;
        }
        String propertyName = dataPath.substring(dataPath.lastIndexOf(".") + 1, dataPath.length());
        parent.getAsJsonObject().add(propertyName, Utils.getNumberAsJsonElement(newValue));

        updateView(dataPath);
    }

    public void set(String dataPath, boolean newValue, int childIndex) {
        if (dataPath == null) {
            return;
        }
        JsonElement jsonParentDataPath = Utils.getStringAsJsonElement(dataPath.substring(0, dataPath.lastIndexOf(".")));
        JsonElement parent = getElementFromData(jsonParentDataPath, dataProvider, childIndex);
        if (!parent.isJsonObject()) {
            return;
        }
        String propertyName = dataPath.substring(dataPath.lastIndexOf(".") + 1, dataPath.length());
        parent.getAsJsonObject().add(propertyName, Utils.getBooleanAsJsonElement(newValue));

        updateView(dataPath);
    }

    private void updateView(String dataPath) {
        this.isViewUpdating = true;
        if (this.bindings != null) {
            for (Binding binding : this.bindings) {
                if (binding.getBindingName().equals(dataPath)) {
                    this.handleBinding(binding, dataProvider.getRoot().getAsJsonObject());
                }
            }
        }

        if (getChildren() != null) {
            for (ProteusView proteusView : getChildren()) {
                ((DataProteusView)proteusView).updateView(dataPath);
            }
        }

        this.isViewUpdating = false;
    }
}
