package com.flipkart.android.proteus.providers;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.DataContext;
import com.flipkart.android.proteus.builder.LayoutBuilder;
import com.flipkart.android.proteus.exceptions.InvalidDataPathException;
import com.flipkart.android.proteus.exceptions.JsonNullException;
import com.flipkart.android.proteus.exceptions.NoSuchDataPathException;
import com.flipkart.android.proteus.parser.LayoutHandler;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Utils;
import com.flipkart.android.proteus.view.ProteusView;
import com.flipkart.android.proteus.view.manager.ProteusViewManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class LayoutImpl implements Layout {
    JsonObject layout;

    public LayoutImpl(JsonObject layout) {
        this.layout = layout;
    }

    @Override
    public String getType() {
        return Utils.getPropertyAsString(layout, ProteusConstants.TYPE);
    }

    @Override
    public String getLayoutIdentifier() {
        return Utils.getLayoutIdentifier(layout);
    }

    @Override
    public List<AttributeValuePair> getAttributes(LayoutHandler handler, ProteusView view) {
        return Utils.getAttributeValuePairList(handler, view, layout);
    }

    @Override
    public boolean has(String key) {
        return layout.has(key);
    }

    @Override
    public List<ProteusView> getChildrenProteusViews(LayoutBuilder layoutBuilder, ProteusView view, ProteusViewManager viewManager, JsonObject data) {
        return Utils.getChildrenProteusViews(layout, layoutBuilder, view, viewManager, data);
    }

    @Override
    public List<ProteusView> getChildrenProteusViews(ProteusView view, ProteusViewManager viewManager, LayoutHandler handler, LayoutBuilder builder) {
        List<ProteusView> proteusViews = new ArrayList<>();
        JsonElement element = isDataDriven(viewManager);
        if (element != null) {
            String dataPath = element.getAsString().substring(1);
            int length;

            viewManager.setDataPathForChildren(dataPath);
            try {

                @SuppressWarnings("ConstantConditions")
                JsonElement dataElement = Utils.readJson(dataPath, viewManager.getDataContext().getData(), viewManager.getDataContext().getIndex());

                if (dataElement.isJsonArray()) {
                    length = dataElement.getAsJsonArray().size();
                } else {
                    length = 0;
                }

            } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException | IllegalStateException e) {
                Log.e("#handleChildren() ", e.getMessage());
                length = 0;
            } catch (NumberFormatException e) {
                Log.e("#handleChildren() ", element.getAsString() + " is not a number. layout: " + layout.toString());
                length = 0;
            }

            // get the child type
            JsonObject childLayout = getChildLayout(layout.get(ProteusConstants.CHILD_TYPE), layout, view);

            JsonElement childDataContext = childLayout.get(ProteusConstants.CHILD_DATA_CONTEXT);
            JsonElement childDataContextFromParent = layout.get(ProteusConstants.CHILD_DATA_CONTEXT);

            if (childDataContextFromParent != null && childDataContext != null) {
                Utils.addElements(childDataContext.getAsJsonObject(), childDataContextFromParent.getAsJsonObject(), true);
            } else if (childDataContextFromParent != null) {
                childLayout.add(ProteusConstants.DATA_CONTEXT, childDataContextFromParent);
            }

            viewManager.setChildLayout(new LayoutImpl(childLayout));


            for (int index = 0; index < length; index++) {
                proteusViews.add(builder.build((ViewGroup) view, new LayoutImpl(childLayout), viewManager.getDataContext().getData(), index, viewManager.getStyles()));
            }
        }
        return proteusViews;
    }

    @Override
    public DataContext getDataContext(View parent, JsonObject data, int index) {
        DataContext dataContext, parentDataContext = null;
        JsonElement scope = layout.get(ProteusConstants.DATA_CONTEXT);

        if (parent instanceof ProteusView) {
            parentDataContext = ((ProteusView) parent).getViewManager().getDataContext();
        }

        if (scope == null || scope.isJsonNull()) {
            if (parentDataContext != null) {
                dataContext = new DataContext(parentDataContext);
            } else {
                dataContext = new DataContext();
                dataContext.setData(data);
                dataContext.setIndex(index);
            }
        } else {
            if (parentDataContext != null) {
                dataContext = parentDataContext.createChildDataContext(scope.getAsJsonObject(), index);
            } else {
                dataContext = new DataContext();
                dataContext.setData(data);
                dataContext = dataContext.createChildDataContext(scope.getAsJsonObject(), index);
            }
        }
        return dataContext;
    }

    // TODO: possible NPE, re-factor required
    public JsonObject getChildLayout(JsonElement type, JsonObject source, ProteusView view) {
        if (type == null) {
            return null;
        }

        JsonObject layout;
        if (type.isJsonObject() && !type.isJsonNull()) {
            layout = type.getAsJsonObject();
            layout = Utils.mergeLayouts(layout, source);
        } else if (type.isJsonPrimitive()) {
            layout = onLayoutRequired(type.getAsString(), view);
            if (layout == null) {
                layout = new JsonObject();
                layout.add(ProteusConstants.TYPE, type);
            } else {
                layout = Utils.mergeLayouts(layout, source);
            }
        } else {
            layout = Utils.mergeLayouts(new JsonObject(), source);
            layout.add(ProteusConstants.TYPE, type);
        }

        return layout;
    }

    @Nullable
    protected JsonObject onLayoutRequired(String type, ProteusView parent) {
        return null;
    }

    protected JsonElement isDataDriven(ProteusViewManager viewManager) {
        if (viewManager.getLayout() == null || viewManager.getDataContext() == null || viewManager.getDataContext().getData() == null) {
            return null;
        }

        JsonElement element = layout.get(ProteusConstants.CHILDREN);
        JsonElement child = layout.get(ProteusConstants.CHILD_TYPE);

        if (child == null || child.isJsonNull()) {
            return null;
        }

        if (element != null && element.isJsonPrimitive() && !element.getAsString().isEmpty()
                && element.getAsString().charAt(0) == ProteusConstants.DATA_PREFIX) {
            return element;
        }

        return null;
    }
}
