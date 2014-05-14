package com.flipkart.layoutengine.parser;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flipkart.layoutengine.library.R;
import com.flipkart.layoutengine.toolbox.AttributeBundle;
import com.google.gson.JsonObject;

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
         * This is hacky way to generate layout params. But no other way exists.
         * Ref : http://stackoverflow.com/questions/7018267/generating-a-layoutparams-based-on-the-type-of-parent
         */
        XmlResourceParser parser = parent.getResources().getLayout(R.layout.layout_params_hack);
        try {
            while (parser.nextToken() != XmlPullParser.START_TAG) {
                // Skip everything until the view tag.
            }
            return parent.generateLayoutParams(parser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

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
                layoutParams.height = Integer.parseInt(attributeValue);
                view.setLayoutParams(layoutParams);
            }
        });
        addHandler("width", new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeValue, T view) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = Integer.parseInt(attributeValue);
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

                if ("center".equals(attributeValue)) {
                    layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
                } else if ("center_horizontal".equals(attributeValue)) {
                    layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                } else if ("center_vertical".equals(attributeValue)) {
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;
                } else if ("left".equals(attributeValue)) {
                    layoutParams.gravity = Gravity.LEFT;
                } else if ("right".equals(attributeValue)) {
                    layoutParams.gravity = Gravity.RIGHT;
                }

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

                if ("center".equals(attributeValue)) {
                    viewGroup.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                } else if ("center_horizontal".equals(attributeValue)) {
                    viewGroup.setGravity(Gravity.CENTER_HORIZONTAL);
                } else if ("center_vertical".equals(attributeValue)) {
                    viewGroup.setGravity(Gravity.CENTER_VERTICAL);
                } else if ("left".equals(attributeValue)) {
                    viewGroup.setGravity(Gravity.LEFT);
                } else if ("right".equals(attributeValue)) {
                    viewGroup.setGravity(Gravity.RIGHT);
                }

            }
        });

        addHandler("padding", new AttributeProcessor<T>() {
            @Override
            public void handle(String attributeValue, T view) {
                String[] strings = attributeValue.split(" ");
                view.setPadding(Integer.parseInt(strings[0]),Integer.parseInt(strings[1]),Integer.parseInt(strings[2]),Integer.parseInt(strings[3]));
            }
        });
        
    }
}
