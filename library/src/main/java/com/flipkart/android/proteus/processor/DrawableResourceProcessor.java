/*
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.android.proteus.processor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;

import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.toolbox.ColorUtils;
import com.flipkart.android.proteus.toolbox.NetworkDrawableHelper;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.view.ProteusView;
import com.flipkart.android.proteus.view.manager.ProteusViewManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Use this as the base processor for references like @drawable or remote resources with http:// urls.
 */
public abstract class DrawableResourceProcessor<V extends View> extends AttributeProcessor<V> {

    private static final String TAG = "DrawableResource";

    private static final String DRAWABLE_SELECTOR = "selector";
    private static final String DRAWABLE_SHAPE = "shape";
    private static final String DRAWABLE_LAYER_LIST = "layer-list";
    private static final String DRAWABLE_LEVEL_LIST = "level-list";
    private static final String TYPE = "type";
    private static final String CHILDREN = "children";
    private static final String TYPE_CORNERS = "corners";
    private static final String TYPE_GRADIENT = "gradient";
    private static final String TYPE_PADDING = "padding";
    private static final String TYPE_SIZE = "size";
    private static final String TYPE_SOLID = "solid";
    private static final String TYPE_STROKE = "stroke";
    private static final String SHAPE_RECTANGLE = "rectangle";
    private static final String SHAPE_OVAL = "oval";
    private static final String SHAPE_LINE = "line";
    private static final String SHAPE_RING = "ring";
    private static final String LINEAR_GRADIENT = "linear";
    private static final String RADIAL_GRADIENT = "radial";
    private static final String SWEEP_GRADIENT = "sweep";
    private static Gson sGson = new Gson();

    public static GradientDrawable loadGradientDrawable(Context context, JsonObject value) {
        ShapeDrawableJson shapeDrawable = sGson.fromJson(value, ShapeDrawableJson.class);
        return shapeDrawable.init(context);
    }

    @Override
    public void handle(String key, JsonElement value, V view) {
        if (value.isJsonPrimitive()) {
            handleString(key, value.getAsString(), view);
        } else if (value.isJsonObject()) {
            handleElement(key, value, view);
        } else {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, "Resource for key: " + key + " must be a primitive or an object. value -> " + value.toString());
            }
        }
    }

    /**
     * This block handles different drawables.
     * Selector and LayerListDrawable are handled here.
     * Override this to handle more types of drawables
     *
     * @param attributeKey
     * @param attributeValue
     * @param view
     */
    protected void handleElement(String attributeKey, JsonElement attributeValue, V view) {

        JsonObject jsonObject = attributeValue.getAsJsonObject();

        JsonElement type = jsonObject.get(TYPE);
        String drawableType = type.getAsString();
        JsonElement childrenElement = null;
        switch (drawableType) {
            case DRAWABLE_SELECTOR:
                final StateListDrawable stateListDrawable = new StateListDrawable();
                childrenElement = jsonObject.get(CHILDREN);
                if (childrenElement != null) {
                    JsonArray children = childrenElement.getAsJsonArray();
                    for (JsonElement childElement : children) {
                        JsonObject child = childElement.getAsJsonObject();
                        final Pair<int[], JsonElement> state = ParseHelper.parseState(child);
                        if (state != null) {
                            DrawableResourceProcessor<V> processor = new DrawableResourceProcessor<V>() {
                                @Override
                                public void setDrawable(V view, Drawable drawable) {
                                    stateListDrawable.addState(state.first, drawable);
                                }
                            };
                            processor.handle(attributeKey, state.second, view);
                        }

                    }
                }
                setDrawable(view, stateListDrawable);
                break;
            case DRAWABLE_SHAPE:
                GradientDrawable gradientDrawable = loadGradientDrawable(view.getContext(), jsonObject);
                if (null != gradientDrawable) {
                    setDrawable(view, gradientDrawable);
                }
                break;
            case DRAWABLE_LAYER_LIST:
                final List<Pair<Integer, Drawable>> drawables = new ArrayList<>();
                childrenElement = jsonObject.get(CHILDREN);
                if (childrenElement != null) {
                    JsonArray children = childrenElement.getAsJsonArray();
                    for (JsonElement childElement : children) {
                        JsonObject child = childElement.getAsJsonObject();
                        final Pair<Integer, JsonElement> layerPair = ParseHelper.parseLayer(child);
                        if (null != layerPair) {
                            DrawableResourceProcessor<V> processor = new DrawableResourceProcessor<V>() {
                                @Override
                                public void setDrawable(V view, Drawable drawable) {
                                    drawables.add(new Pair<>(layerPair.first, drawable));
                                    onLayerDrawableFinish(view, drawables);
                                }
                            };
                            processor.handle(attributeKey, layerPair.second, view);
                        }
                    }
                }
                break;
            case DRAWABLE_LEVEL_LIST:
                final LevelListDrawable levelListDrawable = new LevelListDrawable();
                childrenElement = jsonObject.get(CHILDREN);
                if (childrenElement != null) {
                    JsonArray children = childrenElement.getAsJsonArray();
                    for (JsonElement childElement : children) {
                        LayerListDrawableItem layerListDrawableItem = sGson.fromJson(childElement, LayerListDrawableItem.class);
                        layerListDrawableItem.addItem(view.getContext(), levelListDrawable);
                    }
                }
                break;
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
     * @param attributeKey
     * @param attributeValue
     * @param view
     */
    protected void handleString(String attributeKey, final String attributeValue, final V view) {
        ProteusViewManager viewManager = ((ProteusView) view).getViewManager();
        boolean synchronousRendering = viewManager.getLayoutBuilder().isSynchronousRendering();

        if (ParseHelper.isLocalResourceAttribute(attributeValue)) {
            int attributeId = ParseHelper.getAttributeId(view.getContext(), attributeValue);
            if (0 != attributeId) {
                TypedArray ta = view.getContext().obtainStyledAttributes(new int[]{attributeId});
                Drawable drawable = ta.getDrawable(0 /* index */);
                ta.recycle();
                setDrawable(view, drawable);
            }
        } else if (ParseHelper.isColor(attributeValue)) {
            setDrawable(view, new ColorDrawable(ParseHelper.parseColor(attributeValue)));
        } else if (ParseHelper.isLocalDrawableResource(attributeValue)) {
            try {
                Resources r = view.getContext().getResources();
                int drawableId = r.getIdentifier(attributeValue, "drawable", view.getContext().getPackageName());
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
                    if (errorDrawable != null) {
                        setDrawable(view, errorDrawable);
                    }
                }
            };
            new NetworkDrawableHelper(view, attributeValue, synchronousRendering, callback, viewManager.getLayoutBuilder().getNetworkDrawableHelper(), viewManager.getLayout());
        }

    }

    public abstract void setDrawable(V view, Drawable drawable);

    private abstract static class GradientDrawableElement {
        private int mTempColor = 0;

        public abstract void apply(Context context, GradientDrawable gradientDrawable);

        protected int loadColor(Context context, JsonElement colorValue) {
            mTempColor = 0;
            if (null != colorValue && !colorValue.isJsonNull()) {
                ColorUtils.loadColor(context, colorValue, new ValueCallback<Integer>() {
                    @Override
                    public void onReceiveValue(Integer value) {
                        mTempColor = value;
                    }
                }, new ValueCallback<ColorStateList>() {
                    @Override
                    public void onReceiveValue(ColorStateList value) {

                    }
                });
            }
            return mTempColor;
        }
    }

    private static class Corners extends GradientDrawableElement {

        @SerializedName("radius")
        public String radius;
        @SerializedName("topLeftRadius")
        public String topLeftRadius;
        @SerializedName("topRightRadius")
        public String topRightRadius;
        @SerializedName("bottomLeftRadius")
        public String bottomLeftRadius;
        @SerializedName("bottomRightRadius")
        public String bottomRightRadius;

        @Override
        public void apply(Context context, GradientDrawable gradientDrawable) {
            if (!TextUtils.isEmpty(radius)) {
                gradientDrawable.setCornerRadius(ParseHelper.parseDimension(radius, context));
            }

            float fTopLeftRadius = TextUtils.isEmpty(topLeftRadius) ? 0 : ParseHelper.parseDimension(topLeftRadius, context);
            float fTopRightRadius = TextUtils.isEmpty(topRightRadius) ? 0 : ParseHelper.parseDimension(topRightRadius, context);
            float fBottomRightRadius = TextUtils.isEmpty(bottomRightRadius) ? 0 : ParseHelper.parseDimension(bottomRightRadius, context);
            float fBottomLeftRadius = TextUtils.isEmpty(bottomLeftRadius) ? 0 : ParseHelper.parseDimension(bottomLeftRadius, context);

            if (fTopLeftRadius != 0 || fTopRightRadius != 0 || fBottomRightRadius != 0 || fBottomLeftRadius != 0) {
                // The corner radii are specified in clockwise order (see Path.addRoundRect())
                gradientDrawable.setCornerRadii(new float[]{
                        fTopLeftRadius, fTopLeftRadius,
                        fTopRightRadius, fTopRightRadius,
                        fBottomRightRadius, fBottomRightRadius,
                        fBottomLeftRadius, fBottomLeftRadius
                });
            }
        }
    }

    private static class Solid extends GradientDrawableElement {

        @SerializedName("color")
        public JsonElement color;

        @Override
        public void apply(Context context, final GradientDrawable gradientDrawable) {
            ColorUtils.loadColor(context, color, new ValueCallback<Integer>() {
                /**
                 * Invoked when the value is available.
                 *
                 * @param value The value.
                 */
                @Override
                public void onReceiveValue(Integer value) {
                    gradientDrawable.setColor(value);
                }
            }, new ValueCallback<ColorStateList>() {
                /**
                 * Invoked when the value is available.
                 *
                 * @param value The value.
                 */
                @Override
                public void onReceiveValue(ColorStateList value) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        gradientDrawable.setColor(value);
                    }
                }
            });
        }
    }

    private static class Gradient extends GradientDrawableElement {

        @SerializedName("angle")
        public Integer angle;
        @SerializedName("centerX")
        public Float centerX;
        @SerializedName("centerY")
        public Float centerY;
        @SerializedName("centerColor")
        public JsonElement centerColor;
        @SerializedName("endColor")
        public JsonElement endColor;
        @SerializedName("gradientRadius")
        public Float gradientRadius;
        @SerializedName("startColor")
        public JsonElement startColor;
        @SerializedName("gradientType")
        public String gradientType;
        @SerializedName("useLevel")
        public Boolean useLevel;

        public static GradientDrawable.Orientation getOrientation(Integer angle) {
            GradientDrawable.Orientation orientation = GradientDrawable.Orientation.LEFT_RIGHT;

            if (null != angle) {
                angle %= 360;
                if (angle % 45 == 0) {
                    switch (angle) {
                        case 0:
                            orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                            break;
                        case 45:
                            orientation = GradientDrawable.Orientation.BL_TR;
                            break;
                        case 90:
                            orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                            break;
                        case 135:
                            orientation = GradientDrawable.Orientation.BR_TL;
                            break;
                        case 180:
                            orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                            break;
                        case 225:
                            orientation = GradientDrawable.Orientation.TR_BL;
                            break;
                        case 270:
                            orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                            break;
                        case 315:
                            orientation = GradientDrawable.Orientation.TL_BR;
                            break;
                    }
                }
            }
            return orientation;
        }

        public static GradientDrawable init(@Nullable int[] colors, @Nullable Integer angle) {
            return colors != null ? new GradientDrawable(getOrientation(angle), colors) : new GradientDrawable();
        }

        @Override
        public void apply(Context context, GradientDrawable gradientDrawable) {
            if (null != centerX && null != centerY) {
                gradientDrawable.setGradientCenter(centerX, centerY);
            }

            if (null != gradientRadius) {
                gradientDrawable.setGradientRadius(gradientRadius);
            }

            if (!TextUtils.isEmpty(gradientType)) {
                switch (gradientType) {
                    case LINEAR_GRADIENT:
                        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                        break;
                    case RADIAL_GRADIENT:
                        gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
                        break;
                    case SWEEP_GRADIENT:
                        gradientDrawable.setGradientType(GradientDrawable.SWEEP_GRADIENT);
                        break;
                }

            }
        }

        public GradientDrawable init(Context context) {
            int[] colors = null;
            if (centerColor != null) {
                colors = new int[3];
                colors[0] = loadColor(context, startColor);
                colors[1] = loadColor(context, centerColor);
                colors[2] = loadColor(context, endColor);
            } else {
                colors = new int[2];
                colors[0] = loadColor(context, startColor);
                colors[1] = loadColor(context, endColor);
            }

            return init(colors, angle);
        }
    }

    private static class Size extends GradientDrawableElement {

        @SerializedName("width")
        public String width;
        @SerializedName("height")
        public String height;

        @Override
        public void apply(Context context, GradientDrawable gradientDrawable) {
            gradientDrawable.setSize((int) ParseHelper.parseDimension(width, context), (int) ParseHelper.parseDimension(height, context));
        }
    }

    private static class Stroke extends GradientDrawableElement {

        @SerializedName("width")
        public String width;
        @SerializedName("color")
        public JsonElement color;
        @SerializedName("dashWidth")
        public String dashWidth;
        @SerializedName("dashGap")
        public String dashGap;

        @Override
        public void apply(Context context, GradientDrawable gradientDrawable) {
            if (null == dashWidth) {
                gradientDrawable.setStroke((int) ParseHelper.parseDimension(width, context), loadColor(context, color));
            } else if (null != dashWidth) {
                gradientDrawable.setStroke((int) ParseHelper.parseDimension(width, context), loadColor(context, color), ParseHelper.parseDimension(dashWidth, context), ParseHelper.parseDimension(dashGap, context));
            }
        }
    }

    private static class LayerListDrawableItem {

        @SerializedName("minLevel")
        public Integer minLevel;
        @SerializedName("maxLevel")
        public Integer maxLevel;
        @SerializedName("drawable")
        public JsonElement drawable;

        public void addItem(Context context, final LevelListDrawable levelListDrawable) {
            DrawableResourceProcessor<View> processor = new DrawableResourceProcessor<View>() {
                @Override
                public void setDrawable(View view, Drawable drawable) {
                    levelListDrawable.addLevel(minLevel, maxLevel, drawable);
                }
            };
        }
    }

    private static class ShapeDrawableJson {

        @SerializedName("shape")
        public String shape;
        @SerializedName("innerRadius")
        public String innerRadius;
        @SerializedName("innerRadiusRatio")
        public Float innerRadiusRatio;
        @SerializedName("thickness")
        public String thickness;
        @SerializedName("thicknessRatio")
        public Float thicknessRatio;
        @SerializedName("children")
        public JsonArray children;

        public GradientDrawable init(Context context) {
            ArrayList<GradientDrawableElement> elements = null;
            Gradient gradient = null;

            if (children != null && children.size() > 0) {
                elements = new ArrayList<>(children.size());
                for (JsonElement jsonElement : children) {
                    if (jsonElement.isJsonObject()) {
                        String typeKey = jsonElement.getAsJsonObject().getAsJsonPrimitive(TYPE).getAsString();
                        GradientDrawableElement element = null;
                        switch (typeKey) {
                            case TYPE_CORNERS:
                                element = sGson.fromJson(jsonElement, Corners.class);
                                break;
                            case TYPE_PADDING:
                                break;
                            case TYPE_SIZE:
                                element = sGson.fromJson(jsonElement, Size.class);
                                break;
                            case TYPE_SOLID:
                                element = sGson.fromJson(jsonElement, Solid.class);
                                break;
                            case TYPE_STROKE:
                                element = sGson.fromJson(jsonElement, Stroke.class);
                                break;
                            case TYPE_GRADIENT:
                                gradient = sGson.fromJson(jsonElement, Gradient.class);
                                element = gradient;
                                break;
                        }

                        if (null != element) {
                            elements.add(element);
                        }

                    }
                }
            }

            GradientDrawable gradientDrawable = (null != gradient) ? gradient.init(context) : new GradientDrawable();

            if (!TextUtils.isEmpty(shape)) {
                int shapeInt = -1;
                switch (shape) {
                    case SHAPE_RECTANGLE:
                        shapeInt = GradientDrawable.RECTANGLE;
                        break;
                    case SHAPE_OVAL:
                        shapeInt = GradientDrawable.OVAL;
                        break;
                    case SHAPE_LINE:
                        shapeInt = GradientDrawable.LINE;
                        break;
                    case SHAPE_RING:
                        shapeInt = GradientDrawable.RING;
                        break;
                }

                if (-1 != shapeInt) {
                    gradientDrawable.setShape(shapeInt);
                }
            }
            if (null != elements) {
                for (GradientDrawableElement element : elements) {
                    element.apply(context, gradientDrawable);
                }
            }

            return gradientDrawable;
        }
    }

}
