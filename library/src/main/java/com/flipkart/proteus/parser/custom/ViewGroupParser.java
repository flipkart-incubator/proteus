package com.flipkart.proteus.parser.custom;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.proteus.DataContext;
import com.flipkart.proteus.builder.LayoutBuilder;
import com.flipkart.proteus.parser.Attributes;
import com.flipkart.proteus.parser.ParseHelper;
import com.flipkart.proteus.parser.Parser;
import com.flipkart.proteus.parser.WrappableParser;
import com.flipkart.proteus.processor.StringAttributeProcessor;
import com.flipkart.proteus.toolbox.ProteusConstants;
import com.flipkart.proteus.view.ProteusView;
import com.flipkart.proteus.view.manager.ProteusViewManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ViewGroupParser<T extends ViewGroup> extends WrappableParser<T> {

    private static final String LAYOUT_MODE_CLIP_BOUNDS = "clipBounds";
    private static final String LAYOUT_MODE_OPTICAL_BOUNDS = "opticalBounds";

    public ViewGroupParser(Parser<T> wrappedParser) {
        super(ViewGroup.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers() {
        super.prepareHandlers();
        addHandler(Attributes.ViewGroup.ClipChildren, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                boolean clipChildren = ParseHelper.parseBoolean(attributeValue);
                view.setClipChildren(clipChildren);
            }
        });

        addHandler(Attributes.ViewGroup.ClipToPadding, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                boolean clipToPadding = ParseHelper.parseBoolean(attributeValue);
                view.setClipToPadding(clipToPadding);
            }
        });

        addHandler(Attributes.ViewGroup.LayoutMode, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    if (LAYOUT_MODE_CLIP_BOUNDS.equals(attributeValue)) {
                        view.setLayoutMode(ViewGroup.LAYOUT_MODE_CLIP_BOUNDS);
                    } else if (LAYOUT_MODE_OPTICAL_BOUNDS.equals(attributeValue)) {
                        view.setLayoutMode(ViewGroup.LAYOUT_MODE_OPTICAL_BOUNDS);
                    }
                }
            }
        });

        addHandler(Attributes.ViewGroup.SplitMotionEvents, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                boolean splitMotionEvents = ParseHelper.parseBoolean(attributeValue);
                view.setMotionEventSplittingEnabled(splitMotionEvents);
            }
        });
    }

    @Override
    public boolean handleChildren(ProteusView view) {
        ProteusViewManager viewManager = view.getViewManager();
        LayoutBuilder layoutBuilder = viewManager.getLayoutBuilder();
        DataContext dataContext = viewManager.getDataContext();
        JsonObject layout = viewManager.getLayout();

        if (dataContext == null || layout == null) {
            return false;
        }

        JsonObject data = dataContext.getData();
        JsonElement element = layout.get(ProteusConstants.CHILDREN);
        JsonArray children;
        ProteusView child;

        if (!(element instanceof JsonArray) || layoutBuilder == null) {
            return false;
        }

        children = element.getAsJsonArray();
        for (int index = 0; index < children.size(); index++) {
            child = layoutBuilder.build((View) view, children.get(index).getAsJsonObject(), data, viewManager.getDataContext().getIndex(), view.getViewManager().getStyles());
            addView(view, child);
        }

        return true;
    }

    @Override
    public boolean addView(ProteusView parent, ProteusView view) {
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).addView((View) view);
            return true;
        }
        return false;
    }
}
