package com.flipkart.layoutengine.builder;

import android.view.View;

import org.json.JSONObject;

/**
 * A wrapper class to update the views build by a {@link com.flipkart.layoutengine.builder.LayoutBuilder}.
 * Enables consumers of the views built by the builder to update the data, data-model, or layout at
 * runtime.
 *
 * @author Aditya Sharat {@literal <aditya.sharat@flipkart.com>}
 */
public interface ViewBuilder {

    /**
     * Returns the reference to the view wrapped by the {@link com.flipkart.layoutengine.builder.ViewBuilder}
     *
     * @return reference to the view {@link android.view.View} wrapped by the {@link com.flipkart.layoutengine.builder.ViewBuilder}
     */
    public View getView();

    /**
     * Updates the data associated with view wrapped by the {@link com.flipkart.layoutengine.builder.ViewBuilder}
     * with new {@link org.json.JSONObject} object.
     *
     * @param data new {@link org.json.JSONObject} object which will used to update the view.
     * @return reference to the updated view {@link android.view.View} wrapped by the
     * {@link com.flipkart.layoutengine.builder.ViewBuilder}
     */
    public View updateView(JSONObject data);
}
