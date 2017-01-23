/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 *
 * Copyright (c) 2017 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
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
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.webkit.URLUtil;

import com.flipkart.android.proteus.Array;
import com.flipkart.android.proteus.AttributeProcessor;
import com.flipkart.android.proteus.Color;
import com.flipkart.android.proteus.ObjectValue;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.Resource;
import com.flipkart.android.proteus.StyleAttribute;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.manager.ProteusViewManager;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.toolbox.NetworkDrawableHelper;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Iterator;
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
    private static final String DRAWABLE_RIPPLE = "ripple";
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

    @Override
    public void handleValue(V view, Value value) {
        if (value.isPrimitive()) {
            handleString(view, value.getAsString());
        } else if (value.isObject()) {
            handleElement(view, value.getAsObject());
        } else {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, "Drawable Resource must be a primitive or an object. value -> " + value.toString());
            }
        }
    }

    @Override
    public void handleResource(V view, Resource resource) {

    }

    @Override
    public void handleStyleAttribute(V view, StyleAttribute style) {

    }

    /**
     * Any string based drawables are handled here. Color, local resource and remote image urls.
     *
     * @param view
     */
    protected void handleString(final V view, final String value) {
        ProteusViewManager viewManager = ((ProteusView) view).getViewManager();

        if (ParseHelper.isStyleAttribute(value)) {
            int attributeId = ParseHelper.getAttributeId(view.getContext(), value);
            if (0 != attributeId) {
                TypedArray ta = view.getContext().obtainStyledAttributes(new int[]{attributeId});
                Drawable drawable = ta.getDrawable(0 /* index */);
                ta.recycle();
                setDrawable(view, drawable);
            }
        } else if (ParseHelper.isColor(value)) {
            setDrawable(view, new ColorDrawable(ParseHelper.parseColor(value)));
        } else if (ParseHelper.isLocalDrawableResource(value)) {
            try {
                Resources r = view.getContext().getResources();
                int drawableId = r.getIdentifier(value, "drawable", view.getContext().getPackageName());
                Drawable drawable = r.getDrawable(drawableId);
                setDrawable(view, drawable);
            } catch (Exception ex) {
                System.out.println("Could not load local resource " + value);
            }
        } else if (URLUtil.isValidUrl(value)) {
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
            new NetworkDrawableHelper((ProteusView) view, value, viewManager.getProteusLayoutInflater().getBitmapLoader(), callback, null);
        }

    }

    /**
     * This block handles different drawables.
     * Selector and LayerListDrawable are handled here.
     * Override this to setBoolean more types of drawables
     *
     * @param view
     */
    protected void handleElement(V view, ObjectValue value) {

        String type = value.getAsString(TYPE);
        Value children = value.get(CHILDREN);
        Iterator<Value> iterator;
        switch (type) {
            case DRAWABLE_SELECTOR:
                final StateListDrawable stateListDrawable = new StateListDrawable();
                if (children.isArray()) {
                    iterator = children.getAsArray().iterator();
                    while (iterator.hasNext()) {
                        final Pair<int[], Value> state = ParseHelper.parseState(iterator.next().getAsObject());
                        if (state != null) {
                            DrawableResourceProcessor<V> processor = new DrawableResourceProcessor<V>() {
                                @Override
                                public void setDrawable(V view, Drawable drawable) {
                                    stateListDrawable.addState(state.first, drawable);
                                }
                            };
                            processor.process(view, state.second);
                        }

                    }
                }
                setDrawable(view, stateListDrawable);
                break;
            case DRAWABLE_SHAPE:
                GradientDrawable gradientDrawable = loadGradientDrawable(view.getContext(), value);
                if (null != gradientDrawable) {
                    setDrawable(view, gradientDrawable);
                }
                break;
            case DRAWABLE_LAYER_LIST:
                final List<Pair<Integer, Drawable>> drawables = new ArrayList<>();
                if (children.isArray()) {
                    iterator = children.getAsArray().iterator();
                    while (iterator.hasNext()) {
                        final Pair<Integer, Value> layerPair = ParseHelper.parseLayer(iterator.next().getAsObject());
                        if (null != layerPair) {
                            DrawableResourceProcessor<V> processor = new DrawableResourceProcessor<V>() {
                                @Override
                                public void setDrawable(V view, Drawable drawable) {
                                    drawables.add(new Pair<>(layerPair.first, drawable));
                                    onLayerDrawableFinish(view, drawables);
                                }
                            };
                            processor.process(view, layerPair.second);
                        }
                    }
                }
                break;
            case DRAWABLE_LEVEL_LIST:
                final LevelListDrawable levelListDrawable = new LevelListDrawable();
                if (children.isArray()) {
                    iterator = children.getAsArray().iterator();
                    while (iterator.hasNext()) {
                        LayerListDrawableItem layerListDrawableItem = LayerListDrawableItem.parse(iterator.next().getAsObject());
                        layerListDrawableItem.addItem(view, levelListDrawable);
                    }
                    setDrawable(view, levelListDrawable);
                }
                break;
            case DRAWABLE_RIPPLE:
                setDrawable(view, RippleDrawableParser.parse(view, value));
                break;
        }
    }

    public abstract void setDrawable(V view, Drawable drawable);

    @Nullable
    public static GradientDrawable loadGradientDrawable(Context context, ObjectValue value) {
        return ShapeDrawableParser.parse(value, context);
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

    private abstract static class GradientDrawableElement {
        private int mTempColor = 0;

        public abstract void apply(Context context, GradientDrawable gradientDrawable);

        protected int loadColor(Context context, Value value) {
            mTempColor = 0;
            if (!value.isNull()) {
                mTempColor = Color.valueOf(value, context).apply(context).color;
            }
            return mTempColor;
        }
    }

    private static class Corners extends GradientDrawableElement {

        public static final String RADIUS = "radius";
        public static final String TOP_LEFT_RADIUS = "topLeftRadius";
        public static final String TOP_RIGHT_RADIUS = "topRightRadius";
        public static final String BOTTOM_LEFT_RADIUS = "bottomLeftRadius";
        public static final String BOTTOM_RIGHT_RADIUS = "bottomRightRadius";

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

        public static Corners parse(ObjectValue parser) {
            Corners corners = new Corners();
            corners.radius = parser.getAsString(RADIUS);
            corners.topLeftRadius = parser.getAsString(TOP_LEFT_RADIUS);
            corners.topRightRadius = parser.getAsString(TOP_RIGHT_RADIUS);
            corners.bottomLeftRadius = parser.getAsString(BOTTOM_LEFT_RADIUS);
            corners.bottomRightRadius = parser.getAsString(BOTTOM_RIGHT_RADIUS);
            return corners;
        }

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

        public static final String COLOR = "color";

        @SerializedName("color")
        public Value color;

        public static Solid parse(ObjectValue value) {
            Solid solid = new Solid();
            solid.color = value.get(COLOR);
            return solid;
        }

        @Override
        public void apply(Context context, final GradientDrawable gradientDrawable) {
            Color.Result result = Color.valueOf(color, context).apply(context);
            if (null != result.colors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                gradientDrawable.setColor(result.colors);
            } else {
                gradientDrawable.setColor(result.color);
            }
        }
    }

    private static class Gradient extends GradientDrawableElement {

        public static final String ANGLE = "angle";
        public static final String CENTER_X = "centerX";
        public static final String CENTER_Y = "centerY";
        public static final String CENTER_COLOR = "centerColor";
        public static final String END_COLOR = "endColor";
        public static final String GRADIENT_RADIUS = "gradientRadius";
        public static final String START_COLOR = "startColor";
        public static final String GRADIENT_TYPE = "gradientType";
        public static final String USE_LEVEL = "useLevel";

        @SerializedName("angle")
        public Integer angle;
        @SerializedName("centerX")
        public Float centerX;
        @SerializedName("centerY")
        public Float centerY;
        @SerializedName("centerColor")
        public Value centerColor;
        @SerializedName("endColor")
        public Value endColor;
        @SerializedName("gradientRadius")
        public Float gradientRadius;
        @SerializedName("startColor")
        public Value startColor;
        @SerializedName("gradientType")
        public String gradientType;
        @SerializedName("useLevel")
        public Boolean useLevel;

        public static Gradient parse(ObjectValue value) {
            Gradient gradient = new Gradient();
            gradient.angle = value.getAsInteger(ANGLE);
            gradient.centerX = value.getAsFloat(CENTER_X);
            gradient.centerY = value.getAsFloat(CENTER_Y);
            gradient.centerColor = value.get(CENTER_COLOR);
            gradient.endColor = value.get(END_COLOR);
            gradient.gradientRadius = value.getAsFloat(GRADIENT_RADIUS);
            gradient.startColor = value.get(START_COLOR);
            gradient.gradientType = value.getAsString(GRADIENT_TYPE);
            gradient.useLevel = value.getAsBoolean(USE_LEVEL);
            return gradient;
        }

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
            int[] colors;
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

        public static final String WIDTH = "width";
        public static final String HEIGHT = "height";

        @SerializedName("width")
        public String width;
        @SerializedName("height")
        public String height;

        public static Size parse(ObjectValue parser) {
            Size size = new Size();
            size.width = parser.getAsString(WIDTH);
            size.height = parser.getAsString(HEIGHT);
            return size;
        }

        @Override
        public void apply(Context context, GradientDrawable gradientDrawable) {
            gradientDrawable.setSize((int) ParseHelper.parseDimension(width, context), (int) ParseHelper.parseDimension(height, context));
        }
    }

    private static class Stroke extends GradientDrawableElement {

        public static final String WIDTH = "width";
        public static final String COLOR = "color";
        public static final String DASH_WIDTH = "dashWidth";
        public static final String DASH_GAP = "dashGap";

        @SerializedName("width")
        public String width;
        @SerializedName("color")
        public Value color;
        @SerializedName("dashWidth")
        public String dashWidth;
        @SerializedName("dashGap")
        public String dashGap;

        public static Stroke parse(ObjectValue parser) {
            Stroke stroke = new Stroke();
            stroke.width = parser.getAsString(WIDTH);
            stroke.color = parser.get(COLOR);
            stroke.dashWidth = parser.getAsString(DASH_WIDTH);
            stroke.dashGap = parser.getAsString(DASH_GAP);
            return stroke;
        }

        @Override
        public void apply(Context context, GradientDrawable gradientDrawable) {
            if (null == dashWidth) {
                gradientDrawable.setStroke((int) ParseHelper.parseDimension(width, context), loadColor(context, color));
            } else if (null != dashGap) {
                gradientDrawable.setStroke((int) ParseHelper.parseDimension(width, context), loadColor(context, color), ParseHelper.parseDimension(dashWidth, context), ParseHelper.parseDimension(dashGap, context));
            }
        }
    }

    private static class LayerListDrawableItem {

        public static final String MIN_LEVEL = "minLevel";
        public static final String MAX_LEVEL = "maxLevel";
        public static final String DRAWABLE = "drawable";

        @SerializedName("minLevel")
        public Integer minLevel;
        @SerializedName("maxLevel")
        public Integer maxLevel;
        @SerializedName("drawable")
        public Value drawable;

        public static LayerListDrawableItem parse(ObjectValue value) {
            LayerListDrawableItem drawableItem = new LayerListDrawableItem();
            drawableItem.minLevel = value.getAsInteger(MIN_LEVEL);
            drawableItem.maxLevel = value.getAsInteger(MAX_LEVEL);
            drawableItem.drawable = value.get(DRAWABLE);
            return drawableItem;
        }

        public void addItem(View view, final LevelListDrawable levelListDrawable) {
            AttributeProcessor<View> processor = new DrawableResourceProcessor<View>() {
                @Override
                public void setDrawable(View view, Drawable drawable) {
                    levelListDrawable.addLevel(minLevel, maxLevel, drawable);
                }
            };
            processor.process(view, drawable);
        }
    }

    private static class ShapeDrawableParser {

        public static final String SHAPE = "shape";
        public static final String INNER_RADIUS = "innerRadius";
        public static final String INNER_RADIUS_RATIO = "innerRadiusRatio";
        public static final String THICKNESS = "thickness";
        public static final String THICKNESS_RATIO = "thicknessRatio";

        public static GradientDrawable parse(ObjectValue value, Context context) {
            String shape = value.getAsString(SHAPE);
            String innerRadius = value.getAsString(INNER_RADIUS);
            Float innerRadiusRatio = value.getAsFloat(INNER_RADIUS_RATIO);
            String thickness = value.getAsString(THICKNESS);
            Float thicknessRatio = value.getAsFloat(THICKNESS_RATIO);
            Array children = value.getAsArray(CHILDREN);
            Iterator<Value> iterator = children.iterator();

            ArrayList<GradientDrawableElement> elements = null;
            Gradient gradient = null;

            if (children.size() > 0) {
                elements = new ArrayList<>(children.size());
                while (iterator.hasNext()) {
                    ObjectValue child = iterator.next().getAsObject();
                    String typeKey = child.getAsString(TYPE);
                    GradientDrawableElement element = null;
                    switch (typeKey) {
                        case TYPE_CORNERS:
                            element = Corners.parse(child);
                            break;
                        case TYPE_PADDING:
                            break;
                        case TYPE_SIZE:
                            element = Size.parse(child);
                            break;
                        case TYPE_SOLID:
                            element = Solid.parse(child);
                            break;
                        case TYPE_STROKE:
                            element = Stroke.parse(child);
                            break;
                        case TYPE_GRADIENT:
                            gradient = Gradient.parse(child);
                            element = gradient;
                            break;
                    }

                    if (null != element) {
                        elements.add(element);
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

    private static class RippleDrawableParser {

        public static final String COLOR = "color";
        public static final String MASK = "mask";
        public static final String CONTENT = "content";
        public static final String DEFAULT_BACKGROUND = "defaultBackground";

        @Nullable
        public static Drawable parse(@NonNull View view, ObjectValue object) {

            Context context = view.getContext();

            final Value color = object.get(COLOR);
            final Value mask = object.get(MASK);
            final Value content = object.get(CONTENT);
            final Value defaultBackground = object.get(DEFAULT_BACKGROUND);

            final ColorStateList colorStateList;
            final Drawable[] contentDrawable = new Drawable[1];
            final Drawable[] maskDrawable = new Drawable[1];
            final Drawable[] defaultBackgroundDrawable = new Drawable[1];

            Drawable resultDrawable = null;

            Color.Result result = Color.valueOf(color, context).apply(context);

            if (null != result.colors) {
                colorStateList = result.colors;
            } else {
                int[][] states = new int[][]{
                        new int[]{}
                };

                int[] colors = new int[]{
                        result.color
                };
                colorStateList = new ColorStateList(states, colors);
            }

            if (null != content) {
                DrawableResourceProcessor contentDrawableProcessor = new DrawableResourceProcessor() {
                    @Override
                    public void setDrawable(View view, Drawable drawable) {
                        contentDrawable[0] = drawable;
                    }
                };
                contentDrawableProcessor.process(view, content);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (null != mask) {
                    DrawableResourceProcessor maskDrawableProcessor = new DrawableResourceProcessor() {
                        @Override
                        public void setDrawable(View view, Drawable drawable) {
                            maskDrawable[0] = drawable;
                        }
                    };
                    maskDrawableProcessor.process(view, mask);
                }

                resultDrawable = new RippleDrawable(colorStateList, contentDrawable[0], maskDrawable[0]);

            } else if (null != defaultBackground) {
                DrawableResourceProcessor defaultDrawableProcessor = new DrawableResourceProcessor() {
                    @Override
                    public void setDrawable(View view, Drawable drawable) {
                        defaultBackgroundDrawable[0] = drawable;
                    }
                };
                defaultDrawableProcessor.process(view, defaultBackground);

                resultDrawable = defaultBackgroundDrawable[0];

            } else if (contentDrawable[0] != null) {
                int pressedColor = colorStateList.getColorForState(new int[]{android.R.attr.state_pressed}, colorStateList.getDefaultColor());
                int focussedColor = colorStateList.getColorForState(new int[]{android.R.attr.state_focused}, pressedColor);
                ColorDrawable pressedColorDrawable = new ColorDrawable(pressedColor);
                ColorDrawable focussedColorDrawable = new ColorDrawable(focussedColor);
                StateListDrawable stateListDrawable = new StateListDrawable();
                stateListDrawable.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed}, pressedColorDrawable);
                stateListDrawable.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focussedColorDrawable);
                stateListDrawable.addState(new int[]{android.R.attr.state_enabled}, contentDrawable[0]);
                resultDrawable = stateListDrawable;
            }
            return resultDrawable;
        }
    }

}
