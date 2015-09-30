package com.flipkart.layoutengine.parser;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flipkart.layoutengine.EventType;
import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.processor.EventProcessor;
import com.flipkart.layoutengine.processor.JsonDataProcessor;
import com.flipkart.layoutengine.processor.ResourceReferenceProcessor;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;
import com.flipkart.layoutengine.toolbox.IdGenerator;
import com.flipkart.layoutengine.toolbox.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;

/**
 * @author kiran.kumar
 */
public class ViewParser<V extends View> extends Parser<V> {

    public static final String ATTRIBUTE_BORDER_WIDTH = "width";
    public static final String ATTRIBUTE_BORDER_COLOR = "color";
    public static final String ATTRIBUTE_BORDER_RADIUS = "radius";
    public static final String ATTRIBUTE_BG_COLOR = "bgColor";

    public ViewParser(Class viewClass) {
        super(viewClass);
    }

    protected void prepareHandlers(final Context context) {

        addHandler(Attributes.View.OnClick, new EventProcessor<V>(context) {
            @Override
            public void setOnEventListener(final V view, final ParserContext parserContext, final JsonElement attributeValue) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fireEvent(view, parserContext, EventType.OnClick, attributeValue);
                    }
                });
            }
        });
        addHandler(Attributes.View.Background, new ResourceReferenceProcessor<V>(context) {
            @Override
            public void setDrawable(V view, Drawable drawable) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackgroundDrawable(drawable);
                } else {
                    view.setBackground(drawable);
                }
            }
        });
        addHandler(Attributes.View.Height, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = ParseHelper.parseDimension(attributeValue, context);
                view.setLayoutParams(layoutParams);
            }
        });
        addHandler(Attributes.View.Width, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = ParseHelper.parseDimension(attributeValue, context);
                view.setLayoutParams(layoutParams);
            }
        });
        addHandler(Attributes.View.Weight, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                LinearLayout.LayoutParams layoutParams;
                if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                    layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                    layoutParams.weight = ParseHelper.parseFloat(attributeValue);
                    view.setLayoutParams(layoutParams);
                } else {
                    Log.e(Utils.TAG_ERROR, attributeKey + " is only supported for LinearLayouts");
                }
            }
        });
        addHandler(Attributes.View.LayoutGravity, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

                if (layoutParams instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams linearLayoutParams = (LinearLayout.LayoutParams) layoutParams;
                    linearLayoutParams.gravity = ParseHelper.parseGravity(attributeValue);
                    view.setLayoutParams(layoutParams);
                } else if (layoutParams instanceof FrameLayout.LayoutParams) {
                    FrameLayout.LayoutParams linearLayoutParams = (FrameLayout.LayoutParams) layoutParams;
                    linearLayoutParams.gravity = ParseHelper.parseGravity(attributeValue);
                    view.setLayoutParams(layoutParams);
                } else {
                    Log.e(Utils.TAG_ERROR, attributeKey + " is only supported for LinearLayout and FrameLayout");
                }
            }
        });
        addHandler(Attributes.View.Padding, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                int dimension = ParseHelper.parseDimension(attributeValue, context);
                view.setPadding(dimension, dimension, dimension, dimension);
            }
        });
        addHandler(Attributes.View.PaddingLeft, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                int dimension = ParseHelper.parseDimension(attributeValue, context);
                view.setPadding(dimension, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            }
        });
        addHandler(Attributes.View.PaddingTop, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                int dimension = ParseHelper.parseDimension(attributeValue, context);
                view.setPadding(view.getPaddingLeft(), dimension, view.getPaddingRight(), view.getPaddingBottom());
            }
        });
        addHandler(Attributes.View.PaddingRight, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                int dimension = ParseHelper.parseDimension(attributeValue, context);
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), dimension, view.getPaddingBottom());
            }
        });
        addHandler(Attributes.View.PaddingBottom, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                int dimension = ParseHelper.parseDimension(attributeValue, context);
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), dimension);
            }
        });
        addHandler(Attributes.View.Margin, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                int dimension = ParseHelper.parseDimension(attributeValue, context);
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins(dimension, dimension, dimension, dimension);
                    view.setLayoutParams(layoutParams);
                } else {
                    Log.e(Utils.TAG_ERROR, "margins can only be applied to views with parent ViewGroup");
                }
            }
        });
        addHandler(Attributes.View.MarginLeft, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                int dimension = ParseHelper.parseDimension(attributeValue, context);
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins(dimension, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
                    view.setLayoutParams(layoutParams);
                } else {
                    Log.e(Utils.TAG_ERROR, "margins can only be applied to views with parent ViewGroup");
                }
            }
        });
        addHandler(Attributes.View.MarginTop, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                int dimension = ParseHelper.parseDimension(attributeValue, context);
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, dimension, layoutParams.rightMargin, layoutParams.bottomMargin);
                    view.setLayoutParams(layoutParams);
                } else {
                    Log.e(Utils.TAG_ERROR, "margins can only be applied to views with parent ViewGroup");
                }
            }
        });
        addHandler(Attributes.View.MarginRight, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                int dimension = ParseHelper.parseDimension(attributeValue, context);
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, dimension, layoutParams.bottomMargin);
                    view.setLayoutParams(layoutParams);
                } else {
                    Log.e(Utils.TAG_ERROR, "margins can only be applied to views with parent ViewGroup");
                }
            }
        });
        addHandler(Attributes.View.MarginBottom, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                int dimension = ParseHelper.parseDimension(attributeValue, context);
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, dimension);
                    view.setLayoutParams(layoutParams);
                } else {
                    Log.e(Utils.TAG_ERROR, "margins can only be applied to views with parent ViewGroup");
                }
            }
        });
        addHandler(Attributes.View.Alpha, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                view.setAlpha(ParseHelper.parseFloat(attributeValue));
            }
        });
        addHandler(Attributes.View.Visibility, new JsonDataProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue, V view, JsonObject layout) {
                // noinspection ResourceType
                view.setVisibility(ParseHelper.parseVisibility(attributeValue));
            }
        });
        addHandler(Attributes.View.Invisibility, new JsonDataProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue, V view, JsonObject layout) {
                // noinspection ResourceType
                view.setVisibility(ParseHelper.parseInvisibility(attributeValue));
            }
        });
        addHandler(Attributes.View.Id, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                view.setId(IdGenerator.getInstance().getUnique(attributeValue));
            }
        });
        addHandler(Attributes.View.ContentDescription, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                view.setContentDescription(attributeValue);
            }
        });
        addHandler(Attributes.View.Clickable, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                boolean clickable = ParseHelper.parseBoolean(attributeValue);
                view.setClickable(clickable);
            }
        });
        addHandler(Attributes.View.Tag, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                view.setTag(attributeValue);
            }
        });
        addHandler(Attributes.View.Border, new JsonDataProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue, V view, JsonObject layout) {
                if (!attributeValue.isJsonObject() || attributeValue.isJsonNull()) {
                    return;
                }

                int cornerRadius = 0, borderWidth = 0, borderColor = Color.TRANSPARENT, bgColor = Color.TRANSPARENT;
                JsonObject data = attributeValue.getAsJsonObject();

                String value = Utils.getPropertyAsString(data, ATTRIBUTE_BG_COLOR);
                if (value != null && !value.equals("-1")) {
                    bgColor = ParseHelper.parseColor(value);
                }

                value = Utils.getPropertyAsString(data, ATTRIBUTE_BORDER_COLOR);
                if (value != null) {
                    borderColor = ParseHelper.parseColor(value);
                }

                value = Utils.getPropertyAsString(data, ATTRIBUTE_BORDER_RADIUS);
                if (value != null) {
                    cornerRadius = ParseHelper.parseDimension(value, context);
                }

                value = Utils.getPropertyAsString(data, ATTRIBUTE_BORDER_WIDTH);
                if (value != null) {
                    borderWidth = ParseHelper.parseDimension(value, context);
                }

                GradientDrawable border = new GradientDrawable();
                border.setCornerRadius(cornerRadius);
                border.setShape(GradientDrawable.RECTANGLE);
                border.setStroke(borderWidth, borderColor);
                border.setColor(bgColor);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackgroundDrawable(border);
                } else {
                    view.setBackground(border);
                }
            }
        });

        addHandler(Attributes.View.Enable, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                boolean enabled = ParseHelper.parseBoolean(attributeValue);
                view.setEnabled(enabled);
            }
        });

        final HashMap<String, Integer> relativeLayoutParams = new HashMap<>();
        relativeLayoutParams.put(Attributes.View.Above.getName(), RelativeLayout.ABOVE);
        relativeLayoutParams.put(Attributes.View.AlignBaseline.getName(), RelativeLayout.ALIGN_BASELINE);
        relativeLayoutParams.put(Attributes.View.AlignBottom.getName(), RelativeLayout.ALIGN_BOTTOM);
        relativeLayoutParams.put(Attributes.View.AlignEnd.getName(), RelativeLayout.ALIGN_END);
        relativeLayoutParams.put(Attributes.View.AlignLeft.getName(), RelativeLayout.ALIGN_LEFT);
        relativeLayoutParams.put(Attributes.View.AlignParentBottom.getName(), RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeLayoutParams.put(Attributes.View.AlignParentEnd.getName(), RelativeLayout.ALIGN_PARENT_END);
        relativeLayoutParams.put(Attributes.View.AlignParentLeft.getName(), RelativeLayout.ALIGN_PARENT_LEFT);
        relativeLayoutParams.put(Attributes.View.AlignParentRight.getName(), RelativeLayout.ALIGN_PARENT_RIGHT);
        relativeLayoutParams.put(Attributes.View.AlignParentStart.getName(), RelativeLayout.ALIGN_PARENT_START);
        relativeLayoutParams.put(Attributes.View.AlignParentTop.getName(), RelativeLayout.ALIGN_PARENT_TOP);
        relativeLayoutParams.put(Attributes.View.AlignRight.getName(), RelativeLayout.ALIGN_RIGHT);
        relativeLayoutParams.put(Attributes.View.AlignStart.getName(), RelativeLayout.ALIGN_START);
        relativeLayoutParams.put(Attributes.View.AlignTop.getName(), RelativeLayout.ALIGN_TOP);

        relativeLayoutParams.put(Attributes.View.Below.getName(), RelativeLayout.BELOW);
        relativeLayoutParams.put(Attributes.View.CenterHorizontal.getName(), RelativeLayout.CENTER_HORIZONTAL);
        relativeLayoutParams.put(Attributes.View.CenterInParent.getName(), RelativeLayout.CENTER_IN_PARENT);
        relativeLayoutParams.put(Attributes.View.CenterVertical.getName(), RelativeLayout.CENTER_VERTICAL);
        relativeLayoutParams.put(Attributes.View.ToEndOf.getName(), RelativeLayout.END_OF);
        relativeLayoutParams.put(Attributes.View.ToLeftOf.getName(), RelativeLayout.LEFT_OF);
        relativeLayoutParams.put(Attributes.View.ToRightOf.getName(), RelativeLayout.RIGHT_OF);
        relativeLayoutParams.put(Attributes.View.ToStartOf.getName(), RelativeLayout.START_OF);

        StringAttributeProcessor<V> relativeLayoutProcessor = new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                int id = IdGenerator.getInstance().getUnique(attributeValue);
                Integer rule = relativeLayoutParams.get(attributeKey);
                ParseHelper.addRelativeLayoutRule(view, rule, id);
            }
        };

        StringAttributeProcessor<V> relativeLayoutBooleanProcessor = new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, JsonObject layout) {
                int trueOrFalse = ParseHelper.parseRelativeLayoutBoolean(attributeValue);
                ParseHelper.addRelativeLayoutRule(view, relativeLayoutParams.get(attributeKey), trueOrFalse);
            }
        };

        addHandler(Attributes.View.Above, relativeLayoutProcessor);
        addHandler(Attributes.View.AlignBaseline, relativeLayoutProcessor);
        addHandler(Attributes.View.AlignBottom, relativeLayoutProcessor);
        addHandler(Attributes.View.AlignEnd, relativeLayoutProcessor);
        addHandler(Attributes.View.AlignLeft, relativeLayoutProcessor);
        addHandler(Attributes.View.AlignRight, relativeLayoutProcessor);
        addHandler(Attributes.View.AlignStart, relativeLayoutProcessor);
        addHandler(Attributes.View.AlignTop, relativeLayoutProcessor);
        addHandler(Attributes.View.Below, relativeLayoutProcessor);
        addHandler(Attributes.View.ToEndOf, relativeLayoutProcessor);
        addHandler(Attributes.View.ToLeftOf, relativeLayoutProcessor);
        addHandler(Attributes.View.ToRightOf, relativeLayoutProcessor);
        addHandler(Attributes.View.ToStartOf, relativeLayoutProcessor);


        addHandler(Attributes.View.AlignParentBottom, relativeLayoutBooleanProcessor);
        addHandler(Attributes.View.AlignParentEnd, relativeLayoutBooleanProcessor);
        addHandler(Attributes.View.AlignParentLeft, relativeLayoutBooleanProcessor);
        addHandler(Attributes.View.AlignParentRight, relativeLayoutBooleanProcessor);
        addHandler(Attributes.View.AlignParentStart, relativeLayoutBooleanProcessor);
        addHandler(Attributes.View.AlignParentTop, relativeLayoutBooleanProcessor);
        addHandler(Attributes.View.CenterHorizontal, relativeLayoutBooleanProcessor);
        addHandler(Attributes.View.CenterInParent, relativeLayoutBooleanProcessor);
        addHandler(Attributes.View.CenterVertical, relativeLayoutBooleanProcessor);


    }
}
