package com.flipkart.layoutengine.processor;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
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
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Use this as the base processor for references like @drawable or remote resources with http:// urls.
 */
public abstract class DrawableResourceProcessor<V extends View> extends AttributeProcessor<V> {

    private static final String TAG = DrawableResourceProcessor.class.getSimpleName();
    private Context context;
    private Logger logger = LoggerFactory.getLogger(DrawableResourceProcessor.class);

    public DrawableResourceProcessor(Context context) {
        this.context = context;
    }

    @Override
    public void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue,
                       V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
        if (attributeValue.isJsonPrimitive()) {
            handleString(parserContext, attributeKey, attributeValue.getAsString(), view, proteusView, parent, layout, index);
        } else if (attributeValue.isJsonObject()) {
            handleElement(parserContext, attributeKey, attributeValue, view, proteusView, parent, layout, index);
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("Resource for key: " + attributeKey
                        + " must be a primitive or an object. value -> " + attributeValue.toString());
            }
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
     * @param proteusView
     * @param parent
     * @param layout
     */
    protected void handleElement(ParserContext parserContext, String attributeKey,
                                 JsonElement attributeValue, V view, ProteusView proteusView,
                                 ProteusView parent, JsonObject layout, int index) {

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
                        DrawableResourceProcessor<V> processor = new DrawableResourceProcessor<V>(context) {
                            @Override
                            public void setDrawable(V view, Drawable drawable) {
                                stateListDrawable.addState(state.first, drawable);
                            }
                        };
                        processor.handle(parserContext, attributeKey, new JsonPrimitive(state.second), view, proteusView, parent, layout, index);
                    }

                }
            }
            setDrawable(view, stateListDrawable);
        } else if ("layer-list".equals(drawableType)) {
            final List<Pair<Integer, Drawable>> drawables = new ArrayList<>();
            JsonElement childrenElement = jsonObject.get("children");
            if (childrenElement != null) {
                JsonArray children = childrenElement.getAsJsonArray();
                for (JsonElement childElement : children) {
                    JsonObject child = childElement.getAsJsonObject();
                    final Pair<Integer, String> layerPair = ParseHelper.parseLayer(child);

                    DrawableResourceProcessor<V> processor = new DrawableResourceProcessor<V>(context) {
                        @Override
                        public void setDrawable(V view, Drawable drawable) {
                            drawables.add(new Pair<>(layerPair.first, drawable));
                            onLayerDrawableFinish(view, drawables);
                        }
                    };
                    processor.handle(parserContext, attributeKey, new JsonPrimitive(layerPair.second), view, proteusView, parent, layout, index);
                }
            }
        }
    }

    private void onLayerDrawableFinish(V view, List<Pair<Integer, Drawable>> drawables) {
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
     * @param proteusView
     * @param parent
     * @param layout
     */
    protected void handleString(ParserContext parserContext, String attributeKey, final String attributeValue,
                                final V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
        boolean synchronousRendering = parserContext.getLayoutBuilder().isSynchronousRendering();

        if (ParseHelper.isLocalResourceAttribute(attributeValue)) {
            int attributeId = ParseHelper.getAttributeId(context, attributeValue);
            if (0 != attributeId) {
                TypedArray ta = context.obtainStyledAttributes(new int[]{attributeId});
                Drawable drawable = ta.getDrawable(0 /* index */);
                ta.recycle();
                setDrawable(view, drawable);
            }
        } else if (ParseHelper.isColor(attributeValue)) {
            setDrawable(view, new ColorDrawable(ParseHelper.parseColor(attributeValue)));
        } else if (ParseHelper.isLocalDrawableResource(attributeValue)) {
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

    public abstract void setDrawable(V view, Drawable drawable);

}
