package com.flipkart.layoutengine.view;

import android.util.Log;
import android.view.View;

import com.flipkart.layoutengine.DataContext;
import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.binding.Binding;
import com.flipkart.layoutengine.builder.DataParsingLayoutBuilder;
import com.flipkart.layoutengine.builder.LayoutBuilderFactory;
import com.flipkart.layoutengine.exceptions.InvalidDataPathException;
import com.flipkart.layoutengine.exceptions.JsonNullException;
import com.flipkart.layoutengine.exceptions.NoSuchDataPathException;
import com.flipkart.layoutengine.toolbox.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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
    private String dataPathForChildren;
    private JsonObject childLayout;

    /**
     * This Array holds a to the {@link Binding}s of this {@link DataProteusView}.
     */
    private ArrayList<Binding> bindings;
    private ParserContext parserContext;

    public DataProteusView(ProteusView proteusView) {
        super(proteusView.getView(), proteusView.getLayout(), proteusView.getIndex(),
                proteusView.getChildren(), proteusView.getParent());

        if (proteusView instanceof DataProteusView) {
            DataProteusView dataProteusView = (DataProteusView) proteusView;
            parserContext = dataProteusView.getParserContext();
            bindings = dataProteusView.getBindings();
            dataPathForChildren = dataProteusView.getDataPathForChildren();
            childLayout = dataProteusView.getChildLayout();
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
        JsonObject copyOfData = null;

        if (data != null) {
            copyOfData = LayoutBuilderFactory.GSON.fromJson(data.toString(), JsonObject.class);
        }

        // update the data context so all child views can refer to new data
        updateDataContext(copyOfData);

        // update the bindings of this view
        if (this.bindings != null) {
            for (Binding binding : this.bindings) {
                this.handleBinding(binding);
            }
        }

        // update the child views
        if (dataPathForChildren != null) {
            if (children == null) {
                children = new ArrayList<>();
            }
            updateChildrenFromData();
        } else if (children != null) {
            for (ProteusView proteusView : children) {
                proteusView.updateData(null);
            }
        }

        this.isViewUpdating = false;
        return this.getView();
    }

    private void updateDataContext(JsonObject newData) {

        DataContext dataContext = parserContext.getDataContext();

        if (newData != null) {
            JsonObject oldData = parserContext.getDataContext().getDataProvider().getData().getAsJsonObject();
            Utils.merge(oldData, newData);
        }

        if (dataContext.isDataContextFailed()) {
            DataParsingLayoutBuilder layoutBuilder = (DataParsingLayoutBuilder) parserContext.getLayoutBuilder();
            JsonObject scope = LayoutBuilderFactory.GSON.fromJson(
                    LayoutBuilderFactory.GSON.toJson(dataContext.getScope()), JsonObject.class);
            DataContext newDataContext = layoutBuilder.getNewDataContext(scope, dataContext, index, newData);

            parserContext.setDataContext(newDataContext);
        }
    }

    private void updateChildrenFromData() {
        JsonArray childrenDataArray = new JsonArray();
        try {
            childrenDataArray = Utils.getElementFromData(dataPathForChildren,
                    parserContext.getDataContext().getDataProvider(), index).getAsJsonArray();
        } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException | IllegalStateException e) {
            Log.e(TAG + "#updateChildrenFromData()", e.getMessage());
        }

        if (children.size() > childrenDataArray.size()) {
            while (children.size() > childrenDataArray.size()) {
                ProteusView proteusView = children.remove(children.size() - 1);
                proteusView.removeView();
            }
        }

        for (int index = 0; index < childrenDataArray.size(); index++) {
            if (index < children.size()) {
                children.get(index).updateData(null);
            } else {
                if (childLayout != null) {
                    DataProteusView proteusView = (DataProteusView) parserContext.getLayoutBuilder().build(view,
                            childLayout,
                            parserContext.getDataContext().getDataProvider().getData().getAsJsonObject(),
                            index, styles);
                    addChild(proteusView);
                }
            }
        }
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
                    layout,
                    new JsonPrimitive(binding.getAttributeValue()),
                    this,
                    parent,
                    index);
        } else {
            JsonElement dataValue;
            try {
                dataValue = Utils.getElementFromData(binding.getBindingName(),
                        parserContext.getDataContext().getDataProvider(), index);
            } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException e) {
                Log.e(TAG + "#handleBinding()", e.getMessage());
                if (getView() != null) {
                    getView().setVisibility(View.GONE);
                }
                return;
            }
            parserContext.getLayoutBuilder().handleAttribute(
                    binding.getLayoutHandler(),
                    parserContext,
                    binding.getAttributeKey(),
                    layout,
                    dataValue,
                    this,
                    parent,
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
            parent = Utils.getElementFromData(aliasedDataPath.substring(0, aliasedDataPath.lastIndexOf(".")),
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
        set(dataPath, new JsonPrimitive(newValue), childIndex);
    }

    public void set(String dataPath, Number newValue, int childIndex) {
        set(dataPath, new JsonPrimitive(newValue), childIndex);
    }

    public void set(String dataPath, boolean newValue, int childIndex) {
        set(dataPath, new JsonPrimitive(newValue), childIndex);
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

    public String getDataPathForChildren() {
        return dataPathForChildren;
    }

    public void setDataPathForChildren(String dataPathForChildren) {
        this.dataPathForChildren = dataPathForChildren;
    }

    public void setChildLayout(JsonObject childLayout) {
        this.childLayout = childLayout;
    }

    public JsonObject getChildLayout() {
        return childLayout;
    }
}
