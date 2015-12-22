package com.flipkart.layoutengine.parser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flipkart.layoutengine.EventType;
import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.builder.LayoutBuilder;
import com.flipkart.layoutengine.processor.DimensionAttributeProcessor;
import com.flipkart.layoutengine.processor.DrawableResourceProcessor;
import com.flipkart.layoutengine.processor.EventProcessor;
import com.flipkart.layoutengine.processor.JsonDataProcessor;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;
import com.flipkart.layoutengine.processor.TweenAnimationResourceProcessor;
import com.flipkart.layoutengine.provider.ProteusConstants;
import com.flipkart.layoutengine.toolbox.IdGenerator;
import com.flipkart.layoutengine.toolbox.Styles;
import com.flipkart.layoutengine.toolbox.Utils;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kiran.kumar
 */
public class ViewParser<V extends View> extends Parser<V> {

    private Logger logger = LoggerFactory.getLogger(ViewParser.class);

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
        addHandler(Attributes.View.Background, new DrawableResourceProcessor<V>(context) {
            @Override
            public void setDrawable(V view, Drawable drawable) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    //noinspection deprecation
                    view.setBackgroundDrawable(drawable);
                } else {
                    view.setBackground(drawable);
                }
            }
        });
        addHandler(Attributes.View.Height, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(ParserContext parserContext, int dimension, V view, String attributeKey, JsonElement attributeValue, ProteusView proteusView, JsonObject layout, int index) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = dimension;
                view.setLayoutParams(layoutParams);
            }
        });
        addHandler(Attributes.View.Width, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(ParserContext parserContext, int dimension, V view, String attributeKey, JsonElement attributeValue, ProteusView proteusView, JsonObject layout, int index) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = dimension;
                view.setLayoutParams(layoutParams);
            }
        });
        addHandler(Attributes.View.Weight, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                LinearLayout.LayoutParams layoutParams;
                if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                    layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                    layoutParams.weight = ParseHelper.parseFloat(attributeValue);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (logger.isErrorEnabled()) {
                        logger.error(attributeKey + " is only supported for LinearLayouts");
                    }
                }
            }
        });
        addHandler(Attributes.View.LayoutGravity, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

                if (layoutParams instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams linearLayoutParams = (LinearLayout.LayoutParams) layoutParams;
                    //noinspection ResourceType
                    linearLayoutParams.gravity = ParseHelper.parseGravity(attributeValue);
                    view.setLayoutParams(layoutParams);
                } else if (layoutParams instanceof FrameLayout.LayoutParams) {
                    FrameLayout.LayoutParams linearLayoutParams = (FrameLayout.LayoutParams) layoutParams;
                    linearLayoutParams.gravity = ParseHelper.parseGravity(attributeValue);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (logger.isErrorEnabled()) {
                        logger.error(attributeKey + " is only supported for LinearLayout and FrameLayout");
                    }
                }
            }
        });
        addHandler(Attributes.View.Padding, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(ParserContext parserContext, int dimension, V view, String attributeKey, JsonElement attributeValue, ProteusView proteusView, JsonObject layout, int index) {
                view.setPadding(dimension, dimension, dimension, dimension);
            }
        });
        addHandler(Attributes.View.PaddingLeft, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(ParserContext parserContext, int dimension, V view, String attributeKey, JsonElement attributeValue, ProteusView proteusView, JsonObject layout, int index) {
                view.setPadding(dimension, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            }
        });
        addHandler(Attributes.View.PaddingTop, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(ParserContext parserContext, int dimension, V view, String attributeKey, JsonElement attributeValue, ProteusView proteusView, JsonObject layout, int index) {
                view.setPadding(view.getPaddingLeft(), dimension, view.getPaddingRight(), view.getPaddingBottom());
            }
        });
        addHandler(Attributes.View.PaddingRight, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(ParserContext parserContext, int dimension, V view, String attributeKey, JsonElement attributeValue, ProteusView proteusView, JsonObject layout, int index) {
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), dimension, view.getPaddingBottom());
            }
        });
        addHandler(Attributes.View.PaddingBottom, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(ParserContext parserContext, int dimension, V view, String attributeKey, JsonElement attributeValue, ProteusView proteusView, JsonObject layout, int index) {
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), dimension);
            }
        });
        addHandler(Attributes.View.Margin, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(ParserContext parserContext, int dimension, V view, String attributeKey, JsonElement attributeValue, ProteusView proteusView, JsonObject layout, int index) {
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins(dimension, dimension, dimension, dimension);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (logger.isErrorEnabled()) {
                        logger.error("margins can only be applied to views with parent ViewGroup");
                    }
                }
            }
        });
        addHandler(Attributes.View.MarginLeft, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(ParserContext parserContext, int dimension, V view, String attributeKey, JsonElement attributeValue, ProteusView proteusView, JsonObject layout, int index) {
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins(dimension, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (logger.isErrorEnabled()) {
                        logger.error("margins can only be applied to views with parent ViewGroup");
                    }
                }
            }
        });
        addHandler(Attributes.View.MarginTop, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(ParserContext parserContext, int dimension, V view, String attributeKey, JsonElement attributeValue, ProteusView proteusView, JsonObject layout, int index) {
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, dimension, layoutParams.rightMargin, layoutParams.bottomMargin);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (logger.isErrorEnabled()) {
                        logger.error("margins can only be applied to views with parent ViewGroup");
                    }
                }
            }
        });
        addHandler(Attributes.View.MarginRight, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(ParserContext parserContext, int dimension, V view, String attributeKey, JsonElement attributeValue, ProteusView proteusView, JsonObject layout, int index) {
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, dimension, layoutParams.bottomMargin);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (logger.isErrorEnabled()) {
                        logger.error("margins can only be applied to views with parent ViewGroup");
                    }
                }
            }
        });
        addHandler(Attributes.View.MarginBottom, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(ParserContext parserContext, int dimension, V view, String attributeKey, JsonElement attributeValue, ProteusView proteusView, JsonObject layout, int index) {
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, dimension);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (logger.isErrorEnabled()) {
                        logger.error("margins can only be applied to views with parent ViewGroup");
                    }
                }
            }
        });

        addHandler(Attributes.View.MinHeight, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(ParserContext parserContext, int dimension, V view, String attributeKey, JsonElement attributeValue, ProteusView proteusView, JsonObject layout, int index) {
                view.setMinimumHeight(dimension);
            }
        });

        addHandler(Attributes.View.MinWidth, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(ParserContext parserContext, int dimension, V view, String attributeKey, JsonElement attributeValue, ProteusView proteusView, JsonObject layout, int index) {
                view.setMinimumWidth(dimension);
            }
        });

        addHandler(Attributes.View.Elevation, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(ParserContext parserContext, int dimension, V view, String attributeKey, JsonElement attributeValue, ProteusView proteusView, JsonObject layout, int index) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setElevation(dimension);
                }
            }
        });
        addHandler(Attributes.View.Alpha, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                view.setAlpha(ParseHelper.parseFloat(attributeValue));
            }
        });
        addHandler(Attributes.View.Visibility, new JsonDataProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue, V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                // noinspection ResourceType
                view.setVisibility(ParseHelper.parseVisibility(attributeValue));
            }
        });
        addHandler(Attributes.View.Invisibility, new JsonDataProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue, V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                // noinspection ResourceType
                view.setVisibility(ParseHelper.parseInvisibility(attributeValue));
            }
        });
        addHandler(Attributes.View.Id, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                view.setId(IdGenerator.getInstance().getUnique(attributeValue));

                // set view id resource name
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    final String resourceName = attributeValue;
                    view.setAccessibilityDelegate(new View.AccessibilityDelegate() {
                        @Override
                        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                            super.onInitializeAccessibilityNodeInfo(host, info);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                info.setViewIdResourceName(resourceName);
                            }
                        }
                    });
                }
            }
        });
        addHandler(Attributes.View.ContentDescription, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                view.setContentDescription(attributeValue);
            }
        });
        addHandler(Attributes.View.Clickable, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                boolean clickable = ParseHelper.parseBoolean(attributeValue);
                view.setClickable(clickable);
            }
        });
        addHandler(Attributes.View.Tag, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                view.setTag(attributeValue);
            }
        });
        addHandler(Attributes.View.Border, new JsonDataProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, JsonElement attributeValue, V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                Drawable border = Utils.getBorderDrawble(attributeValue, context);
                if (border == null) {
                    return;
                }

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    //noinspection deprecation
                    view.setBackgroundDrawable(border);
                } else {
                    view.setBackground(border);
                }
            }
        });

        addHandler(Attributes.View.Enabled, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                boolean enabled = ParseHelper.parseBoolean(attributeValue);
                view.setEnabled(enabled);
            }
        });

        addHandler(Attributes.View.Style, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                Styles styles = parserContext.getStyles();
                LayoutHandler handler = parserContext.getLayoutBuilder().getHandler(Utils.getPropertyAsString(layout, ProteusConstants.TYPE));
                if (styles == null) {
                    return;
                }
                String[] styleSet = attributeValue.split(ProteusConstants.STYLE_DELIMITER);
                for (String styleName : styleSet) {
                    if (styles.contains(styleName)) {
                        process(styles.getStyle(styleName), layout, proteusView, (handler != null ? handler : ViewParser.this),
                                parserContext.getLayoutBuilder(), parserContext, parent, index);
                    }
                }
            }

            private void process(Map<String, JsonElement> style, JsonObject layout, ProteusView proteusView, LayoutHandler handler,
                                 LayoutBuilder builder, ParserContext parserContext, ProteusView parent, int index) {
                for (Map.Entry<String, JsonElement> attribute : style.entrySet()) {
                    if (layout.has(attribute.getKey())) {
                        continue;
                    }
                    builder.handleAttribute(handler, parserContext, attribute.getKey(), attribute.getValue(),
                            layout, proteusView, parent, index);
                }
            }
        });

        addHandler(Attributes.View.TransitionName, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setTransitionName(attributeValue);
                }
            }
        });

        addHandler(Attributes.View.RequiresFadingEdge, new StringAttributeProcessor<V>() {

            private final String NONE = "none";
            private final String BOTH = "both";
            private final String VERTICAL = "vertical";
            private final String HORIZONTAL = "horizontal";

            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue,
                               V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {

                switch (attributeValue) {
                    case NONE:
                        view.setVerticalFadingEdgeEnabled(false);
                        view.setHorizontalFadingEdgeEnabled(false);
                        break;
                    case BOTH:
                        view.setVerticalFadingEdgeEnabled(true);
                        view.setHorizontalFadingEdgeEnabled(true);
                        break;
                    case VERTICAL:
                        view.setVerticalFadingEdgeEnabled(true);
                        view.setHorizontalFadingEdgeEnabled(false);
                        break;
                    case HORIZONTAL:
                        view.setVerticalFadingEdgeEnabled(false);
                        view.setHorizontalFadingEdgeEnabled(true);
                        break;
                    default:
                        view.setVerticalFadingEdgeEnabled(false);
                        view.setHorizontalFadingEdgeEnabled(false);
                        break;
                }
            }
        });

        addHandler(Attributes.View.FadingEdgeLength, new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue,
                               V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                view.setFadingEdgeLength(ParseHelper.parseInt(attributeValue));
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
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
                int id = IdGenerator.getInstance().getUnique(attributeValue);
                Integer rule = relativeLayoutParams.get(attributeKey);
                ParseHelper.addRelativeLayoutRule(view, rule, id);
            }
        };

        StringAttributeProcessor<V> relativeLayoutBooleanProcessor = new StringAttributeProcessor<V>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, V view, ProteusView proteusView, ProteusView parent, JsonObject layout, int index) {
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


        addHandler(Attributes.View.Animation, new TweenAnimationResourceProcessor<V>(context) {

            @Override
            public void setAnimation(V view, Animation animation) {
                view.setAnimation(animation);
            }
        });
    }
}
