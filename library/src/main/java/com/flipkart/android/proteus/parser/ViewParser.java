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

package com.flipkart.android.proteus.parser;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flipkart.android.proteus.LayoutParser;
import com.flipkart.android.proteus.builder.ProteusLayoutInflater;
import com.flipkart.android.proteus.processor.AttributeProcessor;
import com.flipkart.android.proteus.processor.DimensionAttributeProcessor;
import com.flipkart.android.proteus.processor.DrawableResourceProcessor;
import com.flipkart.android.proteus.processor.EventProcessor;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.processor.TweenAnimationResourceProcessor;
import com.flipkart.android.proteus.toolbox.EventType;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.toolbox.Utils;
import com.flipkart.android.proteus.view.ProteusAndroidView;
import com.flipkart.android.proteus.view.ProteusView;
import com.flipkart.android.proteus.view.manager.ProteusViewManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kiran.kumar
 */
public class ViewParser<V extends View> extends BaseTypeParser<V> {

    private static final String TAG = "ViewParser";

    private static final String ID_STRING_START_PATTERN = "@+id/";
    private static final String ID_STRING_START_PATTERN1 = "@id/";
    private static final String ID_STRING_NORMALIZED_PATTERN = ":id/";

    @Override
    public ProteusView createView(ViewGroup parent, LayoutParser layout, JsonObject data, Styles styles, int index) {
        return new ProteusAndroidView(parent.getContext());
    }

    protected void registerAttributeProcessors() {

        addAttributeProcessor(Attributes.View.OnClick, new EventProcessor<V>() {
            @Override
            public void setOnEventListener(final V view, final LayoutParser parser) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fireEvent((ProteusView) view, EventType.OnClick, parser);
                    }
                });
            }
        });
        addAttributeProcessor(Attributes.View.Background, new DrawableResourceProcessor<V>() {
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
        addAttributeProcessor(Attributes.View.Height, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, String key, float dimension, LayoutParser parser) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.height = (int) dimension;
                    view.setLayoutParams(layoutParams);
                }
            }
        });
        addAttributeProcessor(Attributes.View.Width, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, String key, float dimension, LayoutParser parser) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.width = (int) dimension;
                    view.setLayoutParams(layoutParams);
                }
            }
        });
        addAttributeProcessor(Attributes.View.Weight, new StringAttributeProcessor<V>() {
            @Override
            public void handle(String attributeKey, String attributeValue, V view) {
                LinearLayout.LayoutParams layoutParams;
                if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                    layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                    layoutParams.weight = ParseHelper.parseFloat(attributeValue);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (ProteusConstants.isLoggingEnabled()) {
                        Log.e(TAG, attributeKey + " is only supported for LinearLayouts");
                    }
                }
            }
        });
        addAttributeProcessor(Attributes.View.LayoutGravity, new StringAttributeProcessor<V>() {
            @Override
            public void handle(String attributeKey, String attributeValue, V view) {
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
                    if (ProteusConstants.isLoggingEnabled()) {
                        Log.e(TAG, attributeKey + " is only supported for LinearLayout and FrameLayout");
                    }
                }
            }
        });
        addAttributeProcessor(Attributes.View.Padding, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, String key, float dimension, LayoutParser parser) {
                view.setPadding((int) dimension, (int) dimension, (int) dimension, (int) dimension);
            }
        });
        addAttributeProcessor(Attributes.View.PaddingLeft, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, String key, float dimension, LayoutParser parser) {
                view.setPadding((int) dimension, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            }
        });
        addAttributeProcessor(Attributes.View.PaddingTop, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, String key, float dimension, LayoutParser parser) {
                view.setPadding(view.getPaddingLeft(), (int) dimension, view.getPaddingRight(), view.getPaddingBottom());
            }
        });
        addAttributeProcessor(Attributes.View.PaddingRight, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, String key, float dimension, LayoutParser parser) {
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), (int) dimension, view.getPaddingBottom());
            }
        });
        addAttributeProcessor(Attributes.View.PaddingBottom, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, String key, float dimension, LayoutParser parser) {
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), (int) dimension);
            }
        });
        addAttributeProcessor(Attributes.View.Margin, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, String key, float dimension, LayoutParser parser) {
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins((int) dimension, (int) dimension, (int) dimension, (int) dimension);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (ProteusConstants.isLoggingEnabled()) {
                        Log.e(TAG, "margins can only be applied to views with parent ViewGroup");
                    }
                }
            }
        });
        addAttributeProcessor(Attributes.View.MarginLeft, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, String key, float dimension, LayoutParser parser) {
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins((int) dimension, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (ProteusConstants.isLoggingEnabled()) {
                        Log.e(TAG, "margins can only be applied to views with parent ViewGroup");
                    }
                }
            }
        });
        addAttributeProcessor(Attributes.View.MarginTop, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, String key, float dimension, LayoutParser parser) {
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, (int) dimension, layoutParams.rightMargin, layoutParams.bottomMargin);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (ProteusConstants.isLoggingEnabled()) {
                        Log.e(TAG, "margins can only be applied to views with parent ViewGroup");
                    }
                }
            }
        });
        addAttributeProcessor(Attributes.View.MarginRight, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, String key, float dimension, LayoutParser parser) {
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, (int) dimension, layoutParams.bottomMargin);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (ProteusConstants.isLoggingEnabled()) {
                        Log.e(TAG, "margins can only be applied to views with parent ViewGroup");
                    }
                }
            }
        });
        addAttributeProcessor(Attributes.View.MarginBottom, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, String key, float dimension, LayoutParser parser) {
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams;
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, (int) dimension);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (ProteusConstants.isLoggingEnabled()) {
                        Log.e(TAG, "margins can only be applied to views with parent ViewGroup");
                    }
                }
            }
        });

        addAttributeProcessor(Attributes.View.MinHeight, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, String key, float dimension, LayoutParser parser) {
                view.setMinimumHeight((int) dimension);
            }
        });

        addAttributeProcessor(Attributes.View.MinWidth, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, String key, float dimension, LayoutParser parser) {
                view.setMinimumWidth((int) dimension);
            }
        });

        addAttributeProcessor(Attributes.View.Elevation, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, String key, float dimension, LayoutParser parser) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setElevation(dimension);
                }
            }
        });
        addAttributeProcessor(Attributes.View.Alpha, new StringAttributeProcessor<V>() {
            @Override
            public void handle(String attributeKey, String attributeValue, V view) {
                view.setAlpha(ParseHelper.parseFloat(attributeValue));
            }
        });
        addAttributeProcessor(Attributes.View.Visibility, new AttributeProcessor<V>() {
            @Override
            public void handle(V view, String key, LayoutParser parser) {
                // noinspection ResourceType
                view.setVisibility(ParseHelper.parseVisibility(parser));
            }
        });
        addAttributeProcessor(Attributes.View.Invisibility, new AttributeProcessor<V>() {
            @Override
            public void handle(V view, String key, LayoutParser parser) {
                // noinspection ResourceType
                view.setVisibility(ParseHelper.parseInvisibility(parser));
            }
        });
        addAttributeProcessor(Attributes.View.Id, new StringAttributeProcessor<V>() {
            @Override
            public void handle(String attributeKey, String attributeValue, final V view) {
                if (view instanceof ProteusView) {
                    view.setId(((ProteusView) view).getViewManager().getUniqueViewId(attributeValue));
                }

                // set view id resource name
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    final String resourceName = attributeValue;
                    view.setAccessibilityDelegate(new View.AccessibilityDelegate() {
                        @Override
                        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                            super.onInitializeAccessibilityNodeInfo(host, info);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                String normalizedResourceName;
                                if (!TextUtils.isEmpty(resourceName)) {
                                    String id;
                                    if (resourceName.startsWith(ID_STRING_START_PATTERN)) {
                                        id = resourceName.substring(ID_STRING_START_PATTERN.length());
                                    } else if (resourceName.startsWith(ID_STRING_START_PATTERN1)) {
                                        id = resourceName.substring(ID_STRING_START_PATTERN1.length());
                                    } else {
                                        id = resourceName;
                                    }
                                    normalizedResourceName = view.getContext().getPackageName() + ID_STRING_NORMALIZED_PATTERN + id;
                                } else {
                                    normalizedResourceName = "";
                                }
                                info.setViewIdResourceName(normalizedResourceName);
                            }
                        }
                    });
                }
            }
        });
        addAttributeProcessor(Attributes.View.ContentDescription, new StringAttributeProcessor<V>() {
            @Override
            public void handle(String attributeKey, String attributeValue, V view) {
                view.setContentDescription(attributeValue);
            }
        });
        addAttributeProcessor(Attributes.View.Clickable, new StringAttributeProcessor<V>() {
            @Override
            public void handle(String attributeKey, String attributeValue, V view) {
                boolean clickable = ParseHelper.parseBoolean(attributeValue);
                view.setClickable(clickable);
            }
        });
        addAttributeProcessor(Attributes.View.Tag, new StringAttributeProcessor<V>() {
            @Override
            public void handle(String attributeKey, String attributeValue, V view) {
                view.setTag(attributeValue);
            }
        });
        addAttributeProcessor(Attributes.View.Border, new AttributeProcessor<V>() {
            @Override
            public void handle(V view, String key, LayoutParser parser) {
                Drawable border = Utils.getBorderDrawable(parser, view.getContext());
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

        addAttributeProcessor(Attributes.View.Enabled, new StringAttributeProcessor<V>() {
            @Override
            public void handle(String attributeKey, String attributeValue, V view) {
                boolean enabled = ParseHelper.parseBoolean(attributeValue);
                view.setEnabled(enabled);
            }
        });

        addAttributeProcessor(Attributes.View.Style, new StringAttributeProcessor<V>() {
            @Override
            public void handle(String attributeKey, String attributeValue, V view) {
                ProteusViewManager viewManager = ((ProteusView) view).getViewManager();
                LayoutParser parser = viewManager.getLayoutParser();
                Styles styles = viewManager.getStyles();

                TypeParser handler = viewManager.getProteusLayoutInflater().getParser(parser.getType());
                if (styles == null) {
                    return;
                }

                String[] styleSet = attributeValue.split(ProteusConstants.STYLE_DELIMITER);
                for (String styleName : styleSet) {
                    if (styles.contains(styleName)) {
                        process(styles.getStyle(styleName), parser, (ProteusView) view, (handler != null ? handler : ViewParser.this), viewManager.getProteusLayoutInflater());
                    }
                }
            }

            private void process(Map<String, JsonElement> style, LayoutParser parser, ProteusView proteusView, TypeParser handler, ProteusLayoutInflater builder) {
                for (Map.Entry<String, JsonElement> entry : style.entrySet()) {
                    builder.handleAttribute(handler, proteusView, entry.getKey(), parser.getValueParser(entry.getValue()));
                }
            }
        });

        addAttributeProcessor(Attributes.View.TransitionName, new StringAttributeProcessor<V>() {
            @Override
            public void handle(String attributeKey, String attributeValue, V view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setTransitionName(attributeValue);
                }
            }
        });

        addAttributeProcessor(Attributes.View.RequiresFadingEdge, new StringAttributeProcessor<V>() {

            private final String NONE = "none";
            private final String BOTH = "both";
            private final String VERTICAL = "vertical";
            private final String HORIZONTAL = "horizontal";

            @Override
            public void handle(String attributeKey, String attributeValue,
                               V view) {

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

        addAttributeProcessor(Attributes.View.FadingEdgeLength, new StringAttributeProcessor<V>() {
            @Override
            public void handle(String attributeKey, String attributeValue,
                               V view) {
                view.setFadingEdgeLength(ParseHelper.parseInt(attributeValue));
            }
        });

        final HashMap<String, Integer> relativeLayoutParams = new HashMap<>();
        relativeLayoutParams.put(Attributes.View.Above.getName(), RelativeLayout.ABOVE);
        relativeLayoutParams.put(Attributes.View.AlignBaseline.getName(), RelativeLayout.ALIGN_BASELINE);
        relativeLayoutParams.put(Attributes.View.AlignBottom.getName(), RelativeLayout.ALIGN_BOTTOM);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            relativeLayoutParams.put(Attributes.View.AlignEnd.getName(), RelativeLayout.ALIGN_END);
        }

        relativeLayoutParams.put(Attributes.View.AlignLeft.getName(), RelativeLayout.ALIGN_LEFT);
        relativeLayoutParams.put(Attributes.View.AlignParentBottom.getName(), RelativeLayout.ALIGN_PARENT_BOTTOM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            relativeLayoutParams.put(Attributes.View.AlignParentEnd.getName(), RelativeLayout.ALIGN_PARENT_END);
        }
        relativeLayoutParams.put(Attributes.View.AlignParentLeft.getName(), RelativeLayout.ALIGN_PARENT_LEFT);
        relativeLayoutParams.put(Attributes.View.AlignParentRight.getName(), RelativeLayout.ALIGN_PARENT_RIGHT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            relativeLayoutParams.put(Attributes.View.AlignParentStart.getName(), RelativeLayout.ALIGN_PARENT_START);
        }
        relativeLayoutParams.put(Attributes.View.AlignParentTop.getName(), RelativeLayout.ALIGN_PARENT_TOP);
        relativeLayoutParams.put(Attributes.View.AlignRight.getName(), RelativeLayout.ALIGN_RIGHT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            relativeLayoutParams.put(Attributes.View.AlignStart.getName(), RelativeLayout.ALIGN_START);
        }
        relativeLayoutParams.put(Attributes.View.AlignTop.getName(), RelativeLayout.ALIGN_TOP);

        relativeLayoutParams.put(Attributes.View.Below.getName(), RelativeLayout.BELOW);
        relativeLayoutParams.put(Attributes.View.CenterHorizontal.getName(), RelativeLayout.CENTER_HORIZONTAL);
        relativeLayoutParams.put(Attributes.View.CenterInParent.getName(), RelativeLayout.CENTER_IN_PARENT);
        relativeLayoutParams.put(Attributes.View.CenterVertical.getName(), RelativeLayout.CENTER_VERTICAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            relativeLayoutParams.put(Attributes.View.ToEndOf.getName(), RelativeLayout.END_OF);
        }
        relativeLayoutParams.put(Attributes.View.ToLeftOf.getName(), RelativeLayout.LEFT_OF);
        relativeLayoutParams.put(Attributes.View.ToRightOf.getName(), RelativeLayout.RIGHT_OF);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            relativeLayoutParams.put(Attributes.View.ToStartOf.getName(), RelativeLayout.START_OF);
        }

        StringAttributeProcessor<V> relativeLayoutProcessor = new StringAttributeProcessor<V>() {
            @Override
            public void handle(String attributeKey, String attributeValue, V view) {
                if (view instanceof ProteusView) {
                    int id = ((ProteusView) view).getViewManager().getUniqueViewId(attributeValue);
                    Integer rule = relativeLayoutParams.get(attributeKey);
                    if (rule != null) {
                        ParseHelper.addRelativeLayoutRule(view, rule, id);
                    }
                }
            }
        };

        StringAttributeProcessor<V> relativeLayoutBooleanProcessor = new StringAttributeProcessor<V>() {
            @Override
            public void handle(String attributeKey, String attributeValue, V view) {
                int trueOrFalse = ParseHelper.parseRelativeLayoutBoolean(attributeValue);
                Integer rule = relativeLayoutParams.get(attributeKey);
                if (rule != null) {
                    ParseHelper.addRelativeLayoutRule(view, rule, trueOrFalse);
                }
            }
        };

        addAttributeProcessor(Attributes.View.Above, relativeLayoutProcessor);
        addAttributeProcessor(Attributes.View.AlignBaseline, relativeLayoutProcessor);
        addAttributeProcessor(Attributes.View.AlignBottom, relativeLayoutProcessor);
        addAttributeProcessor(Attributes.View.AlignEnd, relativeLayoutProcessor);
        addAttributeProcessor(Attributes.View.AlignLeft, relativeLayoutProcessor);
        addAttributeProcessor(Attributes.View.AlignRight, relativeLayoutProcessor);
        addAttributeProcessor(Attributes.View.AlignStart, relativeLayoutProcessor);
        addAttributeProcessor(Attributes.View.AlignTop, relativeLayoutProcessor);
        addAttributeProcessor(Attributes.View.Below, relativeLayoutProcessor);
        addAttributeProcessor(Attributes.View.ToEndOf, relativeLayoutProcessor);
        addAttributeProcessor(Attributes.View.ToLeftOf, relativeLayoutProcessor);
        addAttributeProcessor(Attributes.View.ToRightOf, relativeLayoutProcessor);
        addAttributeProcessor(Attributes.View.ToStartOf, relativeLayoutProcessor);


        addAttributeProcessor(Attributes.View.AlignParentBottom, relativeLayoutBooleanProcessor);
        addAttributeProcessor(Attributes.View.AlignParentEnd, relativeLayoutBooleanProcessor);
        addAttributeProcessor(Attributes.View.AlignParentLeft, relativeLayoutBooleanProcessor);
        addAttributeProcessor(Attributes.View.AlignParentRight, relativeLayoutBooleanProcessor);
        addAttributeProcessor(Attributes.View.AlignParentStart, relativeLayoutBooleanProcessor);
        addAttributeProcessor(Attributes.View.AlignParentTop, relativeLayoutBooleanProcessor);
        addAttributeProcessor(Attributes.View.CenterHorizontal, relativeLayoutBooleanProcessor);
        addAttributeProcessor(Attributes.View.CenterInParent, relativeLayoutBooleanProcessor);
        addAttributeProcessor(Attributes.View.CenterVertical, relativeLayoutBooleanProcessor);


        addAttributeProcessor(Attributes.View.Animation, new TweenAnimationResourceProcessor<V>() {

            @Override
            public void setAnimation(V view, Animation animation) {
                view.setAnimation(animation);
            }
        });

        addAttributeProcessor(Attributes.View.TextAlignment, new StringAttributeProcessor<V>() {

            @Override
            public void handle(String attributeKey, String attributeValue, V view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    Integer textAlignment = ParseHelper.parseTextAlignment(attributeValue);
                    if (null != textAlignment) {
                        //noinspection ResourceType
                        view.setTextAlignment(textAlignment);
                    }
                }
            }
        });
    }

    @Override
    public boolean handleChildren(ProteusView view) {
        return false;
    }

    @Override
    public boolean addView(ProteusView parent, ProteusView view) {
        return false;
    }
}
