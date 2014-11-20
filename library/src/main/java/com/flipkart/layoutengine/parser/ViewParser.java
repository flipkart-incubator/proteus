package com.flipkart.layoutengine.parser;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flipkart.layoutengine.processor.AttributeProcessor;
import com.nineoldandroids.view.ViewHelper;

import java.util.HashMap;

/**
 * Created by kiran.kumar on 12/05/14.
 * @attr Tests
 */
public class ViewParser<T extends View> extends Parser<T> {


    private static final String TAG = ViewParser.class.getSimpleName();

    public ViewParser(Class viewClass) {
        super(viewClass);
    }




    protected void prepareHandlers(Context context) {


        addHandler(Attributes.View.Background, new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, View view) {
                 view.setBackgroundColor(ParseHelper.parseColor(attributeValue));

            }
        });
        addHandler(Attributes.View.Height, new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                if(TextUtils.isDigitsOnly(attributeValue)) {

                    layoutParams.height = ParseHelper.parseDimension(attributeValue);
                }
                else
                {
                    if("match_parent".equals(attributeValue) || "fill_parent".equals(attributeValue))
                    {
                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                    else if("wrap_content".equals(attributeValue))
                    {
                        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
                }
                view.setLayoutParams(layoutParams);
            }
        });
        addHandler(Attributes.View.Width, new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                if(TextUtils.isDigitsOnly(attributeValue)) {

                    layoutParams.width = ParseHelper.parseDimension(attributeValue);
                }
                else
                {
                    if("match_parent".equals(attributeValue) || "fill_parent".equals(attributeValue))
                    {
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                    else if("wrap_content".equals(attributeValue))
                    {
                        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
                }
                view.setLayoutParams(layoutParams);
            }
        });

        addHandler(Attributes.View.Weight,new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                LinearLayout.LayoutParams layoutParams = null;
                try {
                    layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                } catch (ClassCastException ex) {
                    throw new IllegalArgumentException(attributeKey+" is only supported for linear containers");
                }
                layoutParams.weight = Float.parseFloat(attributeValue);
                view.setLayoutParams(layoutParams);
            }
        });

        addHandler(Attributes.View.LayoutGravity, new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                LinearLayout.LayoutParams layoutParams = null;
                try {
                    layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                } catch (ClassCastException ex) {
                    throw new IllegalArgumentException("alignToParent is only supported for linear containers");
                }
                layoutParams.gravity = ParseHelper.parseGravity(attributeValue);

            }
        });

        addHandler(Attributes.View.Gravity, new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                if(view instanceof ViewGroup) {
                    LinearLayout viewGroup = null;
                    try {
                        viewGroup = (LinearLayout) view;
                    } catch (ClassCastException ex) {
                        throw new IllegalArgumentException(attributeKey + " is only supported for linear containers");
                    }


                    viewGroup.setGravity(ParseHelper.parseGravity(attributeValue));
                }

            }
        });

        addHandler(Attributes.View.Padding, new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                int dimension = ParseHelper.parseDimension(attributeValue);
                view.setPadding(dimension,dimension,dimension,dimension);
            }
        });
        addHandler(Attributes.View.PaddingLeft, new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                int dimension = ParseHelper.parseDimension(attributeValue);
                view.setPadding(dimension,view.getPaddingTop(),view.getPaddingRight(),view.getPaddingBottom());
            }
        });
        addHandler(Attributes.View.PaddingTop, new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                int dimension = ParseHelper.parseDimension(attributeValue);
                view.setPadding(view.getPaddingLeft(),dimension,view.getPaddingRight(),view.getPaddingBottom());
            }
        });
        addHandler(Attributes.View.PaddingRight, new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                int dimension = ParseHelper.parseDimension(attributeValue);
                view.setPadding(view.getPaddingLeft(),view.getPaddingTop(),dimension,view.getPaddingBottom());
            }
        });
        addHandler(Attributes.View.PaddingBottom, new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                int dimension = ParseHelper.parseDimension(attributeValue);
                view.setPadding(view.getPaddingLeft(),view.getPaddingTop(),view.getPaddingRight(),dimension);
            }
        });

        addHandler(Attributes.View.Margin, new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                String[] strings = attributeValue.split(" ");
                ViewGroup.MarginLayoutParams layoutParams;
                try {
                    layoutParams = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
                }
                catch (ClassCastException ex)
                {
                    throw new IllegalArgumentException("margins can only be applied for some views");
                }


                layoutParams.setMargins(ParseHelper.parseDimension(strings[0]), ParseHelper.parseDimension(strings[1]), ParseHelper.parseDimension(strings[2]), ParseHelper.parseDimension(strings[3]));
                view.setLayoutParams(layoutParams);
            }
        });

        addHandler(Attributes.View.Alpha,new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                ViewHelper.setAlpha(view,Float.parseFloat(attributeValue));
            }
        });

        addHandler(Attributes.View.Visibility,new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                //noinspection ResourceType
                view.setVisibility(ParseHelper.parseVisibility(attributeValue));
            }
        });

        addHandler(Attributes.View.Id,new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                if(TextUtils.isDigitsOnly(attributeValue))
                {
                    view.setId(Integer.valueOf(attributeValue));
                }
                else {
                    Log.d(TAG,"id attribute should be an integer");
                }
            }
        });

        addHandler(Attributes.View.Tag,new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setTag(attributeValue);
            }
        });

        final HashMap<String,Integer> relativeLayoutParams = new HashMap<String, Integer>();
        relativeLayoutParams.put("above",RelativeLayout.ABOVE);
        relativeLayoutParams.put("alignBaseline",RelativeLayout.ALIGN_BASELINE);
        relativeLayoutParams.put("alignBottom",RelativeLayout.ALIGN_BOTTOM);
        relativeLayoutParams.put("alignEnd",RelativeLayout.ALIGN_END);
        relativeLayoutParams.put("alignLeft",RelativeLayout.ALIGN_LEFT);
        relativeLayoutParams.put("alignParentBottom",RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeLayoutParams.put("alignParentEnd",RelativeLayout.ALIGN_PARENT_END);
        relativeLayoutParams.put("alignParentLeft",RelativeLayout.ALIGN_PARENT_LEFT);
        relativeLayoutParams.put("alignParentRight",RelativeLayout.ALIGN_PARENT_RIGHT);
        relativeLayoutParams.put("alignParentStart",RelativeLayout.ALIGN_PARENT_START);
        relativeLayoutParams.put("alignParentTop",RelativeLayout.ALIGN_PARENT_TOP);
        relativeLayoutParams.put("alignRight",RelativeLayout.ALIGN_RIGHT);
        relativeLayoutParams.put("alignStart",RelativeLayout.ALIGN_START);
        relativeLayoutParams.put("alignTop",RelativeLayout.ALIGN_TOP);
        //relativeLayoutParams.put("alignWithParentIfMissing",RelativeLayout.ALIGN_PARENT_IF_MISSING); // not supported as rule
        relativeLayoutParams.put("below",RelativeLayout.BELOW);
        relativeLayoutParams.put("centerHorizontal",RelativeLayout.CENTER_HORIZONTAL);
        relativeLayoutParams.put("centerInParent",RelativeLayout.CENTER_IN_PARENT);
        relativeLayoutParams.put("centerVertical",RelativeLayout.CENTER_VERTICAL);
        relativeLayoutParams.put("toEndOf",RelativeLayout.END_OF);
        relativeLayoutParams.put("toLeftOf",RelativeLayout.LEFT_OF);
        relativeLayoutParams.put("toRightOf",RelativeLayout.RIGHT_OF);
        relativeLayoutParams.put("toStartOf",RelativeLayout.START_OF);


        AttributeProcessor<T> relativeLayoutProcessor = new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                Integer id = ParseHelper.parseId(attributeValue);
                ParseHelper.addRelativeLayoutRule(view, relativeLayoutParams.get(attributeKey),id);
            }
        };

        AttributeProcessor<T> relativeLayoutBooleanProcessor = new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                int trueOrFalse = ParseHelper.parseRelativeLayoutBoolean(attributeValue);
                ParseHelper.addRelativeLayoutRule(view, relativeLayoutParams.get(attributeKey),trueOrFalse);
            }
        };

        addHandler(Attributes.View.Above,relativeLayoutProcessor);
        addHandler(Attributes.View.AlignBaseline,relativeLayoutProcessor);
        addHandler(Attributes.View.AlignBottom,relativeLayoutProcessor);
        addHandler(Attributes.View.AlignEnd,relativeLayoutProcessor);
        addHandler(Attributes.View.AlignLeft,relativeLayoutProcessor);
        addHandler(Attributes.View.AlignRight,relativeLayoutProcessor);
        addHandler(Attributes.View.AlignStart,relativeLayoutProcessor);
        addHandler(Attributes.View.AlignTop,relativeLayoutProcessor);
        addHandler(Attributes.View.Below,relativeLayoutProcessor);
        addHandler(Attributes.View.ToEndOf,relativeLayoutProcessor);
        addHandler(Attributes.View.ToLeftOf,relativeLayoutProcessor);
        addHandler(Attributes.View.ToRightOf,relativeLayoutProcessor);
        addHandler(Attributes.View.ToStartOf,relativeLayoutProcessor);




        addHandler(Attributes.View.AlignParentBottom,relativeLayoutBooleanProcessor);
        addHandler(Attributes.View.AlignParentEnd,relativeLayoutBooleanProcessor);
        addHandler(Attributes.View.AlignParentLeft,relativeLayoutBooleanProcessor);
        addHandler(Attributes.View.AlignParentRight,relativeLayoutBooleanProcessor);
        addHandler(Attributes.View.AlignParentStart,relativeLayoutBooleanProcessor);
        addHandler(Attributes.View.AlignParentTop,relativeLayoutBooleanProcessor);
        addHandler(Attributes.View.CenterHorizontal,relativeLayoutBooleanProcessor);
        addHandler(Attributes.View.CenterInParent,relativeLayoutBooleanProcessor);
        addHandler(Attributes.View.CenterVertical,relativeLayoutBooleanProcessor);






;
    }
}
