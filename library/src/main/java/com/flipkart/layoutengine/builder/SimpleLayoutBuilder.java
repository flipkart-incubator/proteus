package com.flipkart.layoutengine.builder;

import android.support.annotation.Nullable;
import android.view.View;

import com.flipkart.layoutengine.DataContext;
import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.toolbox.BitmapLoader;
import com.flipkart.layoutengine.toolbox.ProteusConstants;
import com.flipkart.layoutengine.toolbox.Styles;
import com.flipkart.layoutengine.toolbox.Utils;
import com.flipkart.layoutengine.view.ProteusView;
import com.flipkart.layoutengine.view.manager.ProteusViewManager;
import com.flipkart.layoutengine.view.manager.ProteusViewManagerImpl;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * A layout builder which can parse json to construct an android view out of it. It uses the
 * registered handlers to convert the json string to a view and then assign attributes.
 */
public class SimpleLayoutBuilder implements LayoutBuilder {

    @Nullable
    protected LayoutBuilderCallback listener;
    private HashMap<String, LayoutHandler> layoutHandlers = new HashMap<>();
    @Nullable
    private BitmapLoader bitmapLoader;

    private boolean isSynchronousRendering = false;

    private Logger logger = LoggerFactory.getLogger(SimpleLayoutBuilder.class);

    protected SimpleLayoutBuilder() {

    }

    @Override
    public void registerHandler(String type, LayoutHandler handler) {
        handler.prepareAttributeHandlers();
        layoutHandlers.put(type, handler);
    }

    @Override
    public void unregisterHandler(String type) {
        layoutHandlers.remove(type);
    }

    @Override
    @Nullable
    public LayoutHandler getHandler(String type) {
        return layoutHandlers.get(type);
    }

    @Override
    @Nullable
    public ProteusView build(View parent, JsonObject layout, JsonObject data, int index, Styles styles) {
        String type = Utils.getPropertyAsString(layout, ProteusConstants.TYPE);

        if (type == null) {
            throw new IllegalArgumentException("'type' missing in layout: " + layout.toString());
        }

        LayoutHandler handler = layoutHandlers.get(type);
        if (handler == null) {
            return onUnknownViewEncountered(type, parent, layout, data, index, styles);
        }

        /**
         * View creation.
         */
        final ProteusView view;

        onBeforeCreateView(handler, parent, layout, data, index, styles);
        view = createView(handler, parent, layout, data, index, styles);
        onAfterCreateView(handler, view, layout, data, index, styles);

        ProteusViewManager viewManager = createViewManager(handler, parent, layout, data, index, styles);
        view.setViewManager(viewManager);

        /**
         * Parsing each attribute and setting it on the view.
         */
        JsonElement value;
        String attribute;

        for (Map.Entry<String, JsonElement> entry : layout.entrySet()) {
            if (ProteusConstants.TYPE.equals(entry.getKey()) || ProteusConstants.CHILDREN.equals(entry.getKey()) || ProteusConstants.CHILD_TYPE.equals(entry.getKey())) {
                continue;
            }

            value = entry.getValue();
            attribute = entry.getKey();

            boolean handled = handleAttribute(handler, view, attribute, value);

            if (!handled) {
                onUnknownAttributeEncountered(attribute, value, view);
            }
        }

        /**
         * Process the children.
         */
        handleChildren(handler, view);

        return view;
    }

    protected void onBeforeCreateView(LayoutHandler handler, View parent, JsonObject layout, JsonObject data, int index, Styles styles) {
        handler.onBeforeCreateView(parent, layout, data, index, styles);
    }

    protected ProteusView createView(LayoutHandler handler, View parent, JsonObject layout, JsonObject data, int index, Styles styles) {
        View view = handler.createView(parent, layout, data, index, styles);
        if (!(view instanceof ProteusView)) {
            throw new IllegalStateException(layout.get(ProteusConstants.TYPE).getAsString() + " is not a ProteusView.");
        }
        return (ProteusView) view;
    }

    protected void onAfterCreateView(LayoutHandler handler, ProteusView view, JsonObject layout, JsonObject data, int index, Styles styles) {
        //noinspection unchecked
        handler.onAfterCreateView((View) view, layout, data, index, styles);
    }

    protected ProteusViewManager createViewManager(LayoutHandler handler, View parent, JsonObject layout, JsonObject data, int index, Styles styles) {
        if (logger.isDebugEnabled()) {
            logger.debug("ProteusView created with " + Utils.getLayoutIdentifier(layout));
        }

        ProteusViewManagerImpl viewManager = new ProteusViewManagerImpl();
        DataContext dataContext = new DataContext();
        dataContext.setData(data);
        dataContext.setIndex(index);

        viewManager.setLayout(layout);
        viewManager.setDataContext(dataContext);
        viewManager.setStyles(styles);
        viewManager.setLayoutBuilder(this);
        viewManager.setLayoutHandler(handler);

        return viewManager;
    }

    protected void handleChildren(LayoutHandler handler, ProteusView view) {
        if (logger.isDebugEnabled()) {
            logger.debug("Parsing children for view with " + Utils.getLayoutIdentifier(view.getViewManager().getLayout()));
        }

        handler.handleChildren(view);
    }

    public boolean handleAttribute(LayoutHandler handler, ProteusView view, String attribute, JsonElement value) {
        if (logger.isDebugEnabled()) {
            logger.debug("Handle '" + attribute + "' : " + value.toString() + " for view with " + Utils.getLayoutIdentifier(view.getViewManager().getLayout()));
        }
        //noinspection unchecked
        return handler.handleAttribute((View) view, attribute, value);
    }

    protected void onUnknownAttributeEncountered(String attribute, JsonElement value, ProteusView view) {
        if (listener != null) {
            listener.onUnknownAttribute(attribute, value, view);
        }
    }

    @Nullable
    protected ProteusView onUnknownViewEncountered(String type, View parent, JsonObject layout, JsonObject data, int index, Styles styles) {
        if (logger.isDebugEnabled()) {
            logger.debug("No LayoutHandler for: " + type);
        }
        if (listener != null) {
            return listener.onUnknownViewType(type, parent, layout, data, index, styles);
        }
        return null;
    }

    @Nullable
    @Override
    public LayoutBuilderCallback getListener() {
        return listener;
    }

    @Override
    public void setListener(@Nullable LayoutBuilderCallback listener) {
        this.listener = listener;
    }

    @Override
    public BitmapLoader getNetworkDrawableHelper() {
        return bitmapLoader;
    }

    @Override
    public void setBitmapLoader(@Nullable BitmapLoader bitmapLoader) {
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
