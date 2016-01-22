package com.flipkart.layoutengine.processor;

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
import android.util.Pair;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.toolbox.ColorUtils;
import com.flipkart.layoutengine.toolbox.NetworkDrawableHelper;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.Gson;
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

    private static Gson sGson = new Gson();

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
        public String radius;
        public String topLeftRadius;
        public String topRightRadius;
        public String bottomLeftRadius;
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
        public Integer angle;
        public Float centerX;
        public Float centerY;
        public JsonElement centerColor;
        public JsonElement endColor;
        public Float gradientRadius;
        public JsonElement startColor;
        public String gradientType;
        public Boolean useLevel;

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

        public static GradientDrawable init(@Nullable int[] colors, @Nullable Integer angle) {
            return colors != null ? new GradientDrawable(getOrientation(angle), colors) : new GradientDrawable();
        }
    }


    private static class Size extends GradientDrawableElement {
        public String width;
        public String height;

        @Override
        public void apply(Context context, GradientDrawable gradientDrawable) {
            gradientDrawable.setSize((int) ParseHelper.parseDimension(width, context), (int) ParseHelper.parseDimension(height, context));
        }
    }

    private static class Stroke extends GradientDrawableElement {
        public String width;
        public JsonElement color;
        public String dashWidth;
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
        public Integer minLevel;
        public Integer maxLevel;
        public JsonElement drawable;

        public void addItem(Context context, final LevelListDrawable levelListDrawable) {
            DrawableResourceProcessor<View> processor = new DrawableResourceProcessor<View>(context) {
                @Override
                public void setDrawable(View view, Drawable drawable) {
                    levelListDrawable.addLevel(minLevel, maxLevel, drawable);
                }
            };
        }
    }

    private static class ShapeDrawableJson {
        public String shape;
        public String innerRadius;
        public Float innerRadiusRatio;
        public String thickness;
        public Float thicknessRatio;
        public JsonArray children;

        public GradientDrawable init(Context context, @Nullable GradientDrawable useGradient) {
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

            GradientDrawable gradientDrawable = (null != gradient) ? gradient.init(context) : (null != useGradient) ? useGradient : new GradientDrawable();

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
                break;
            case DRAWABLE_LEVEL_LIST:
                final LevelListDrawable levelListDrawable = new LevelListDrawable();
                childrenElement = jsonObject.get(CHILDREN);
                if (childrenElement != null) {
                    JsonArray children = childrenElement.getAsJsonArray();
                    for (JsonElement childElement : children) {
                        LayerListDrawableItem layerListDrawableItem = sGson.fromJson(childElement, LayerListDrawableItem.class);
                        layerListDrawableItem.addItem(context, levelListDrawable);
                    }
                }
                break;
        }
    }

    private static GradientDrawable loadGradientDrawable(Context context, JsonObject value) {
        ShapeDrawableJson shapeDrawable = sGson.fromJson(value, ShapeDrawableJson.class);
        return shapeDrawable.init(context, null);
    }

    /**
     * @param Context  Context object
     * @param JsonObject Json representation of the gradient drawable
     * @param colors  [startColor, centerColor, endColor] or [startColor, endColor]. This can be null
     * @param angle  angle. This can be null
     */
    public static GradientDrawable loadShapeDrawable(Context context, JsonObject value, @Nullable int[] colors, Integer angle) {
        ShapeDrawableJson shapeDrawable = sGson.fromJson(value, ShapeDrawableJson.class);
        return shapeDrawable.init(context, Gradient.init(colors, angle));
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
