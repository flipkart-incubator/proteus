package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonObject;

public class ViewGroupParser<T extends ViewGroup> extends WrappableParser<T> {
    private static final String LAYOUT_MODE_CLIP_BOUNDS = "clipBounds";
    private static final String LAYOUT_MODE_OPTICAL_BOUNDS = "opticalBounds";

    public ViewGroupParser(Parser<T> wrappedParser) {
        super(ViewGroup.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(Context context) {
        super.prepareHandlers(context);
        addHandler(Attributes.ViewGroup.ClipChildren, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                Boolean clipChildren = ParseHelper.parseBoolean(attributeValue);
                if (null != clipChildren) {
                    view.setClipChildren(clipChildren);
                }
            }
        });

        addHandler(Attributes.ViewGroup.ClipToPadding, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                Boolean clipToPadding = ParseHelper.parseBoolean(attributeValue);
                if (null != clipToPadding) {
                    view.setClipToPadding(clipToPadding);
                }
            }
        });

        addHandler(Attributes.ViewGroup.LayoutMode, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                if (LAYOUT_MODE_CLIP_BOUNDS.equals(attributeValue)) {
                    view.setLayoutMode(ViewGroup.LAYOUT_MODE_CLIP_BOUNDS);
                } else if (LAYOUT_MODE_OPTICAL_BOUNDS.equals(attributeValue)) {
                    view.setLayoutMode(ViewGroup.LAYOUT_MODE_OPTICAL_BOUNDS);
                }
            }
        });

        addHandler(Attributes.ViewGroup.SplitMotionEvents, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                Boolean splitMotionEvents = ParseHelper.parseBoolean(attributeValue);
                if (null != splitMotionEvents) {
                    view.setMotionEventSplittingEnabled(splitMotionEvents);
                }
            }
        });
    }
}
