package com.flipkart.layoutengine.view;

import android.view.View;

import com.google.gson.JsonElement;

/**
 * A wrapper class to update the views build by a {@link com.flipkart.layoutengine.builder.LayoutBuilder}.
 * Enables consumers of the views built by the builder to update the data, data-model, or layout at
 * runtime.
 *
 * @author Aditya Sharat {@literal <aditya.sharat@flipkart.com>}
 */
public interface ProteusView {

    /**
     * Returns the reference to the view wrapped by the {@link ProteusView}
     *
     * @return reference to the view {@link android.view.View} wrapped by the {@link ProteusView}
     */
    public View getView();

    /**
     * Updates the data associated with view wrapped by the {@link ProteusView}
     * with new {@link org.json.JSONObject} object.
     *
     * @param data new {@link org.json.JSONObject} object which will used to update the view.
     * @return reference to the updated view {@link android.view.View} wrapped by the
     * {@link ProteusView}
     */
    public View updateView(JsonElement data);
}
