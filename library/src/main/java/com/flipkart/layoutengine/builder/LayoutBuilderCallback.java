package com.flipkart.layoutengine.builder;

import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.widget.Adapter;

import com.flipkart.layoutengine.EventType;
import com.flipkart.layoutengine.toolbox.Styles;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * @author kiran.kumar
 */
public interface LayoutBuilderCallback {

    /**
     * called when the builder encounters an attribute key which is unhandled by its parser.
     *
     * @param attribute attribute that is being parsed
     * @param view      corresponding view for current attribute that is being parsed
     */
    void onUnknownAttribute(String attribute, JsonElement value, ProteusView view);

    /**
     * called when the builder encounters a view type which it cannot understand.
     */
    @Nullable
    ProteusView onUnknownViewType(String type, View parent, JsonObject layout, JsonObject data, int index, Styles styles);

    JsonObject onLayoutRequired(String type, ProteusView parent);

    void onViewBuiltFromViewProvider(ProteusView view, View parent, String type, int index);

    /**
     * called when any click occurs on views
     *
     * @param view The view that triggered the event
     */
    View onEvent(ProteusView view, JsonElement value, EventType eventType);

    PagerAdapter onPagerAdapterRequired(ProteusView parent, final List<ProteusView> children, JsonObject layout);

    Adapter onAdapterRequired(ProteusView parent, final List<ProteusView> children, JsonObject layout);

}
