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

package com.flipkart.android.proteus.parser;

import android.annotation.SuppressLint;
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

import com.flipkart.android.proteus.AttributeProcessor;
import com.flipkart.android.proteus.Layout;
import com.flipkart.android.proteus.ProteusLayoutInflater;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.TypeParser;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.manager.ProteusViewManager;
import com.flipkart.android.proteus.processor.BooleanAttributeProcessor;
import com.flipkart.android.proteus.processor.DimensionAttributeProcessor;
import com.flipkart.android.proteus.processor.DrawableResourceProcessor;
import com.flipkart.android.proteus.processor.EventProcessor;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.processor.TweenAnimationResourceProcessor;
import com.flipkart.android.proteus.toolbox.Attributes;
import com.flipkart.android.proteus.toolbox.EventType;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.view.ProteusAndroidView;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * @author kiran.kumar
 */
public class ViewParser<V extends View> extends TypeParser<V> {

    private static final String TAG = "ViewParser";

    private static final String ID_STRING_START_PATTERN = "@+id/";
    private static final String ID_STRING_START_PATTERN1 = "@id/";
    private static final String ID_STRING_NORMALIZED_PATTERN = ":id/";

    @Override
    public ProteusView createView(ProteusLayoutInflater inflater, ViewGroup parent, Layout layout, JsonObject data, Styles styles, int index) {
        return new ProteusAndroidView(parent.getContext());
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor(Attributes.View.OnClick, new EventProcessor<V>() {
            @Override
            public void setOnEventListener(final V view, final Value value) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fireEvent((ProteusView) view, EventType.OnClick, value);
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
            public void setDimension(V view, float dimension) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.height = (int) dimension;
                    view.setLayoutParams(layoutParams);
                }
            }
        });
        addAttributeProcessor(Attributes.View.Width, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.width = (int) dimension;
                    view.setLayoutParams(layoutParams);
                }
            }
        });
        addAttributeProcessor(Attributes.View.Weight, new StringAttributeProcessor<V>() {
            @Override
            public void handle(V view, String value) {
                LinearLayout.LayoutParams layoutParams;
                if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                    layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                    layoutParams.weight = ParseHelper.parseFloat(value);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (ProteusConstants.isLoggingEnabled()) {
                        Log.e(TAG, "'weight' is only supported for LinearLayouts");
                    }
                }
            }
        });
        addAttributeProcessor(Attributes.View.LayoutGravity, new StringAttributeProcessor<V>() {
            @Override
            public void handle(V view, String value) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

                if (layoutParams instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams linearLayoutParams = (LinearLayout.LayoutParams) layoutParams;
                    //noinspection ResourceType
                    linearLayoutParams.gravity = ParseHelper.parseGravity(value);
                    view.setLayoutParams(layoutParams);
                } else if (layoutParams instanceof FrameLayout.LayoutParams) {
                    FrameLayout.LayoutParams linearLayoutParams = (FrameLayout.LayoutParams) layoutParams;
                    linearLayoutParams.gravity = ParseHelper.parseGravity(value);
                    view.setLayoutParams(layoutParams);
                } else {
                    if (ProteusConstants.isLoggingEnabled()) {
                        Log.e(TAG, "'layout_gravity' is only supported for LinearLayout and FrameLayout");
                    }
                }
            }
        });
        addAttributeProcessor(Attributes.View.Padding, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setPadding((int) dimension, (int) dimension, (int) dimension, (int) dimension);
            }
        });
        addAttributeProcessor(Attributes.View.PaddingLeft, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setPadding((int) dimension, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            }
        });
        addAttributeProcessor(Attributes.View.PaddingTop, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setPadding(view.getPaddingLeft(), (int) dimension, view.getPaddingRight(), view.getPaddingBottom());
            }
        });
        addAttributeProcessor(Attributes.View.PaddingRight, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), (int) dimension, view.getPaddingBottom());
            }
        });
        addAttributeProcessor(Attributes.View.PaddingBottom, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), (int) dimension);
            }
        });
        addAttributeProcessor(Attributes.View.Margin, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
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
            public void setDimension(V view, float dimension) {
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
            public void setDimension(V view, float dimension) {
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
            public void setDimension(V view, float dimension) {
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
            public void setDimension(V view, float dimension) {
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
            public void setDimension(V view, float dimension) {
                view.setMinimumHeight((int) dimension);
            }
        });

        addAttributeProcessor(Attributes.View.MinWidth, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setMinimumWidth((int) dimension);
            }
        });

        addAttributeProcessor(Attributes.View.Elevation, new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setElevation(dimension);
                }
            }
        });
        addAttributeProcessor(Attributes.View.Alpha, new StringAttributeProcessor<V>() {
            @Override
            public void handle(V view, String value) {
                view.setAlpha(ParseHelper.parseFloat(value));
            }
        });
        addAttributeProcessor(Attributes.View.Visibility, new AttributeProcessor<V>() {
            @Override
            public void handle(V view, Value value) {
                // noinspection ResourceType
                view.setVisibility(ParseHelper.parseVisibility(value));
            }
        });
        addAttributeProcessor(Attributes.View.Invisibility, new AttributeProcessor<V>() {
            @Override
            public void handle(V view, Value value) {
                // noinspection ResourceType
                view.setVisibility(ParseHelper.parseInvisibility(value));
            }
        });
        addAttributeProcessor(Attributes.View.Id, new StringAttributeProcessor<V>() {
            @Override
            public void handle(final V view, String value) {
                if (view instanceof ProteusView) {
                    view.setId(((ProteusView) view).getViewManager().getUniqueViewId(value));
                }

                // set view id resource name
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    final String resourceName = value;
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
            public void handle(V view, String value) {
                view.setContentDescription(value);
            }
        });
        addAttributeProcessor(Attributes.View.Clickable, new BooleanAttributeProcessor<V>() {
            @Override
            public void handle(V view, boolean value) {
                view.setClickable(value);
            }
        });
        addAttributeProcessor(Attributes.View.Tag, new StringAttributeProcessor<V>() {
            @Override
            public void handle(V view, String value) {
                view.setTag(value);
            }
        });
        addAttributeProcessor(Attributes.View.Enabled, new BooleanAttributeProcessor<V>() {
            @Override
            public void handle(V view, boolean value) {
                view.setEnabled(value);
            }
        });

        addAttributeProcessor(Attributes.View.Style, new StringAttributeProcessor<V>() {
            @Override
            public void handle(V view, String value) {
                ProteusViewManager viewManager = ((ProteusView) view).getViewManager();
                Layout layout = viewManager.getLayout();
                Styles styles = viewManager.getStyles();

                TypeParser handler = viewManager.getProteusLayoutInflater().getParser(layout.type);
                if (styles == null) {
                    return;
                }

                String[] styleSet = value.split(ProteusConstants.STYLE_DELIMITER);
                for (String styleName : styleSet) {
                    if (styles.contains(styleName)) {
                        process(styles.getStyle(styleName), (ProteusView) view, (handler != null ? handler : ViewParser.this), viewManager.getProteusLayoutInflater(), layout.type);
                    }
                }
            }

            private void process(Map<String, Value> style, ProteusView proteusView, TypeParser handler, ProteusLayoutInflater inflater, String type) {
                for (Map.Entry<String, Value> entry : style.entrySet()) {
                    inflater.handleAttribute(handler, proteusView, handler.getAttributeId(entry.getKey()), entry.getValue());
                }
            }
        });

        addAttributeProcessor(Attributes.View.TransitionName, new StringAttributeProcessor<V>() {
            @Override
            public void handle(V view, String value) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setTransitionName(value);
                }
            }
        });

        addAttributeProcessor(Attributes.View.RequiresFadingEdge, new StringAttributeProcessor<V>() {

            private final String NONE = "none";
            private final String BOTH = "both";
            private final String VERTICAL = "vertical";
            private final String HORIZONTAL = "horizontal";

            @Override
            public void handle(V view, String value) {

                switch (value) {
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
            public void handle(V view, String value) {
                view.setFadingEdgeLength(ParseHelper.parseInt(value));
            }
        });

        addAttributeProcessor(Attributes.View.Animation, new TweenAnimationResourceProcessor<V>() {

            @Override
            public void setAnimation(V view, Animation animation) {
                view.setAnimation(animation);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            addAttributeProcessor(Attributes.View.TextAlignment, new StringAttributeProcessor<V>() {

                @SuppressLint("NewApi")
                @Override
                public void handle(V view, String value) {

                    Integer textAlignment = ParseHelper.parseTextAlignment(value);
                    if (null != textAlignment) {
                        //noinspection ResourceType
                        view.setTextAlignment(textAlignment);
                    }
                }

            });
        }

        addAttributeProcessor(Attributes.View.Above, createRelativeLayoutRuleProcessor(RelativeLayout.ABOVE));
        addAttributeProcessor(Attributes.View.AlignBaseline, createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_BASELINE));
        addAttributeProcessor(Attributes.View.AlignBottom, createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_BOTTOM));
        addAttributeProcessor(Attributes.View.AlignLeft, createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_LEFT));
        addAttributeProcessor(Attributes.View.AlignRight, createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_RIGHT));
        addAttributeProcessor(Attributes.View.AlignTop, createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_TOP));
        addAttributeProcessor(Attributes.View.Below, createRelativeLayoutRuleProcessor(RelativeLayout.BELOW));
        addAttributeProcessor(Attributes.View.ToLeftOf, createRelativeLayoutRuleProcessor(RelativeLayout.LEFT_OF));
        addAttributeProcessor(Attributes.View.ToRightOf, createRelativeLayoutRuleProcessor(RelativeLayout.RIGHT_OF));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            addAttributeProcessor(Attributes.View.AlignEnd, createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_END));
            addAttributeProcessor(Attributes.View.AlignStart, createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_START));
            addAttributeProcessor(Attributes.View.ToEndOf, createRelativeLayoutRuleProcessor(RelativeLayout.END_OF));
            addAttributeProcessor(Attributes.View.ToStartOf, createRelativeLayoutRuleProcessor(RelativeLayout.START_OF));
        }

        addAttributeProcessor(Attributes.View.AlignParentTop, createRelativeLayoutBooleanRuleProcessor(RelativeLayout.ALIGN_PARENT_TOP));
        addAttributeProcessor(Attributes.View.AlignParentRight, createRelativeLayoutBooleanRuleProcessor(RelativeLayout.ALIGN_PARENT_RIGHT));
        addAttributeProcessor(Attributes.View.AlignParentBottom, createRelativeLayoutBooleanRuleProcessor(RelativeLayout.ALIGN_PARENT_BOTTOM));
        addAttributeProcessor(Attributes.View.AlignParentLeft, createRelativeLayoutBooleanRuleProcessor(RelativeLayout.ALIGN_PARENT_LEFT));
        addAttributeProcessor(Attributes.View.CenterHorizontal, createRelativeLayoutBooleanRuleProcessor(RelativeLayout.CENTER_HORIZONTAL));
        addAttributeProcessor(Attributes.View.CenterVertical, createRelativeLayoutBooleanRuleProcessor(RelativeLayout.CENTER_VERTICAL));
        addAttributeProcessor(Attributes.View.CenterInParent, createRelativeLayoutBooleanRuleProcessor(RelativeLayout.CENTER_IN_PARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            addAttributeProcessor(Attributes.View.AlignParentStart, createRelativeLayoutBooleanRuleProcessor(RelativeLayout.ALIGN_PARENT_START));
            addAttributeProcessor(Attributes.View.AlignParentEnd, createRelativeLayoutBooleanRuleProcessor(RelativeLayout.ALIGN_PARENT_END));
        }
    }

    @Override
    public boolean handleChildren(ProteusView view, Value children) {
        return false;
    }

    @Override
    public boolean addView(ProteusView parent, ProteusView view) {
        return false;
    }

    private AttributeProcessor<V> createRelativeLayoutRuleProcessor(final int rule) {
        return new StringAttributeProcessor<V>() {
            @Override
            public void handle(V view, String value) {
                if (view instanceof ProteusView) {
                    int id = ((ProteusView) view).getViewManager().getUniqueViewId(value);
                    ParseHelper.addRelativeLayoutRule(view, rule, id);
                }
            }
        };
    }

    private AttributeProcessor<V> createRelativeLayoutBooleanRuleProcessor(final int rule) {
        return new BooleanAttributeProcessor<V>() {
            @Override
            public void handle(V view, boolean value) {
                int trueOrFalse = ParseHelper.parseRelativeLayoutBoolean(value);
                ParseHelper.addRelativeLayoutRule(view, rule, trueOrFalse);
            }
        };
    }
}
