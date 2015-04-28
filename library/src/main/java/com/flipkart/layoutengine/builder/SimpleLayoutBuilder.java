package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.binding.Binding;
import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.provider.DataParsingAdapter;
import com.flipkart.layoutengine.provider.GsonProvider;
import com.flipkart.layoutengine.provider.Provider;
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
 * A layout builder which can parse json to construct an android view out of it. It uses the registered handlers to convert the json string to a view and then assign attributes.
 */
class SimpleLayoutBuilder implements LayoutBuilder {

    protected static final String TAG = SimpleLayoutBuilder.class.getSimpleName();

    private static final Character PREFIX = DataParsingAdapter.PREFIX;

    public static final String TYPE = "type";
    public static final String CHILDREN = "children";
    public static final String CHILD_TYPE = "childType";

    private HashMap<String, LayoutHandler<View>> layoutHandlers = new HashMap<>();
    private LayoutBuilderCallback listener;
    private BitmapLoader bitmapLoader;

    // see the getter for doc
    private boolean isSynchronousRendering = false;

    private Context context;

    public SimpleLayoutBuilder(Context context) {
        this.context = context;
    }

    /**
     * Registers a {@link LayoutHandler} for the specified view type. All the attributes will pass through {@link LayoutHandler#handleAttribute} and expect to be handled.
     *
     * @param viewType The string value for "view" attribute. e.g. TextView, ListView
     * @param handler  The handler which should handle this view.
     */
    @Override
    public void registerHandler(String viewType, LayoutHandler<View> handler) {
        handler.prepare(context);
        layoutHandlers.put(viewType, handler);

    }

    /**
     * Unregisters the specified view type.
     *
     * @param viewType The string value for "view" attribute.
     */
    @Override
    public void unregisterHandler(String viewType) {
        layoutHandlers.remove(viewType);
    }

    /**
     * Unregisters all handlers.
     */
    @Override
    public void unregisterAllHandlers() {
        layoutHandlers.clear();
    }

    /**
     * Get the handler registered with the supplied view type
     *
     * @param viewType The string value for "view" attribute. e.g. TextView, ListView
     * @return A {@link com.flipkart.layoutengine.parser.LayoutHandler} for the view type specified.
     */
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
     * @param context      Represents the context of the parsing.
     * @param parent       The parent view group under which the view being created has to be added as a child.
     * @param jsonObject   The jsonObject which represents the current node which is getting parsed.
     * @param existingView A view which needs to be used instead of creating a new one. Pass null for first pass.
     * @param childIndex   index of child inside its parent view
     * @return The {@link com.flipkart.layoutengine.view.ProteusView} that was built.
     */
    protected ProteusView buildImpl(final ParserContext context, final ViewGroup parent, final JsonObject jsonObject, View existingView, final int childIndex) {
        JsonElement viewTypeElement = jsonObject.get(TYPE);
        //System.out.println("ViewType "+ viewTypeElement.getAsString());
        String viewType = null;
        if (viewTypeElement != null) {
            viewType = viewTypeElement.getAsString();
        } else {

            Log.e(TAG, "view cannot be null");
            return null;
        }

        LayoutHandler<View> handler = layoutHandlers.get(viewType);
        if (handler == null) {
            return onUnknownViewEncountered(context, viewType, parent, jsonObject, childIndex);
        }

        /**
         * View creation.
         */
        final View createdView;
        if (existingView == null) {
            createdView = createView(context, parent, handler, jsonObject);
            handler.setupView(parent, createdView);
        } else {
            createdView = existingView;
        }

        /**
         * Parsing each attribute and setting it on the view.
         */
        Map<String, Binding> bindings = new HashMap<String, Binding>();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {

            if (TYPE.equals(entry.getKey()) || CHILDREN.equals(entry.getKey()) || CHILD_TYPE.equals(entry.getKey())) {
                continue;
            }
            JsonElement jsonDataValue = entry.getValue();
            String attributeName = entry.getKey();

            if (jsonDataValue.isJsonPrimitive()) {
                String attributeValue = jsonDataValue.getAsString();
                JsonElement elementFromData = getElementFromData(jsonDataValue, context.getDataProvider(), childIndex);
                if (elementFromData != null) {
                    Binding binding = new Binding(context, handler, attributeName, attributeValue, createdView, parent, childIndex);
                    jsonDataValue = elementFromData;
                    bindings.put(attributeValue, binding);
                }
            }
            boolean handled = handleAttribute(handler, context, attributeName, jsonObject, jsonDataValue, createdView, parent, childIndex);

            if (!handled) {
                onUnknownAttributeEncountered(context, attributeName, jsonDataValue, jsonObject, createdView, childIndex);
            }
        }

        /**
         * Processing the children.
         */
        JsonElement childViewElement = jsonObject.get(CHILD_TYPE);
        JsonElement childrenElement = jsonObject.get(CHILDREN);
        JsonArray children = null;
        if (childrenElement != null) {
            children = parseChildren(handler, context, childrenElement, childIndex);
        }

        if (children != null && children.size() > 0) {
            ViewGroup selfViewGroup = (ViewGroup) createdView;
            List<ProteusView> childrenToAdd = new ArrayList<ProteusView>();
            for (int i = 0; i < children.size(); i++) {
                JsonObject childObject = children.get(i).getAsJsonObject();
                if (childViewElement != null) {
                    // propagate the value of 'childView' to the recursive calls
                    childObject.add(TYPE, childViewElement);
                }

                ProteusView childView = buildImpl(context, selfViewGroup, childObject, null, i);

                bindings.putAll(childView.getBindings());

                if (childView != null) {
                    childrenToAdd.add(childView);
                }

            }
            if (childrenToAdd.size() > 0) {
                handler.addChildren(this.context, selfViewGroup, childrenToAdd);
            }
        }

        ProteusView finalProteusVIew = new SimpleProteusView(createdView, bindings);

        return finalProteusVIew;
    }

    protected JsonArray parseChildren(LayoutHandler handler, ParserContext context, JsonElement childrenElement, int childIndex) {
        return handler.parseChildren(context, childrenElement, childIndex);
    }

    public boolean handleAttribute(LayoutHandler handler, ParserContext context, String attribute, JsonObject jsonObject, JsonElement element, View view, ViewGroup parent, int index) {
        return handler.handleAttribute(context, attribute, jsonObject, element, view, index);
    }

    protected void onUnknownAttributeEncountered(ParserContext context, String attribute, JsonElement element, JsonObject object, View view, int childIndex) {
        if (listener != null) {
            listener.onUnknownAttribute(context, attribute, element, object, view, childIndex);
        }
    }

    protected ProteusView onUnknownViewEncountered(ParserContext context, String viewType, ViewGroup parent, JsonObject jsonObject, int childIndex) {

        if (listener != null) {
            return listener.onUnknownViewType(context, viewType, jsonObject, parent, childIndex);
        }
        return null;
    }

    protected View createView(ParserContext context, ViewGroup parent, LayoutHandler<View> handler, JsonObject object) {
        View view = handler.createView(context, this.context, parent, object);
        return view;
    }

    protected JsonElement getElementFromData(JsonElement element, Provider dataProvider, int childIndex) {
        return Utils.getElementFromData(PREFIX, element, dataProvider, childIndex);
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

    /**
     * All network bitmap calls will be handed over to this loader.
     *
     * @param bitmapLoader
     */
    @Override
    public void setBitmapLoader(BitmapLoader bitmapLoader) {
        this.bitmapLoader = bitmapLoader;
    }

    /**
     * Set this to true for rendering preview immediately. This is to be used to decide whether remote resources like remote images are to be downloaded synchronously or not
     *
     * @return true if the all views should be rendered immediately.
     */
    @Override
    public boolean isSynchronousRendering() {
        return isSynchronousRendering;
    }

    @Override
    public void setSynchronousRendering(boolean isSynchronousRendering) {
        this.isSynchronousRendering = isSynchronousRendering;
    }
}
