package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.provider.ProteusConstants;
import com.flipkart.layoutengine.toolbox.BitmapLoader;
import com.flipkart.layoutengine.toolbox.Styles;
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
    @Nullable
    public LayoutHandler getHandler(String viewType) {
        return layoutHandlers.get(viewType);
    }

    @Override
    @Nullable
    public ProteusView build(View parent, JsonObject layout, JsonObject data, int childIndex, Styles styles) {
        return buildImpl(createParserContext(data, styles), new SimpleProteusView(parent, 0, null),
                layout, childIndex, styles);
    }

    protected ParserContext createParserContext(JsonObject data, Styles styles) {
        ParserContext parserContext = new ParserContext();
        parserContext.setLayoutBuilder(this);
        parserContext.setStyles(styles);
        return parserContext;
    }

    /**
     * Starts recursively parsing the given jsonObject.
     *
     * @param context    Represents the context of the parsing.
     * @param parent     The parent view group under which the view being created has to be
     *                   added as a child.
     * @param layout     The jsonObject which represents the current layout which is getting parsed.
     * @param childIndex index of child inside its parent view.
     * @param styles     the styles used to be used
     * @return The {@link ProteusView} that was built.
     */
    protected ProteusView buildImpl(ParserContext context, final ProteusView parent,
                                    final JsonObject layout, final int childIndex, Styles styles) {
        JsonElement viewTypeElement = layout.get(ProteusConstants.TYPE);
        String viewType;

        if (viewTypeElement != null) {
            viewType = viewTypeElement.getAsString();
        } else {
            Log.e(TAG, "'type' missing in layout: " + layout.toString());
            return null;
        }

        LayoutHandler<View> handler = layoutHandlers.get(viewType);
        if (handler == null) {
            return onUnknownViewEncountered(context, viewType, parent, layout, childIndex);
        }

        /**
         * View creation.
         */
        final View createdView;
        createdView = createView(context, (ViewGroup) parent.getView(), handler, layout);
        handler.setupView(context, (ViewGroup) parent.getView(), createdView, layout);

        // create the proteus view to return
        ProteusView proteusView = createProteusViewToReturn(createdView, layout, childIndex, parent);
        prepareView(proteusView, context);

        /**
         * Parsing each attribute and setting it on the view.
         */
        JsonElement jsonDataValue;
        String attributeName;
        for (Map.Entry<String, JsonElement> entry : layout.entrySet()) {
            if (ProteusConstants.TYPE.equals(entry.getKey())
                    || ProteusConstants.CHILDREN.equals(entry.getKey())
                    || ProteusConstants.CHILD_TYPE.equals(entry.getKey())
                    || ProteusConstants.STYLE.equals(entry.getKey())) {
                continue;
            }

            jsonDataValue = entry.getValue();
            attributeName = entry.getKey();

            boolean handled = handleAttribute(handler, context, attributeName, jsonDataValue, layout,
                    proteusView, parent, childIndex);

            if (!handled) {
                onUnknownAttributeEncountered(context, attributeName, jsonDataValue, layout,
                        createdView, childIndex);
            }
        }

        /**
         * Process the Styles
         */
        if (styles != null && layout.has(ProteusConstants.STYLE)
                && !layout.get(ProteusConstants.STYLE).isJsonNull()
                && layout.get(ProteusConstants.STYLE).isJsonPrimitive()) {

            String[] styleSet = layout.get(ProteusConstants.STYLE).getAsString().split(ProteusConstants.STYLE_DELIMITER);
            for (String styleName : styleSet) {
                if (styles.contains(styleName)) {
                    processStyle(styles.getStyle(styleName), layout, proteusView, handler, context, parent, childIndex);
                }
            }
        }

        /**
         * Processing the children.
         */
        List<ProteusView> childrenToAdd = parseChildren(handler, context, proteusView, layout, childIndex, styles);
        if (childrenToAdd == null) {
            return proteusView;
        }

        // add the children to the root view group
        if (childrenToAdd.size() > 0) {
            handler.addChildren(context, proteusView, childrenToAdd, layout);
        }

        return proteusView;
    }

    protected ProteusView createProteusViewToReturn(View createdView, JsonObject layout, int index, ProteusView parent) {
        Log.d(TAG, "ProteusView created with " + Utils.getLayoutIdentifier(layout));
        return new SimpleProteusView(createdView, layout, index, parent);
    }

    protected void prepareView(ProteusView proteusView, ParserContext parserContext) {
        // nothing to do here
    }

    protected List<ProteusView> parseChildren(LayoutHandler handler, ParserContext context, ProteusView view,
                                              JsonObject parentLayout, int childIndex, Styles styles) {

        Log.d(TAG, "Parsing children for view with " + Utils.getLayoutIdentifier(parentLayout));

        JsonElement childrenElement = parentLayout.get(ProteusConstants.CHILDREN);
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
                ProteusView childView = buildImpl(context, view, childObject, i, styles);

                // add the child view to the array of children
                if (childView != null) {
                    childrenView.add(childView);
                }
            }
        } else if (childrenElement.isJsonPrimitive()) {
            int length;
            try {
                String attributeValue = childrenElement.getAsString();
                if (ProteusConstants.DATA_NULL.equals(attributeValue)) {
                    length = 0;
                } else {
                    length = Integer.parseInt(attributeValue);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG + "#parseChildren()", childrenElement.getAsString() +
                        " is not a number. layout: " + parentLayout.toString());
                return null;
            }

            // get the child type
            JsonElement childTypeElement = parentLayout.get(ProteusConstants.CHILD_TYPE);
            if (childTypeElement == null) {
                return null;
            }

            JsonObject childLayout = getChildLayout(childTypeElement, context, parentLayout, view);

            for (int i = 0; i < length; i++) {
                ProteusView childView = buildImpl(context, view, childLayout, i, styles);
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
                                     JsonObject parentLayout, ProteusView view) {

        JsonObject childLayout;
        if (childTypeElement.isJsonObject() && !childTypeElement.isJsonNull()) {
            childLayout = childTypeElement.getAsJsonObject();
        } else if (childTypeElement.isJsonPrimitive()) {
            childLayout = onChildTypeLayoutRequired(parserContext,
                    childTypeElement.getAsString(),
                    parentLayout,
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

    private void processStyle(Map<String, JsonElement> style, JsonObject layout, ProteusView view,
                              LayoutHandler handler, ParserContext context, ProteusView parent, int index) {

        for (Map.Entry<String, JsonElement> attribute : style.entrySet()) {
            if (layout.has(attribute.getKey())) {
                continue;
            }
            boolean handled = handleAttribute(handler, context, attribute.getKey(),
                    attribute.getValue(), layout, view, parent, index);
            if (!handled) {
                onUnknownAttributeEncountered(context, attribute.getKey(), attribute.getValue(),
                        layout, view.getView(), index);
            }
        }
    }

    public boolean handleAttribute(LayoutHandler handler, ParserContext context,
                                   String attribute, JsonElement element, JsonObject layout,
                                   ProteusView view, ProteusView parent, int index) {
        Log.d(TAG, "Handle '" + attribute + "' : " + element.toString() + " for view with " + Utils.getLayoutIdentifier(layout));
        return handler.handleAttribute(context, this, attribute, element, layout, view, index);
    }

    protected void onUnknownAttributeEncountered(ParserContext context, String attribute,
                                                 JsonElement element, JsonObject layout, View view,
                                                 int childIndex) {
        if (ProteusConstants.ATTRIBUTES_TO_IGNORE.contains(attribute)) {
            return;
        }
        if (listener != null) {
            listener.onUnknownAttribute(context, attribute, element, layout, view, childIndex);
        }
    }

    protected ProteusView onUnknownViewEncountered(ParserContext context, String viewType,
                                                   ProteusView parent, JsonObject layout,
                                                   int childIndex) {
        Log.d(TAG, "No LayoutHandler for: " + viewType);
        if (listener != null) {
            return listener.onUnknownViewType(context, viewType, layout, parent, childIndex);
        }
        return null;
    }

    protected JsonObject onChildTypeLayoutRequired(ParserContext context, String viewType,
                                                   JsonObject parentLayout, ProteusView parent) {
        Log.d(TAG, "Fetching child layout: " + viewType);
        if (listener != null) {
            return listener.onChildTypeLayoutRequired(context, viewType, parentLayout, parent);
        }
        return null;
    }

    protected View createView(ParserContext context, ViewGroup parent, LayoutHandler handler, JsonObject layout) {
        return handler.createView(context, this.context, parent, layout);
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
