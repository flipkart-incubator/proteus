package com.flipkart.layoutengine.parser;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flipkart.layoutengine.library.R;
import com.flipkart.layoutengine.toolbox.AttributeBundle;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nineoldandroids.view.ViewHelper;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class ViewParser<T extends View> extends Parser<T> {


    public ViewParser(Class viewClass) {
        super(viewClass);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams(ViewGroup parent, JsonObject object) {

        /**
         * This whole method is a hack! ... to generate layout params, since no other way exists.
         * Refer : http://stackoverflow.com/questions/7018267/generating-a-layoutparams-based-on-the-type-of-parent
         */
        XmlResourceParser parser = parent.getResources().getLayout(R.layout.layout_params_hack);
        try {
            while (parser.nextToken() != XmlPullParser.START_TAG) {
                // Skip everything until the view tag.
            }
            ViewGroup.LayoutParams layoutParams = parent.generateLayoutParams(parser);
            return layoutParams;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public void setupView(ViewGroup parent, T view) {
        // nothing to do here
    }

    protected void prepareHandlers(Activity activity) {

        addHandler("backgroundColor", new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeValue, View view) {
                view.setBackgroundColor(Color.parseColor(attributeValue));
            }
        });
        addHandler("height", new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeValue, T view) {
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
            public void handle(String attributeValue, T view) {
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
            public void handle(String attributeValue, T view) {
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
            public void handle(String attributeValue, T view) {
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
            public void handle(String attributeValue, T view) {
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
            public void handle(String attributeValue, T view) {
                String[] strings = attributeValue.split(" ");
                view.setPadding(ParseHelper.parseDimension(strings[0]),ParseHelper.parseDimension(strings[1]),ParseHelper.parseDimension(strings[2]),ParseHelper.parseDimension(strings[3]));
            }
        });

        addHandler("margin", new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeValue, T view) {
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
            public void handle(String attributeValue, T view) {
                ViewHelper.setAlpha(view,Float.parseFloat(attributeValue));
            }
        });

        addHandler("visibility",new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeValue, T view) {
                view.setVisibility(ParseHelper.parseVisibility(attributeValue));
            }
        });



    }

    @Override
    public JsonArray parseChildren(JsonElement element) {
        return element.getAsJsonArray();
    }
}
