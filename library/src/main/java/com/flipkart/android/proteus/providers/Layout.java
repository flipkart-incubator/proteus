package com.flipkart.android.proteus.providers;

import android.view.View;

import com.flipkart.android.proteus.DataContext;
import com.flipkart.android.proteus.builder.LayoutBuilder;
import com.flipkart.android.proteus.parser.LayoutHandler;
import com.flipkart.android.proteus.view.ProteusView;
import com.flipkart.android.proteus.view.manager.ProteusViewManager;
import com.google.gson.JsonObject;

import java.util.List;

public interface Layout {
    String getType();

    String getLayoutIdentifier();

    List<AttributeValuePair> getAttributes(LayoutHandler handler, ProteusView view);

    boolean has(String key);

    List<ProteusView> getChildrenProteusViews(LayoutBuilder layoutBuilder, ProteusView view, ProteusViewManager viewManager, JsonObject data);

    List<ProteusView> getChildrenProteusViews(ProteusView proteusView, ProteusViewManager proteusViewManager, LayoutHandler handler, LayoutBuilder layoutBuilder);

    DataContext getDataContext(View parent, JsonObject data, int index);
}
