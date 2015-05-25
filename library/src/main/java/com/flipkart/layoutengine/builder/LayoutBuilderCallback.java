package com.flipkart.layoutengine.builder;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.flipkart.layoutengine.EventType;
import com.flipkart.layoutengine.ParserContext;
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
     * @param context    ParserContext for current parsed view
     * @param attribute  attribute that is being parsed
     * @param element    corresponding JsonElemt for the parsed attribute
     * @param object     Original JsonObject of the view
     * @param view       corresponding view for current attribute that is being parsed
     * @param childIndex child index
     */
    void onUnknownAttribute(ParserContext context, String attribute, JsonElement element,
                            JsonObject object, View view, int childIndex);

    /**
     * called when the builder encounters a view type which it cannot understand.
     *
     * @param context        ParserContext for current parsed view
     * @param viewType       type of view that is being parsed
     * @param viewJsonObject corresponding JsonObject for the parsed attribute
     * @param childIndex     child index
     */
    ProteusView onUnknownViewType(ParserContext context, String viewType, JsonObject viewJsonObject,
                                  ViewGroup parent, int childIndex);

    void onViewBuiltFromViewProvider(ProteusView createdView, String viewType, ParserContext context,
                                     JsonObject viewJsonObject, ViewGroup parent, int childIndex);

    /**
     * called when any click occurs on views
     *
     * @param context ParserContext for current parsed view
     * @param view    The view that triggered the event
     */
    View onEvent(ParserContext context, View view, JsonElement attributeValue, EventType eventType);

    PagerAdapter onPagerAdapterRequired(ParserContext parserContext, ProteusView<View> parent,
                                        final List<ProteusView> children, JsonObject viewLayout);

    Adapter onAdapterRequired(ParserContext parserContext, ProteusView<View> parent,
                              final List<ProteusView> children, JsonObject viewLayout);

}
