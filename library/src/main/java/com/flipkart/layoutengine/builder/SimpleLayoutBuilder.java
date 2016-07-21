package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.provider.ProteusConstants;
import com.flipkart.layoutengine.toolbox.BitmapLoader;
import com.flipkart.layoutengine.toolbox.IdGeneratorImpl;
import com.flipkart.layoutengine.toolbox.Styles;
import com.flipkart.layoutengine.toolbox.Utils;
import com.flipkart.layoutengine.view.ProteusView;
import com.flipkart.layoutengine.view.SimpleProteusView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A layout builder which can parse json to construct an android view out of it. It uses the
 * registered handlers to convert the json string to a view and then assign attributes.
 */
public class SimpleLayoutBuilder implements LayoutBuilder {

    public static final String TAG_DEBUG = Utils.TAG_DEBUG;
    public static final String TAG_ERROR = Utils.TAG_ERROR;

    private HashMap<String, LayoutHandler> layoutHandlers = new HashMap<>();
    protected LayoutBuilderCallback listener;
    private BitmapLoader bitmapLoader;
    private boolean isSynchronousRendering = false;
    private Context context;
    private static Logger logger = LoggerFactory.getLogger(SimpleLayoutBuilder.class);
    private IdGenerator idGenerator;

    protected SimpleLayoutBuilder(Context context, @Nullable IdGenerator idGenerator) {
        this.context = context;
        this.idGenerator = null != idGenerator ? idGenerator : new IdGeneratorImpl();
    }

    @Override
    public void registerHandler(String viewType, LayoutHandler handler) {
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
     * @param parserContext Represents the context of the parsing.
     * @param parent        The parent view group under which the view being created has to be
     *                      added as a child.
     * @param layout        The jsonObject which represents the current layout which is getting parsed.
     * @param childIndex    index of child inside its parent view.
     * @param styles        the styles used to be used
     * @return The {@link ProteusView} that was built.
     */
    protected ProteusView buildImpl(ParserContext parserContext, final ProteusView parent,
                                    final JsonObject layout, final int childIndex, Styles styles) {
        String type = Utils.getPropertyAsString(layout, ProteusConstants.TYPE);

        if (type == null) {
            throw new IllegalArgumentException("'type' missing in layout: " + layout.toString());
        }

        LayoutHandler handler = layoutHandlers.get(type);
        if (handler == null) {
            return onUnknownViewEncountered(parserContext, type, parent, layout, childIndex);
        }

        /**
         * View creation.
         */
        final View view;
        view = createView(parserContext, parent, handler, layout);
        setupView(parserContext, parent, view, handler, layout);

        ProteusView proteusView = createProteusView(view, layout, childIndex, parent);
        prepareProteusView(proteusView, parserContext);

        /**
         * Parsing each attribute and setting it on the view.
         */
        JsonElement element;
        String attributeName;
        for (Map.Entry<String, JsonElement> entry : layout.entrySet()) {
            if (ProteusConstants.TYPE.equals(entry.getKey())
                    || ProteusConstants.CHILDREN.equals(entry.getKey())
                    || ProteusConstants.CHILD_TYPE.equals(entry.getKey())) {
                continue;
            }

            element = entry.getValue();
            attributeName = entry.getKey();
            boolean handled = handleAttribute(handler, parserContext, attributeName, element, layout,
                    proteusView, parent, childIndex);
            if (!handled) {
                onUnknownAttributeEncountered(parserContext, attributeName, element, layout,
                        view, childIndex);
            }
        }

        /**
         * Processing the children.
         */
        List<ProteusView> childrenToAdd = parseChildren(handler, parserContext, proteusView, layout, childIndex, styles);
        if (childrenToAdd == null) {
            return proteusView;
        }

        // add the children to the root view group
        if (childrenToAdd.size() > 0) {
            handler.addChildren(parserContext, proteusView, childrenToAdd, layout);
        }

        return proteusView;
    }

    protected View createView(ParserContext parserContext, ProteusView parent,
                              LayoutHandler handler, JsonObject layout) {
        return handler.createView(parserContext, this.context, parent, layout);
    }

    protected void setupView(ParserContext parserContext, ProteusView parent, View view,
                             LayoutHandler handler, JsonObject layout) {
        //noinspection unchecked
        handler.setupView(parserContext, parent, view, layout);
    }

    protected ProteusView createProteusView(View createdView, JsonObject layout, int index, ProteusView parent) {
        if (logger.isDebugEnabled()) {
            logger.debug("ProteusView created with " + Utils.getLayoutIdentifier(layout));
        }
        return new SimpleProteusView(createdView, layout, index, parent);
    }

    protected void prepareProteusView(ProteusView proteusView, ParserContext parserContext) {
        // nothing to do here
    }

    protected List<ProteusView> parseChildren(LayoutHandler handler, ParserContext context, ProteusView view,
                                              JsonObject parentLayout, int childIndex, Styles styles) {
        if (logger.isDebugEnabled()) {
            logger.debug("Parsing children for view with " + Utils.getLayoutIdentifier(parentLayout));
        }
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
                if (logger.isErrorEnabled()) {
                    logger.error(childrenElement.getAsString() +
                            " is not a number. layout: " + parentLayout.toString());
                }
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
            childLayout = onChildTypeLayoutRequired(parserContext, childTypeElement.getAsString(),
                    parentLayout, view);
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
                                   String attribute, JsonElement element, JsonObject layout,
                                   ProteusView view, ProteusView parent, int index) {
        if (logger.isDebugEnabled()) {
            logger.debug("Handle '" + attribute + "' : " + element.toString() + " for view with " + Utils.getLayoutIdentifier(layout));
        }
        //noinspection unchecked
        return handler.handleAttribute(context, attribute, element, layout, view.getView(), view, parent, index);
    }

    protected void onUnknownAttributeEncountered(ParserContext context, String attribute,
                                                 JsonElement element, JsonObject layout, View view,
                                                 int childIndex) {
        if (listener != null) {
            listener.onUnknownAttribute(context, attribute, element, layout, view, childIndex);
        }
    }

    protected ProteusView onUnknownViewEncountered(ParserContext context, String viewType,
                                                   ProteusView parent, JsonObject layout,
                                                   int childIndex) {
        if (logger.isDebugEnabled()) {
            logger.debug("No LayoutHandler for: " + viewType);
        }
        if (listener != null) {
            return listener.onUnknownViewType(context, viewType, layout, parent, childIndex);
        }
        return null;
    }

    protected JsonObject onChildTypeLayoutRequired(ParserContext context, String viewType,
                                                   JsonObject parentLayout, ProteusView parent) {
        if (logger.isDebugEnabled()) {
            logger.debug("Fetching child layout: " + viewType);
        }
        if (listener != null) {
            return listener.onChildTypeLayoutRequired(context, viewType, parentLayout, parent);
        }
        return null;
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

    /**
     * Give the View ID for this string. This will generally be given by the instance of ID Generator
     * which will be available with the Layout Builder.
     * This is similar to R.id auto generated
     *
     * @param id
     * @return int value for this id. This will never be -1.
     */
    @Override
    public int getUniqueViewId(String id) {
        return idGenerator.getUnique(id);
    }

    /**
     * Returns the Id Generator for this Layout Builder
     *
     * @return Returns the Id Generator for this Layout Builder
     */
    @Override
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    @Override
    public void setSynchronousRendering(boolean isSynchronousRendering) {
        this.isSynchronousRendering = isSynchronousRendering;
    }
}
