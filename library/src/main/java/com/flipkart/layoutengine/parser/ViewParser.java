package com.flipkart.layoutengine.parser;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.library.R;
import com.flipkart.layoutengine.toolbox.AttributeBundle;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nineoldandroids.view.ViewHelper;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class ViewParser<T extends View> extends Parser<T> {


    private static final String TAG = ViewParser.class.getSimpleName();

    public ViewParser(Class viewClass) {
        super(viewClass);
    }




    protected void prepareHandlers(Activity activity) {


        addHandler("backgroundColor", new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, View view) {
                view.setBackgroundColor(Color.parseColor(attributeValue));
            }
        });
        addHandler("height", new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                if(TextUtils.isDigitsOnly(attributeValue)) {

                    layoutParams.height = ParseHelper.parseDimension(attributeValue);
                }
                else
                {
                    if("fill".equals(attributeValue))
                    {
                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                    else if("wrap".equals(attributeValue))
                    {
                        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
                }
                view.setLayoutParams(layoutParams);
            }
        });
        addHandler("width", new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                if(TextUtils.isDigitsOnly(attributeValue)) {

                    layoutParams.width = ParseHelper.parseDimension(attributeValue);
                }
                else
                {
                    if("fill".equals(attributeValue))
                    {
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                    else if("wrap".equals(attributeValue))
                    {
                        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
                }
                view.setLayoutParams(layoutParams);
            }
        });

        addHandler("weight",new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                LinearLayout.LayoutParams layoutParams = null;
                try {
                    layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                } catch (ClassCastException ex) {
                    throw new IllegalArgumentException("weight is only supported for linear containers");
                }
                layoutParams.weight = Float.parseFloat(attributeValue);
                view.setLayoutParams(layoutParams);
            }
        });

        addHandler("alignToParent", new AttributeProcessor<T>() {
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

        addHandler("alignChildren", new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                LinearLayout viewGroup = null;
                try {
                    viewGroup = (LinearLayout) view;
                } catch (ClassCastException ex) {
                    throw new IllegalArgumentException("alignChildren is only supported for linear containers");
                }


                viewGroup.setGravity(ParseHelper.parseGravity(attributeValue));

            }
        });

        addHandler("padding", new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                String[] strings = attributeValue.split(" ");
                view.setPadding(ParseHelper.parseDimension(strings[0]),ParseHelper.parseDimension(strings[1]),ParseHelper.parseDimension(strings[2]),ParseHelper.parseDimension(strings[3]));
            }
        });

        addHandler("margin", new AttributeProcessor<T>() {
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

        addHandler("opacity",new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                ViewHelper.setAlpha(view,Float.parseFloat(attributeValue));
            }
        });

        addHandler("visibility",new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setVisibility(ParseHelper.parseVisibility(attributeValue));
            }
        });

        addHandler("id",new AttributeProcessor<T>() {
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

        addHandler("tag",new AttributeProcessor<T>() {
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

        addHandler("above",relativeLayoutProcessor);
        addHandler("alignBaseline",relativeLayoutProcessor);
        addHandler("alignBottom",relativeLayoutProcessor);
        addHandler("alignEnd",relativeLayoutProcessor);
        addHandler("alignLeft",relativeLayoutProcessor);
        addHandler("alignRight",relativeLayoutProcessor);
        addHandler("alignStart",relativeLayoutProcessor);
        addHandler("alignTop",relativeLayoutProcessor);
        addHandler("below",relativeLayoutProcessor);
        addHandler("toEndOf",relativeLayoutProcessor);
        addHandler("toLeftOf",relativeLayoutProcessor);
        addHandler("toRightOf",relativeLayoutProcessor);
        addHandler("toStartOf",relativeLayoutProcessor);




        addHandler("alignParentBottom",relativeLayoutBooleanProcessor);
        addHandler("alignParentEnd",relativeLayoutBooleanProcessor);
        addHandler("alignParentLeft",relativeLayoutBooleanProcessor);
        addHandler("alignParentRight",relativeLayoutBooleanProcessor);
        addHandler("alignParentStart",relativeLayoutBooleanProcessor);
        addHandler("alignParentTop",relativeLayoutBooleanProcessor);
        addHandler("centerHorizontal",relativeLayoutBooleanProcessor);
        addHandler("centerInParent",relativeLayoutBooleanProcessor);
        addHandler("centerVertical",relativeLayoutBooleanProcessor);






;
    }
}
