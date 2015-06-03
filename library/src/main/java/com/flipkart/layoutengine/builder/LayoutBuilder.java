package com.flipkart.layoutengine.builder;

import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.toolbox.BitmapLoader;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author kirankumar
 */
public interface LayoutBuilder {

    /**
     * Register {@link LayoutHandler}s for custom view types.
     *
     * @param viewType The name of the view type.
     * @param handler  The {@link LayoutHandler} to use while building this view type.
     */
    void registerHandler(String viewType, LayoutHandler<View> handler);

    /**
     * Un-Register the {@link LayoutHandler} registered using {@link LayoutBuilder#registerHandler}.
     *
     * @param viewType remove {@link LayoutHandler} for the specified view type.
     */
    void unregisterHandler(String viewType);

    /**
     * Un-Register all {@link LayoutHandler} registered using {@link LayoutBuilder#registerHandler}.
     */
    void unregisterAllHandlers();

    /**
     * Returns the {@link LayoutHandler} for the specified view type.
     *
     * @param viewType The name of the view type.
     * @return The {@link LayoutHandler} associated to the specified view type
     */
    LayoutHandler getHandler(String viewType);

    /**
     * This method is used to process the attributes from the layout and set them on the {@link View}
     * that is being built.
     *
     * @param handler               The {@link LayoutHandler} which will be used to handle the attribute.
     * @param context               The {@link ParserContext} which will be used to process the attribute values.
     * @param attributeName         The name of the attribute which needs to be processed.
     * @param layoutJsonObject      The current {@link JsonObject} of the layout associated to this attribute.
     * @param jsonAttributeValue    The name of or reference to the value of this attribute
     * @param associatedProteusView The {@link ProteusView} that is being built.
     * @param index                 The index of the view in its parent.
     * @return true if the attribute is processed false otherwise.
     */
    boolean handleAttribute(LayoutHandler<View> handler,
                            ParserContext context,
                            String attributeName,
                            JsonObject layoutJsonObject,
                            JsonElement jsonAttributeValue,
                            ProteusView associatedProteusView,
                            ProteusView parent,
                            int index);

    /**
     * This methods builds a {@link ProteusView} from a layout {@link JsonObject} and an optional
     * data {@link JsonObject} for binding.
     *
     * @param parent The intended parent view for the {@link View} that will be built.
     * @param layout The {@link JsonObject} which defines the layout for the {@link View} to be built.
     * @param data   The {@link JsonObject} which will be used to replace bindings with values in the {@link View}
     * @return A {@link ProteusView} with the built view, an array of its children and optionally its bindings.
     */
    ProteusView build(View parent, JsonObject layout, JsonObject data);

    /**
     * Used to set a callback object to handle unknown view types and unknown attributes and other
     * exceptions. This callback is also used for requesting {@link android.support.v4.view.PagerAdapter}s
     * and {@link android.widget.Adapter}s
     *
     * @param listener The callback object.
     */
    void setListener(LayoutBuilderCallback listener);

    /**
     * @return The callback object used by this {@link LayoutBuilder}
     */
    LayoutBuilderCallback getListener();

    /**
     * @return The helper object that is being used to handle drawables that need to fetched from a
     * network.
     */
    BitmapLoader getNetworkDrawableHelper();


    /**
     * All network bitmap calls will be handed over to this loader. This method is used to
     * set the {@link com.flipkart.layoutengine.toolbox.BitmapLoader} for the
     * {@link com.flipkart.layoutengine.builder.LayoutBuilder}
     *
     * @param bitmapLoader {@link com.flipkart.layoutengine.toolbox.BitmapLoader} to use for
     *                     loading images.
     */
    void setBitmapLoader(BitmapLoader bitmapLoader);

    /**
     * Set this to true for rendering preview immediately. This is to be used to decide whether
     * remote resources like remote images are to be downloaded synchronously or not
     *
     * @return true if the all views should be rendered immediately.
     */
    void setSynchronousRendering(boolean isSynchronousRendering);

    /**
     * @return true when rendering preview immediately by this {@link LayoutBuilder} synchronously
     * otherwise false.
     */
    boolean isSynchronousRendering();
}
