package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.provider.ProteusConstants;
import com.flipkart.layoutengine.toolbox.BitmapLoader;
import com.flipkart.layoutengine.toolbox.Utils;
import com.flipkart.layoutengine.view.ProteusView;
import com.flipkart.layoutengine.view.SimpleProteusView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A layout builder which can parse json to construct an android view out of it. It uses the
 * registered handlers to convert the json string to a view and then assign attributes.
 */
public class SimpleLayoutBuilder implements LayoutBuilder {

    public static final String TAG = Utils.getTagPrefix() + SimpleLayoutBuilder.class.getSimpleName();


    private HashMap<String, LayoutHandler<View>> layoutHandlers = new HashMap<>();
    protected LayoutBuilderCallback listener;
    private BitmapLoader bitmapLoader;
    private boolean isSynchronousRendering = false;

    private Context context;

    protected SimpleLayoutBuilder(Context context) {
        this.context = context;
    }

    @Override
    public void registerHandler(String viewType, LayoutHandler<View> handler) {
        handler.prepare(context);
        layoutHandlers.put(viewType, handler);
    }

    @Override
    public void unregisterHandler(String viewType) {
        layoutHandlers.remove(viewType);
    }

    @Override
    public void unregisterAllHandlers() {
        layoutHandlers.clear();
    }

    @Override
    public LayoutHandler getHandler(String viewType) {
        return layoutHandlers.get(viewType);
    }

    @Override
    public ProteusView build(View parent, JsonObject layout, JsonObject data, int childIndex) {
        return buildImpl(createParserContext(data), new SimpleProteusView(parent, 0, null),
                layout, null, childIndex);
    }

    protected ParserContext createParserContext(JsonObject data) {
        ParserContext parserContext = new ParserContext();
        parserContext.setLayoutBuilder(this);
        return parserContext;
    }

    /**
     * Starts recursively parsing the given jsonObject.
     *
     * @param context               Represents the context of the parsing.
     * @param parent                The parent view group under which the view being created has to be
     *                              added as a child.
     * @param currentViewJsonObject The jsonObject which represents the current node which is getting parsed.
     * @param existingView          A view which needs to be used instead of creating a new one. Pass null
     *                              for first pass.
     * @param childIndex            index of child inside its parent view.
     * @return The {@link com.flipkart.layoutengine.view.ProteusView} that was built.
     */
    protected ProteusView buildImpl(ParserContext context, final ProteusView parent,
                                    final JsonObject currentViewJsonObject, View existingView,
                                    final int childIndex) {
        JsonElement viewTypeElement = currentViewJsonObject.get(ProteusConstants.TYPE);
        String viewType;

        if (viewTypeElement != null) {
            viewType = viewTypeElement.getAsString();
        } else {
            Log.e(TAG, "view cannot be null");
            return null;
        }

        LayoutHandler<View> handler = layoutHandlers.get(viewType);
        if (handler == null) {
            return onUnknownViewEncountered(context, viewType, parent, currentViewJsonObject, childIndex);
        }

        /**
         * View creation.
         */
        final View createdView;
        if (existingView == null) {
            ViewGroup parentViewGroup = (ViewGroup) parent.getView();
            createdView = createView(context, parentViewGroup, handler, currentViewJsonObject);
            handler.setupView(parentViewGroup, createdView);
        } else {
            createdView = existingView;
        }

        // create the proteus view to return
        ProteusView proteusViewToReturn = createProteusViewToReturn(createdView, childIndex, parent);
        prepareView(proteusViewToReturn, context);

        /**
         * Parsing each attribute and setting it on the view.
         */
        for (Map.Entry<String, JsonElement> entry : currentViewJsonObject.entrySet()) {
            if (ProteusConstants.TYPE.equals(entry.getKey()) || ProteusConstants.CHILDREN.equals(entry.getKey())
                    || ProteusConstants.CHILD_TYPE.equals(entry.getKey())) {
                continue;
            }

            JsonElement jsonDataValue = entry.getValue();
            String attributeName = entry.getKey();
            boolean handled;

            handled = handleAttribute(handler,
                    context,
                    attributeName,
                    currentViewJsonObject,
                    jsonDataValue,
                    proteusViewToReturn,
                    parent,
                    childIndex);

            if (!handled) {
                onUnknownAttributeEncountered(context,
                        attributeName,
                        jsonDataValue,
                        currentViewJsonObject,
                        createdView,
                        childIndex);
            }
        }

        /**
         * Processing the children.
         */
        List<ProteusView> childrenToAdd = parseChildren(handler, context, proteusViewToReturn, currentViewJsonObject, childIndex);
        if (childrenToAdd == null) {
            return proteusViewToReturn;
        }

        // add the children to the root view group
        if (childrenToAdd.size() > 0) {
            handler.addChildren(context, proteusViewToReturn, childrenToAdd, currentViewJsonObject);
        }

        return proteusViewToReturn;
    }

    protected ProteusView createProteusViewToReturn(View createdView, int index, ProteusView parent) {
        return new SimpleProteusView(createdView, index, parent);
    }

    protected void prepareView(ProteusView proteusView, ParserContext parserContext) {
        // nothing to do here
    }

    protected List<ProteusView> parseChildren(LayoutHandler handler, ParserContext context, ProteusView view,
                                              JsonObject parentViewJson, int childIndex) {

        JsonElement childrenElement = parentViewJson.get(ProteusConstants.CHILDREN);

        if (childrenElement == null) {
            return null;
        }

        List<ProteusView> childrenView = new ArrayList<>();

        if (childrenElement.isJsonArray()) {
            JsonArray children = handler.parseChildren(context, childrenElement, childIndex);
            for (int i = 0; i < children.size(); i++) {

                // get layout of child
                JsonObject childObject = children.get(i).getAsJsonObject();

                // build the child view
                ProteusView childView = buildImpl(context, view, childObject, null, i);

                // add the child view to the array of children
                if (childView != null) {
                    childrenView.add(childView);
                }
            }

        } else if (childrenElement.isJsonPrimitive()) {
            int length;

            try {
                length = Integer.parseInt(childrenElement.getAsString());
            } catch (NumberFormatException e) {
                Log.e(TAG + "#parseChildren()", childrenElement.getAsString() +
                        " is not a number. layout: " +
                        parentViewJson.toString());
                return null;
            }

            // get the child type
            JsonElement childTypeElement = parentViewJson.get(ProteusConstants.CHILD_TYPE);

            if (childTypeElement == null) {
                return null;
            }

            JsonObject childLayout = getChildLayout(childTypeElement, context, parentViewJson, view);

            for (int i = 0; i < length; i++) {
                ProteusView childView = buildImpl(context, view, childLayout, null, i);
                if (childView != null && childView.getView() != null) {
                    childrenView.add(childView);
                }
            }
        } else {
            return childrenView;
        }

        return childrenView;
    }

    public JsonObject getChildLayout(JsonElement childTypeElement, ParserContext parserContext,
                                     JsonObject parentViewJson, ProteusView view) {
        JsonObject childLayout;

        if (childTypeElement.isJsonObject() && !childTypeElement.isJsonNull()) {
            childLayout = childTypeElement.getAsJsonObject();
        } else if (childTypeElement.isJsonPrimitive()) {
            childLayout = onChildTypeLayoutRequired(parserContext,
                    childTypeElement.getAsString(),
                    parentViewJson,
                    view);
            if (childLayout == null) {
                childLayout = new JsonObject();
                childLayout.add(ProteusConstants.TYPE, childTypeElement);
            }
        } else {
            childLayout = new JsonObject();
            childLayout.add(ProteusConstants.TYPE, childTypeElement);
        }

        return childLayout;
    }

    public boolean handleAttribute(LayoutHandler handler, ParserContext context,
                                   String attribute, JsonObject layout, JsonElement element,
                                   ProteusView view, ProteusView parent, int index) {
        return handler.handleAttribute(context, attribute, layout, element, view, index);
    }

    protected void onUnknownAttributeEncountered(ParserContext context, String attribute,
                                                 JsonElement element, JsonObject object, View view,
                                                 int childIndex) {
        if (ProteusConstants.ATTRIBUTES_TO_IGNORE.contains(attribute)) {
            return;
        }
        if (listener != null) {
            listener.onUnknownAttribute(context, attribute, element, object, view, childIndex);
        }
    }

    protected ProteusView onUnknownViewEncountered(ParserContext context, String viewType,
                                                   ProteusView parent, JsonObject viewJsonObject,
                                                   int childIndex) {
        if (listener != null) {
            return listener.onUnknownViewType(context, viewType, viewJsonObject, parent, childIndex);
        }
        return null;
    }

    protected JsonObject onChildTypeLayoutRequired(ParserContext context, String viewType,
                                                   JsonObject parentViewJsonObject, ProteusView parent) {
        if (listener != null) {
            return listener.onChildTypeLayoutRequired(context, viewType, parentViewJsonObject, parent);
        }
        return null;
    }

    protected View createView(ParserContext context, ViewGroup parent, LayoutHandler<View> handler,
                              JsonObject object) {
        return handler.createView(context, this.context, parent, object);
    }

    @Override
    public LayoutBuilderCallback getListener() {
        return listener;
    }

    @Override
    public void setListener(LayoutBuilderCallback listener) {
        this.listener = listener;
    }

    @Override
    public BitmapLoader getNetworkDrawableHelper() {
        return bitmapLoader;
    }

    @Override
    public void setBitmapLoader(BitmapLoader bitmapLoader) {
        this.bitmapLoader = bitmapLoader;
    }

    @Override
    public boolean isSynchronousRendering() {
        return isSynchronousRendering;
    }

    @Override
    public void setSynchronousRendering(boolean isSynchronousRendering) {
        this.isSynchronousRendering = isSynchronousRendering;
    }
}
