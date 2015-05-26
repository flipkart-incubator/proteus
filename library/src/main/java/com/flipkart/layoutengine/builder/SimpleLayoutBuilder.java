package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.provider.GsonProvider;
import com.flipkart.layoutengine.provider.Provider;
import com.flipkart.layoutengine.toolbox.BitmapLoader;
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
class SimpleLayoutBuilder implements LayoutBuilder {

    protected static final String TAG = SimpleLayoutBuilder.class.getSimpleName();

    public static final String TYPE = "type";
    public static final String CHILDREN = "children";
    public static final String CHILD_TYPE = "childType";

    private HashMap<String, LayoutHandler<View>> layoutHandlers = new HashMap<>();
    protected LayoutBuilderCallback listener;
    private BitmapLoader bitmapLoader;
    private boolean isSynchronousRendering = false;

    private Context context;

    public SimpleLayoutBuilder(Context context) {
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
    public ProteusView build(ViewGroup parent, JsonObject layout, JsonObject data) {
        return buildImpl(createParserContext(data), parent, layout, null, 0);
    }

    protected ParserContext createParserContext(JsonObject data) {
        ParserContext parserContext = new ParserContext();
        parserContext.setLayoutBuilder(this);
        if (data != null) {
            parserContext.setDataProvider(new GsonProvider(data));
        }
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
     * @param childIndex            index of child inside its parent view
     * @return The {@link com.flipkart.layoutengine.view.ProteusView} that was built.
     */
    protected ProteusView buildImpl(final ParserContext context, final ViewGroup parent,
                                    final JsonObject currentViewJsonObject, View existingView,
                                    final int childIndex) {
        JsonElement viewTypeElement = currentViewJsonObject.get(TYPE);
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
            createdView = createView(context, parent, handler, currentViewJsonObject);
            handler.setupView(parent, createdView);
        } else {
            createdView = existingView;
        }

        // create the proteus view to return
        ProteusView proteusViewToReturn = createProteusViewToReturn(createdView);
        prepareView(proteusViewToReturn, context.getDataProvider());

        /**
         * Parsing each attribute and setting it on the view.
         */
        for (Map.Entry<String, JsonElement> entry : currentViewJsonObject.entrySet()) {
            if (TYPE.equals(entry.getKey()) || CHILDREN.equals(entry.getKey())
                    || CHILD_TYPE.equals(entry.getKey())) {
                continue;
            }

            JsonElement jsonDataValue = entry.getValue();
            String attributeName = entry.getKey();
            boolean handled = handleAttribute(handler,
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
        JsonElement childViewElement = currentViewJsonObject.get(CHILD_TYPE);
        JsonElement childrenElement = currentViewJsonObject.get(CHILDREN);
        JsonArray children = null;
        if (childrenElement != null) {
            children = parseChildren(handler, context, childrenElement, childIndex);
        }

        if (children != null && children.size() > 0) {
            ViewGroup selfViewGroup = (ViewGroup) createdView;
            List<ProteusView> childrenToAdd = new ArrayList<>();
            for (int i = 0; i < children.size(); i++) {
                JsonObject childObject = children.get(i).getAsJsonObject();
                if (childViewElement != null) {
                    // propagate the value of 'childView' to the recursive calls
                    childObject.add(TYPE, childViewElement);
                }

                // build the child views
                ProteusView childView = buildImpl(context, selfViewGroup, childObject, null, i);

                childrenToAdd.add(childView);
            }

            // add the children to the root view group
            if (childrenToAdd.size() > 0) {
                handler.addChildren(context, proteusViewToReturn, childrenToAdd, currentViewJsonObject);
            }
        }
        return proteusViewToReturn;
    }

    protected ProteusView createProteusViewToReturn(View createdView) {
        return new SimpleProteusView(createdView);
    }

    protected void prepareView(ProteusView proteusView, Provider dataProvider) {
        // nothing to do here
    }

    protected JsonArray parseChildren(LayoutHandler handler, ParserContext context,
                                      JsonElement childrenElement, int childIndex) {
        return handler.parseChildren(context, childrenElement, childIndex);
    }

    public boolean handleAttribute(LayoutHandler<View> handler, ParserContext context,
                                   String attribute, JsonObject jsonObject, JsonElement element,
                                   ProteusView<View> view, ViewGroup parent, int index) {
        return handler.handleAttribute(context, attribute, jsonObject, element, view, index);
    }

    protected void onUnknownAttributeEncountered(ParserContext context, String attribute,
                                                 JsonElement element, JsonObject object, View view,
                                                 int childIndex) {
        if (listener != null) {
            listener.onUnknownAttribute(context, attribute, element, object, view, childIndex);
        }
    }

    protected ProteusView onUnknownViewEncountered(ParserContext context, String viewType,
                                                   ViewGroup parent, JsonObject viewJsonObject,
                                                   int childIndex) {
        if (listener != null) {
            return listener.onUnknownViewType(context, viewType, viewJsonObject, parent, childIndex);
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
