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

package com.flipkart.android.proteus;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.processor.ColorResourceProcessor;
import com.flipkart.android.proteus.processor.DimensionAttributeProcessor;
import com.flipkart.android.proteus.processor.DrawableResourceProcessor;
import com.flipkart.android.proteus.toolbox.BitmapLoader;
import com.flipkart.android.proteus.toolbox.DrawableCallback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * DrawableValue
 * TODO: the layer and state parser need to be moved here from the {@link ParseHelper}
 *
 * @author aditya.sharat
 */
public abstract class DrawableValue extends Value {

    private static final String PREFIX_DRAWABLE = "@drawable/";

    private static final String TYPE = "type";
    private static final String CHILDREN = "children";

    private static final String DRAWABLE_SELECTOR = "selector";
    private static final String DRAWABLE_SHAPE = "shape";
    private static final String DRAWABLE_LAYER_LIST = "layer-list";
    private static final String DRAWABLE_LEVEL_LIST = "level-list";
    private static final String DRAWABLE_RIPPLE = "ripple";

    private static final String TYPE_CORNERS = "corners";
    private static final String TYPE_GRADIENT = "gradient";
    private static final String TYPE_PADDING = "padding";
    private static final String TYPE_SIZE = "size";
    private static final String TYPE_SOLID = "solid";
    private static final String TYPE_STROKE = "stroke";

    public static boolean isLocalDrawableResource(String value) {
        return value.startsWith(PREFIX_DRAWABLE);
    }

    @Nullable
    public static DrawableValue valueOf(String value, Context context) {
        if (Color.isColor(value)) {
            return ColorValue.valueOf(value);
        } else {
            return UrlValue.valueOf(value);
        }
    }

    @Nullable
    public static DrawableValue valueOf(ObjectValue value, Context context) {
        String type = value.getAsString(TYPE);
        switch (type) {
            case DRAWABLE_SELECTOR:
                return StateListValue.valueOf(value.getAsArray(CHILDREN), context);
            case DRAWABLE_SHAPE:
                return ShapeValue.valueOf(value, context);
            case DRAWABLE_LAYER_LIST:
                return LayerListValue.valueOf(value.getAsArray(CHILDREN), context);
            case DRAWABLE_LEVEL_LIST:
                return LevelListValue.valueOf(value.getAsArray(CHILDREN), context);
            case DRAWABLE_RIPPLE:
                return RippleValue.valueOf(value, context);
            default:
                return null;
        }
    }

    public abstract void apply(Context context, Callback callback, BitmapLoader loader, ProteusView view);

    @Override
    Value copy() {
        return this;
    }

    public interface Callback {
        void apply(Drawable drawable);
    }

    public static class ColorValue extends DrawableValue {

        private final Value color;

        private ColorValue(Value color) {
            this.color = color;
        }

        public static ColorValue valueOf(String value) {
            return new ColorValue(Color.valueOf(value));
        }

        public static ColorValue valueOf(Value value, Context context) {
            return new ColorValue(ColorResourceProcessor.staticParse(value, context));
        }

        @Override
        public void apply(Context context, Callback callback, BitmapLoader loader, ProteusView view) {
            Drawable drawable = new ColorDrawable(ColorResourceProcessor.evaluate(color, view).color);
            callback.apply(drawable);
        }
    }

    public static class ShapeValue extends DrawableValue {

        private static final int SHAPE_NONE = -1;

        private static final String SHAPE_RECTANGLE = "rectangle";
        private static final String SHAPE_OVAL = "oval";
        private static final String SHAPE_LINE = "line";
        private static final String SHAPE_RING = "ring";

        private static final String SHAPE = "shape";

        private final int shape;
        private final Gradient gradient;
        private final List<DrawableElement> elements;

        private ShapeValue(ObjectValue value, Context context) {
            shape = getShape(value.getAsString(SHAPE));

            Gradient gradient = null;
            Array children = value.getAsArray(CHILDREN);
            Iterator<Value> iterator = children.iterator();

            if (children.size() > 0) {
                elements = new ArrayList<>(children.size());
                while (iterator.hasNext()) {
                    ObjectValue child = iterator.next().getAsObject();
                    String typeKey = child.getAsString(TYPE);
                    DrawableElement element = null;
                    switch (typeKey) {
                        case TYPE_CORNERS:
                            element = Corners.valueOf(child, context);
                            break;
                        case TYPE_PADDING:
                            break;
                        case TYPE_SIZE:
                            element = Size.valueOf(child, context);
                            break;
                        case TYPE_SOLID:
                            element = Solid.valueOf(child, context);
                            break;
                        case TYPE_STROKE:
                            element = Stroke.valueOf(child, context);
                            break;
                        case TYPE_GRADIENT:
                            gradient = Gradient.valueOf(child, context);
                            element = gradient;
                            break;
                    }

                    if (null != element) {
                        elements.add(element);
                    }
                }
                this.gradient = gradient;
            } else {
                elements = null;
                this.gradient = null;
            }
        }

        public static ShapeValue valueOf(ObjectValue value, Context context) {
            return new ShapeValue(value, context);
        }

        private static int getShape(String shape) {
            switch (shape) {
                case SHAPE_RECTANGLE:
                    return GradientDrawable.RECTANGLE;
                case SHAPE_OVAL:
                    return GradientDrawable.OVAL;
                case SHAPE_LINE:
                    return GradientDrawable.LINE;
                case SHAPE_RING:
                    return GradientDrawable.RING;
                default:
                    return SHAPE_NONE;

            }
        }

        @Override
        public void apply(Context context, Callback callback, BitmapLoader loader, ProteusView view) {
            GradientDrawable drawable = null != gradient ? gradient.init(view) : new GradientDrawable();
            if (-1 != shape) {
                drawable.setShape(shape);
            }
            if (null != elements) {
                for (DrawableElement element : elements) {
                    element.apply(view, drawable);
                }
            }
            callback.apply(drawable);
        }
    }

    public static class LayerListValue extends DrawableValue {

        private final int[] ids;
        private final Value[] layers;

        private LayerListValue(Array layers) {
            this.ids = new int[layers.size()];
            this.layers = new Value[layers.size()];
            Pair<Integer, Value> pair;
            int index = 0;
            Iterator<Value> iterator = layers.iterator();
            while (iterator.hasNext()) {
                pair = ParseHelper.parseLayer(iterator.next().getAsObject());
                this.ids[index] = pair.first;
                this.layers[index] = pair.second;
                index++;
            }
        }

        public static LayerListValue valueOf(Array layers, Context context) {
            return new LayerListValue(layers);
        }

        @Override
        public void apply(Context context, Callback callback, BitmapLoader loader, ProteusView view) {
            final Drawable[] drawables = new Drawable[layers.length];
            int index = 0;
            for (Value layer : layers) {
                drawables[index] = DrawableResourceProcessor.evaluate(layer, view);
                index++;
            }

            LayerDrawable layerDrawable = new LayerDrawable(drawables);

            // we could have avoided the following loop if layer drawable
            // had a method to add drawable and set id at same time
            for (int i = 0; i < drawables.length; i++) {
                layerDrawable.setId(i, ids[i]);
            }

            callback.apply(layerDrawable);
        }
    }

    public static class StateListValue extends DrawableValue {

        private final Pair<int[], Value>[] states;

        private StateListValue(Array states) {
            this.states = new Pair[states.size()];
            Iterator<Value> iterator = states.iterator();
            int index = 0;
            while (iterator.hasNext()) {
                this.states[index] = ParseHelper.parseState(iterator.next().getAsObject());
                index++;
            }
        }

        public static StateListValue valueOf(Array states, Context context) {
            return new StateListValue(states);
        }

        @Override
        public void apply(Context context, Callback callback, BitmapLoader loader, ProteusView view) {
            final StateListDrawable stateListDrawable = new StateListDrawable();
            for (final Pair<int[], Value> layer : states) {
                stateListDrawable.addState(layer.first, DrawableResourceProcessor.evaluate(layer.second, view));
            }
            callback.apply(stateListDrawable);
        }
    }

    public static class LevelListValue extends DrawableValue {

        public final Level[] levels;

        public LevelListValue(Array levels, Context context) {
            this.levels = new Level[levels.size()];
            int index = 0;
            Iterator<Value> iterator = levels.iterator();
            while (iterator.hasNext()) {
                this.levels[index] = new Level(iterator.next().getAsObject(), context);
                index++;
            }
        }

        public static LevelListValue valueOf(Array layers, Context context) {
            return new LevelListValue(layers, context);
        }

        @Override
        public void apply(Context context, Callback callback, BitmapLoader loader, ProteusView view) {
            final LevelListDrawable levelListDrawable = new LevelListDrawable();
            for (Level level : levels) {
                level.apply(view, levelListDrawable);
            }
        }

        private static class Level {
            public static final String MIN_LEVEL = "minLevel";
            public static final String MAX_LEVEL = "maxLevel";
            public static final String DRAWABLE = "drawable";

            public final Integer minLevel;
            public final Integer maxLevel;
            public final Value drawable;

            private Level(ObjectValue value, Context context) {
                minLevel = value.getAsInteger(MIN_LEVEL);
                maxLevel = value.getAsInteger(MAX_LEVEL);
                drawable = DrawableResourceProcessor.staticParse(value.get(DRAWABLE), context);
            }

            public void apply(ProteusView view, final LevelListDrawable levelListDrawable) {
                levelListDrawable.addLevel(minLevel, maxLevel, DrawableResourceProcessor.evaluate(drawable, view));
            }
        }
    }

    public static class RippleValue extends DrawableValue {

        public static final String COLOR = "color";
        public static final String MASK = "mask";
        public static final String CONTENT = "content";
        public static final String DEFAULT_BACKGROUND = "defaultBackground";

        public final Value color;
        public final Value mask;
        public final Value content;
        public final Value defaultBackground;

        private RippleValue(ObjectValue object, Context context) {
            color = object.get(COLOR);
            Value value;

            value = object.get(MASK);
            if (null != value) {
                mask = DrawableResourceProcessor.staticParse(value, context);
            } else {
                mask = null;
            }
            value = object.get(CONTENT);
            if (null != value) {
                content = DrawableResourceProcessor.staticParse(value, context);
            } else {
                content = null;
            }
            value = object.get(DEFAULT_BACKGROUND);
            if (null != value) {
                defaultBackground = DrawableResourceProcessor.staticParse(value, context);
            } else {
                defaultBackground = null;
            }
        }

        public static RippleValue valueOf(ObjectValue value, Context context) {
            return new RippleValue(value, context);
        }

        @Override
        public void apply(Context context, Callback callback, BitmapLoader loader, ProteusView view) {
            ColorStateList colorStateList;
            Drawable contentDrawable = null;
            Drawable maskDrawable = null;

            Drawable resultDrawable = null;

            com.flipkart.android.proteus.Color.Result result = ColorResourceProcessor.evaluate(color, view);

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
                contentDrawable = DrawableResourceProcessor.evaluate(content, view);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (null != mask) {
                    maskDrawable = DrawableResourceProcessor.evaluate(mask, view);
                }
                resultDrawable = new RippleDrawable(colorStateList, contentDrawable, maskDrawable);
            } else if (null != defaultBackground) {
                resultDrawable = DrawableResourceProcessor.evaluate(defaultBackground, view);
            } else if (contentDrawable != null) {
                int pressedColor = colorStateList.getColorForState(new int[]{android.R.attr.state_pressed}, colorStateList.getDefaultColor());
                int focusedColor = colorStateList.getColorForState(new int[]{android.R.attr.state_focused}, pressedColor);
                ColorDrawable pressedColorDrawable = new ColorDrawable(pressedColor);
                ColorDrawable focusedColorDrawable = new ColorDrawable(focusedColor);
                StateListDrawable stateListDrawable = new StateListDrawable();
                stateListDrawable.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed}, pressedColorDrawable);
                stateListDrawable.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focusedColorDrawable);
                stateListDrawable.addState(new int[]{android.R.attr.state_enabled}, contentDrawable);
                resultDrawable = stateListDrawable;
            }

            callback.apply(resultDrawable);
        }
    }

    public static class UrlValue extends DrawableValue {

        public final String url;

        private UrlValue(String url) {
            this.url = url;
        }

        public static UrlValue valueOf(String url) {
            return new UrlValue(url);
        }

        @Override
        public void apply(Context context, final Callback callback, BitmapLoader loader, ProteusView view) {
            new DrawableCallback(view, url, loader) {

                @Override
                public void onDrawableLoad(String url, final Drawable drawable) {
                    callback.apply(drawable);
                }

                @Override
                public void onDrawableError(String url, String reason, Drawable errorDrawable) {
                    callback.apply(errorDrawable);
                }
            };
        }
    }

    /**
     *
     */
    private static abstract class DrawableElement {

        public abstract void apply(ProteusView view, GradientDrawable drawable);

    }

    private static class Gradient extends DrawableElement {

        public static final String ANGLE = "angle";
        public static final String CENTER_X = "centerX";
        public static final String CENTER_Y = "centerY";
        public static final String CENTER_COLOR = "centerColor";
        public static final String END_COLOR = "endColor";
        public static final String GRADIENT_RADIUS = "gradientRadius";
        public static final String START_COLOR = "startColor";
        public static final String GRADIENT_TYPE = "gradientType";
        public static final String USE_LEVEL = "useLevel";

        private static final int GRADIENT_TYPE_NONE = -1;

        private static final String LINEAR_GRADIENT = "linear";
        private static final String RADIAL_GRADIENT = "radial";
        private static final String SWEEP_GRADIENT = "sweep";

        public final Integer angle;
        public final Float centerX;
        public final Float centerY;
        public final Value centerColor;
        public final Value endColor;
        public final Value gradientRadius;
        public final Value startColor;
        public final int gradientType;
        public final Boolean useLevel;

        private Gradient(ObjectValue gradient, Context context) {
            angle = gradient.getAsInteger(ANGLE);
            centerX = gradient.getAsFloat(CENTER_X);
            centerY = gradient.getAsFloat(CENTER_Y);
            centerColor = ColorResourceProcessor.staticParse(gradient.get(CENTER_COLOR), context);
            endColor = ColorResourceProcessor.staticParse(gradient.get(END_COLOR), context);
            gradientRadius = DimensionAttributeProcessor.staticParse(gradient.get(GRADIENT_RADIUS), context);
            startColor = ColorResourceProcessor.staticParse(gradient.get(START_COLOR), context);
            gradientType = getGradientType(gradient.getAsString(GRADIENT_TYPE));
            useLevel = gradient.getAsBoolean(USE_LEVEL);
        }

        public static Gradient valueOf(ObjectValue value, Context context) {
            return new Gradient(value, context);
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
        public void apply(ProteusView view, final GradientDrawable drawable) {
            if (null != centerX && null != centerY) {
                drawable.setGradientCenter(centerX, centerY);
            }

            if (null != gradientRadius) {
                drawable.setGradientRadius(DimensionAttributeProcessor.evaluate(gradientRadius, view));
            }

            if (GRADIENT_TYPE_NONE != gradientType) {
                drawable.setGradientType(gradientType);
            }
        }

        private int getGradientType(@Nullable String type) {
            if (null == type) {
                return GRADIENT_TYPE_NONE;
            }
            switch (type) {
                case LINEAR_GRADIENT:
                    return GradientDrawable.LINEAR_GRADIENT;
                case RADIAL_GRADIENT:
                    return GradientDrawable.RADIAL_GRADIENT;
                case SWEEP_GRADIENT:
                    return GradientDrawable.SWEEP_GRADIENT;
                default:
                    return GRADIENT_TYPE_NONE;
            }
        }

        public GradientDrawable init(ProteusView view) {
            int[] colors;
            if (centerColor != null) {
                colors = new int[3];
                colors[0] = ColorResourceProcessor.evaluate(startColor, view).color;
                colors[1] = ColorResourceProcessor.evaluate(centerColor, view).color;
                colors[2] = ColorResourceProcessor.evaluate(endColor, view).color;
            } else {
                colors = new int[2];
                colors[0] = ColorResourceProcessor.evaluate(startColor, view).color;
                colors[1] = ColorResourceProcessor.evaluate(endColor, view).color;
            }

            return init(colors, angle);
        }
    }

    private static class Corners extends DrawableElement {

        public static final String RADIUS = "radius";
        public static final String TOP_LEFT_RADIUS = "topLeftRadius";
        public static final String TOP_RIGHT_RADIUS = "topRightRadius";
        public static final String BOTTOM_LEFT_RADIUS = "bottomLeftRadius";
        public static final String BOTTOM_RIGHT_RADIUS = "bottomRightRadius";

        @Nullable
        private final Value radius;
        @Nullable
        private final Value topLeftRadius;
        @Nullable
        private final Value topRightRadius;
        @Nullable
        private final Value bottomLeftRadius;
        @Nullable
        private final Value bottomRightRadius;

        private Corners(ObjectValue corner, Context context) {
            radius = DimensionAttributeProcessor.staticParse(corner.get(RADIUS), context);
            topLeftRadius = DimensionAttributeProcessor.staticParse(corner.get(TOP_LEFT_RADIUS), context);
            topRightRadius = DimensionAttributeProcessor.staticParse(corner.get(TOP_RIGHT_RADIUS), context);
            bottomLeftRadius = DimensionAttributeProcessor.staticParse(corner.get(BOTTOM_LEFT_RADIUS), context);
            bottomRightRadius = DimensionAttributeProcessor.staticParse(corner.get(BOTTOM_RIGHT_RADIUS), context);
        }

        public static Corners valueOf(ObjectValue corner, Context context) {
            return new Corners(corner, context);
        }

        @Override
        public void apply(ProteusView view, GradientDrawable gradientDrawable) {
            if (null != radius) {
                gradientDrawable.setCornerRadius(DimensionAttributeProcessor.evaluate(radius, view));
            }

            float fTopLeftRadius = DimensionAttributeProcessor.evaluate(topLeftRadius, view);
            float fTopRightRadius = DimensionAttributeProcessor.evaluate(topRightRadius, view);
            float fBottomRightRadius = DimensionAttributeProcessor.evaluate(bottomRightRadius, view);
            float fBottomLeftRadius = DimensionAttributeProcessor.evaluate(bottomLeftRadius, view);

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

    private static class Solid extends DrawableElement {

        public static final String COLOR = "color";

        private final Value color;

        private Solid(ObjectValue value, Context context) {
            color = ColorResourceProcessor.staticParse(value.get(COLOR), context);
        }

        public static Solid valueOf(ObjectValue value, Context context) {
            return new Solid(value, context);
        }

        @Override
        public void apply(ProteusView view, final GradientDrawable gradientDrawable) {
            com.flipkart.android.proteus.Color.Result result = ColorResourceProcessor.evaluate(color, view);
            if (null != result.colors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                gradientDrawable.setColor(result.colors);
            } else {
                gradientDrawable.setColor(result.color);
            }
        }
    }

    private static class Size extends DrawableElement {

        private static final String WIDTH = "width";
        private static final String HEIGHT = "height";

        private final Value width;
        private final Value height;

        private Size(ObjectValue value, Context context) {
            width = DimensionAttributeProcessor.staticParse(value.get(WIDTH), context);
            height = DimensionAttributeProcessor.staticParse(value.get(HEIGHT), context);
        }

        public static Size valueOf(ObjectValue value, Context context) {
            return new Size(value, context);
        }

        @Override
        public void apply(ProteusView view, GradientDrawable gradientDrawable) {
            gradientDrawable.setSize((int) DimensionAttributeProcessor.evaluate(width, view), (int) DimensionAttributeProcessor.evaluate(height, view));
        }
    }

    private static class Stroke extends DrawableElement {

        public static final String WIDTH = "width";
        public static final String COLOR = "color";
        public static final String DASH_WIDTH = "dashWidth";
        public static final String DASH_GAP = "dashGap";

        public final Value width;
        public final Value color;
        public final Value dashWidth;
        public final Value dashGap;

        private Stroke(ObjectValue stroke, Context context) {
            width = DimensionAttributeProcessor.staticParse(stroke.get(WIDTH), context);
            color = ColorResourceProcessor.staticParse(stroke.get(COLOR), context);
            dashWidth = DimensionAttributeProcessor.staticParse(stroke.get(DASH_WIDTH), context);
            dashGap = DimensionAttributeProcessor.staticParse(stroke.get(DASH_GAP), context);
        }

        public static Stroke valueOf(ObjectValue stroke, Context context) {
            return new Stroke(stroke, context);
        }

        @Override
        public void apply(ProteusView view, GradientDrawable gradientDrawable) {
            if (null == dashWidth) {
                gradientDrawable.setStroke((int) DimensionAttributeProcessor.evaluate(width, view), ColorResourceProcessor.evaluate(color, view).color);
            } else if (null != dashGap) {
                gradientDrawable.setStroke((int) DimensionAttributeProcessor.evaluate(width, view),
                        ColorResourceProcessor.evaluate(color, view).color,
                        DimensionAttributeProcessor.evaluate(dashWidth, view),
                        DimensionAttributeProcessor.evaluate(dashGap, view));
            }
        }
    }

}
