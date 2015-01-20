package com.flipkart.layoutengine.parser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.processor.ResourceReferenceProcessor;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;
import com.flipkart.layoutengine.toolbox.IdGenerator;
import com.nineoldandroids.view.ViewHelper;

import java.util.HashMap;

/**
 * Created by kiran.kumar on 12/05/14.
 *
 * @attr Tests
 */
public class ViewParser<T extends View> extends Parser<T> {


    private static final String TAG = ViewParser.class.getSimpleName();

    public ViewParser(Class viewClass) {
        super(viewClass);
    }


    protected void prepareHandlers(final Context context) {


        addHandler(Attributes.View.Background, new ResourceReferenceProcessor<T>(context) {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackgroundDrawable(drawable);
                } else {
                    view.setBackground(drawable);
                }
            }
        });

        addHandler(Attributes.View.Height, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = ParseHelper.parseDimension(attributeValue);
                view.setLayoutParams(layoutParams);
            }
        });
        addHandler(Attributes.View.Width, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = ParseHelper.parseDimension(attributeValue);
                view.setLayoutParams(layoutParams);
            }
        });

        addHandler(Attributes.View.Weight, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                LinearLayout.LayoutParams layoutParams = null;
                try {
                    layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                    layoutParams.weight = Float.parseFloat(attributeValue);
                    view.setLayoutParams(layoutParams);

                } catch (ClassCastException ex) {
                    throw new IllegalArgumentException(attributeKey + " is only supported for linear containers");
                }

            }
        });

        addHandler(Attributes.View.LayoutGravity, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
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
                    throw new IllegalArgumentException(attributeKey + " is only supported for linearlayout and framelayout containers");
                }


            }
        });



        addHandler(Attributes.View.Padding, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                int dimension = ParseHelper.parseDimension(attributeValue);
                view.setPadding(dimension, dimension, dimension, dimension);
            }
        });
        addHandler(Attributes.View.PaddingLeft, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                int dimension = ParseHelper.parseDimension(attributeValue);
                view.setPadding(dimension, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            }
        });
        addHandler(Attributes.View.PaddingTop, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                int dimension = ParseHelper.parseDimension(attributeValue);
                view.setPadding(view.getPaddingLeft(), dimension, view.getPaddingRight(), view.getPaddingBottom());
            }
        });
        addHandler(Attributes.View.PaddingRight, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                int dimension = ParseHelper.parseDimension(attributeValue);
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), dimension, view.getPaddingBottom());
            }
        });
        addHandler(Attributes.View.PaddingBottom, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                int dimension = ParseHelper.parseDimension(attributeValue);
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), dimension);
            }
        });

        addHandler(Attributes.View.Margin, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                int dimension = ParseHelper.parseDimension(attributeValue);
                ViewGroup.MarginLayoutParams layoutParams;
                try {
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                } catch (ClassCastException ex) {
                    throw new IllegalArgumentException("margins can only be applied to views with parent viewgroups");
                }
                layoutParams.setMargins(dimension, dimension, dimension, dimension);
                view.setLayoutParams(layoutParams);
            }
        });
        addHandler(Attributes.View.MarginLeft, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                int dimension = ParseHelper.parseDimension(attributeValue);
                ViewGroup.MarginLayoutParams layoutParams;
                try {
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                } catch (ClassCastException ex) {
                    throw new IllegalArgumentException("margins can only be applied to views with parent viewgroups");
                }
                layoutParams.setMargins(dimension, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
                view.setLayoutParams(layoutParams);
            }
        });
        addHandler(Attributes.View.MarginTop, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                int dimension = ParseHelper.parseDimension(attributeValue);
                ViewGroup.MarginLayoutParams layoutParams;
                try {
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                } catch (ClassCastException ex) {
                    throw new IllegalArgumentException("margins can only be applied to views with parent viewgroups");
                }
                layoutParams.setMargins(layoutParams.leftMargin, dimension, layoutParams.rightMargin, layoutParams.bottomMargin);
                view.setLayoutParams(layoutParams);
            }
        });
        addHandler(Attributes.View.MarginRight, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                int dimension = ParseHelper.parseDimension(attributeValue);
                ViewGroup.MarginLayoutParams layoutParams;
                try {
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                } catch (ClassCastException ex) {
                    throw new IllegalArgumentException("margins can only be applied to views with parent viewgroups");
                }
                layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, dimension, layoutParams.bottomMargin);
                view.setLayoutParams(layoutParams);
            }
        });
        addHandler(Attributes.View.MarginBottom, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                int dimension = ParseHelper.parseDimension(attributeValue);
                ViewGroup.MarginLayoutParams layoutParams;
                try {
                    layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                } catch (ClassCastException ex) {
                    throw new IllegalArgumentException("margins can only be applied to views with parent viewgroups");
                }
                layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, dimension);
                view.setLayoutParams(layoutParams);
            }
        });

        addHandler(Attributes.View.Alpha, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                ViewHelper.setAlpha(view, Float.parseFloat(attributeValue));
            }
        });

        addHandler(Attributes.View.Visibility, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                //noinspection ResourceType
                view.setVisibility(ParseHelper.parseVisibility(attributeValue));
            }
        });

        addHandler(Attributes.View.Id, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {

                    view.setId(IdGenerator.getInstance().getUnique(attributeValue));

            }
        });

        addHandler(Attributes.View.ContentDescription, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setContentDescription(attributeValue);
            }
        });

        addHandler(Attributes.View.Clickable, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                boolean clickable = ParseHelper.parseBoolean(attributeValue);
                view.setClickable(clickable);
            }
        });

        addHandler(Attributes.View.Tag, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setTag(attributeValue);
            }
        });

        final HashMap<String, Integer> relativeLayoutParams = new HashMap<String, Integer>();
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
        //relativeLayoutParams.put("alignWithParentIfMissing",RelativeLayout.ALIGN_PARENT_IF_MISSING); // not supported as rule
        relativeLayoutParams.put(Attributes.View.Below.getName(), RelativeLayout.BELOW);
        relativeLayoutParams.put(Attributes.View.CenterHorizontal.getName(), RelativeLayout.CENTER_HORIZONTAL);
        relativeLayoutParams.put(Attributes.View.CenterInParent.getName(), RelativeLayout.CENTER_IN_PARENT);
        relativeLayoutParams.put(Attributes.View.CenterVertical.getName(), RelativeLayout.CENTER_VERTICAL);
        relativeLayoutParams.put(Attributes.View.ToEndOf.getName(), RelativeLayout.END_OF);
        relativeLayoutParams.put(Attributes.View.ToLeftOf.getName(), RelativeLayout.LEFT_OF);
        relativeLayoutParams.put(Attributes.View.ToRightOf.getName(), RelativeLayout.RIGHT_OF);
        relativeLayoutParams.put(Attributes.View.ToStartOf.getName(), RelativeLayout.START_OF);


        StringAttributeProcessor<T> relativeLayoutProcessor = new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                int id = IdGenerator.getInstance().getUnique(attributeValue);
                Integer rule = relativeLayoutParams.get(attributeKey);
                ParseHelper.addRelativeLayoutRule(view, rule, id);
            }
        };

        StringAttributeProcessor<T> relativeLayoutBooleanProcessor = new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
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
