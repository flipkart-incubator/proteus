package com.flipkart.layoutengine.view.manager;

import android.support.annotation.Nullable;

import com.flipkart.layoutengine.DataContext;
import com.flipkart.layoutengine.binding.Binding;
import com.flipkart.layoutengine.builder.LayoutBuilder;
import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.toolbox.Styles;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * ProteusViewManager
 *
 * @author aditya.sharat
 */
public interface ProteusViewManager {

    /**
     * Update the {@link android.view.View} with new data.
     *
     * @param data New data for the view
     */
    void update(@Nullable JsonObject data);

    LayoutBuilder getLayoutBuilder();

    void setLayoutBuilder(LayoutBuilder layoutBuilder);

    LayoutHandler getLayoutHandler();

    void setLayoutHandler(LayoutHandler layoutHandler);

    /**
     * Returns the layout used to build this {@link android.view.View}.
     *
     * @return Returns the layout used to build this {@link android.view.View}
     */
    JsonObject getLayout();

    /**
     * Sets the layout used to build this {@link android.view.View}.
     *
     * @param layout The layout used to build this {@link android.view.View}
     */
    void setLayout(JsonObject layout);

    /**
     * Returns the current {@link Styles} set in this {@link android.view.View}.
     *
     * @return Returns the {@link Styles}.
     */
    @Nullable
    Styles getStyles();

    /**
     * Sets the {@link Styles} to be applied to this {@link android.view.View}
     */
    void setStyles(@Nullable Styles styles);

    JsonElement get(String dataPath, int index);

    void set(String dataPath, JsonElement newValue);

    void set(String dataPath, String newValue);

    void set(String dataPath, Number newValue);

    void set(String dataPath, boolean newValue);

    @Nullable
    JsonObject getChildLayout();

    void setChildLayout(@Nullable JsonObject childLayout);

    DataContext getDataContext();

    void setDataContext(DataContext dataContext);

    @Nullable
    String getDataPathForChildren();

    void setDataPathForChildren(@Nullable String dataPathForChildren);

    boolean isViewUpdating();

    void addBinding(Binding binding);

    /**
     * Free all resources held by the view manager
     */
    void destroy();

    void setOnUpdateDataListener(@Nullable OnUpdateDataListener listener);

    void removeOnUpdateDataListener();

    @Nullable
    OnUpdateDataListener getOnUpdateDataListeners();

    interface OnUpdateDataListener {

        JsonObject onBeforeUpdateData(@Nullable JsonObject data);

        JsonObject onAfterDataContext(@Nullable JsonObject data);

        void onUpdateDataComplete(@Nullable JsonObject data);
    }
}
