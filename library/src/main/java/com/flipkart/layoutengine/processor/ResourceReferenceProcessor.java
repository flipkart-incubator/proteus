package com.flipkart.layoutengine.processor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Pair;
import android.view.View;
import android.webkit.URLUtil;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.toolbox.NetworkDrawableHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

/**
 * Use this as the base processor for references like @drawable or remote resources with http:// urls.
 */
public abstract class ResourceReferenceProcessor<T extends View> extends AttributeProcessor<T> {

    private Context context;

    public ResourceReferenceProcessor(Context context) {
        this.context = context;
    }

    @Override
    public void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue, T view, JsonObject layout) {
        if (attributeValue.isJsonPrimitive()) {
            handleString(parserContext, attributeKey, attributeValue.getAsString(), view, layout);
        } else {
            handleElement(parserContext, attributeKey, attributeValue, view, layout);
        }
    }

    /**
     * This block handles different drawables.
     * Selector and LayerListDrawable are handled here.
     * Override this to handle more types of drawables
     *
     * @param parserContext
     * @param attributeKey
     * @param attributeValue
     * @param view
     * @param layout
     */
    protected void handleElement(ParserContext parserContext, String attributeKey,
                                 JsonElement attributeValue, T view, JsonObject layout) {
        JsonObject jsonObject = attributeValue.getAsJsonObject();
        JsonElement type = jsonObject.get("type");
        String drawableType = type.getAsString();
        if ("selector".equals(drawableType)) {
            final StateListDrawable stateListDrawable = new StateListDrawable();
            JsonElement childrenElement = jsonObject.get("children");
            if (childrenElement != null) {
                JsonArray children = childrenElement.getAsJsonArray();
                for (JsonElement childElement : children) {
                    JsonObject child = childElement.getAsJsonObject();
                    final Pair<int[], String> state = ParseHelper.parseState(child);
                    if (state != null) {
                        ResourceReferenceProcessor<T> processor = new ResourceReferenceProcessor<T>(context) {
                            @Override
                            public void setDrawable(T view, Drawable drawable) {
                                stateListDrawable.addState(state.first, drawable);
                            }
                        };
                        processor.handle(parserContext, attributeKey, new JsonPrimitive(state.second), view, layout);
                    }

                }
            }
            setDrawable(view, stateListDrawable);
        } else if ("layer-list".equals(drawableType)) {
            final List<Pair<Integer, Drawable>> drawables = new ArrayList<Pair<Integer, Drawable>>();
            JsonElement childrenElement = jsonObject.get("children");
            if (childrenElement != null) {
                JsonArray children = childrenElement.getAsJsonArray();
                for (JsonElement childElement : children) {
                    JsonObject child = childElement.getAsJsonObject();
                    final Pair<Integer, String> layerPair = ParseHelper.parseLayer(child);

                    ResourceReferenceProcessor<T> processor = new ResourceReferenceProcessor<T>(context) {
                        @Override
                        public void setDrawable(T view, Drawable drawable) {
                            drawables.add(new Pair<>(layerPair.first, drawable));
                            onLayerDrawableFinish(view, drawables);
                        }
                    };
                    processor.handle(parserContext, attributeKey, new JsonPrimitive(layerPair.second), view, layout);
                }
            }
        }
    }

    private void onLayerDrawableFinish(T view, List<Pair<Integer, Drawable>> drawables) {
        Drawable[] drawableContainer = new Drawable[drawables.size()];
        // iterate and create an array of drawables to be used for the constructor
        for (int i = 0; i < drawables.size(); i++) {
            Pair<Integer, Drawable> drawable = drawables.get(i);
            drawableContainer[i] = drawable.second;
        }

        // put them in the constructor
        LayerDrawable layerDrawable = new LayerDrawable(drawableContainer);

        // we could have avoided the following loop if layer drawable has a method to add drawable and set id at same time
        for (int i = 0; i < drawables.size(); i++) {
            Pair<Integer, Drawable> drawable = drawables.get(i);
            layerDrawable.setId(i, drawable.first);
            drawableContainer[i] = drawable.second;
        }

        setDrawable(view, layerDrawable);
    }

    /**
     * Any string based drawables are handled here. Color, local resource and remote image urls.
     *
     * @param parserContext
     * @param attributeKey
     * @param attributeValue
     * @param view
     * @param layout
     */
    protected void handleString(ParserContext parserContext, String attributeKey,
                                final String attributeValue, final T view, JsonObject layout) {
        boolean synchronousRendering = parserContext.getLayoutBuilder().isSynchronousRendering();
        if (ParseHelper.isColor(attributeValue)) {
            setDrawable(view, new ColorDrawable(ParseHelper.parseColor(attributeValue)));
        } else if (ParseHelper.isLocalResource(attributeValue)) {
            try {
                Resources r = context.getResources();
                int drawableId = r.getIdentifier(attributeValue, "drawable", context.getPackageName());
                Drawable drawable = r.getDrawable(drawableId);
                setDrawable(view, drawable);
            } catch (Exception ex) {
                System.out.println("Could not load local resource " + attributeValue);
            }
        } else if (URLUtil.isValidUrl(attributeValue)) {
            NetworkDrawableHelper.DrawableCallback callback = new NetworkDrawableHelper.DrawableCallback() {
                @Override
                public void onDrawableLoad(String url, final Drawable drawable) {
                    setDrawable(view, drawable);
                }

                @Override
                public void onDrawableError(String url, String reason, Drawable errorDrawable) {
                    System.out.println("Could not load " + url + " : " + reason);
                    if (errorDrawable != null)
                        setDrawable(view, errorDrawable);
                }
            };
            new NetworkDrawableHelper(context, view, attributeValue, synchronousRendering, callback,
                    parserContext.getLayoutBuilder().getNetworkDrawableHelper(), layout);
        }

    }

    public abstract void setDrawable(T view, Drawable drawable);

}
