/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Flipkart Internet Pvt. Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.flipkart.android.proteus.builder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.flipkart.android.proteus.DataContext;
import com.flipkart.android.proteus.parser.LayoutHandler;
import com.flipkart.android.proteus.toolbox.BitmapLoader;
import com.flipkart.android.proteus.toolbox.IdGenerator;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.toolbox.Utils;
import com.flipkart.android.proteus.view.ProteusView;
import com.flipkart.android.proteus.view.manager.ProteusViewManager;
import com.flipkart.android.proteus.view.manager.ProteusViewManagerImpl;
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

    private static Logger logger = LoggerFactory.getLogger(SimpleLayoutBuilder.class);

    private IdGenerator idGenerator;

    protected SimpleLayoutBuilder(@NonNull IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
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
        if (ProteusConstants.isLoggingEnabled()) {
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
        if (ProteusConstants.isLoggingEnabled()) {
            logger.debug("Parsing children for view with " + Utils.getLayoutIdentifier(view.getViewManager().getLayout()));
        }

        handler.handleChildren(view);
    }

    public boolean handleAttribute(LayoutHandler handler, ProteusView view, String attribute, JsonElement value) {
        if (ProteusConstants.isLoggingEnabled()) {
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
        if (ProteusConstants.isLoggingEnabled()) {
            logger.debug("No LayoutHandler for: " + type);
        }
        if (listener != null) {
            return listener.onUnknownViewType(type, parent, layout, data, index, styles);
        }
        return null;
    }

    @Override
    public int getUniqueViewId(String id) {
        return idGenerator.getUnique(id);
    }

    @Nullable
    @Override
    public IdGenerator getIdGenerator() {
        return idGenerator;
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
