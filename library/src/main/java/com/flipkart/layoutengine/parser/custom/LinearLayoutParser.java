package com.flipkart.layoutengine.parser.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.ResourceReferenceProcessor;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class LinearLayoutParser<T extends LinearLayout> extends WrappableParser<T> {
    public LinearLayoutParser(Parser<T> wrappedParser) {
        super(LinearLayout.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(Context context) {
        super.prepareHandlers(context);
        addHandler(Attributes.LinearLayout.Orientation,new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                if("horizontal".equals(attributeValue)) {
                    view.setOrientation(LinearLayout.HORIZONTAL);
                }
                else
                {
                    view.setOrientation(LinearLayout.VERTICAL);
                }
            }
        });

        addHandler(Attributes.View.Gravity, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {

                view.setGravity(ParseHelper.parseGravity(attributeValue));

            }
        });

        addHandler(Attributes.LinearLayout.Divider , new ResourceReferenceProcessor<T>(context) {
            @SuppressLint("NewApi")
            @Override
            public void setDrawable(T view, Drawable drawable) {
                view.setDividerDrawable(drawable);
            }
        });

        addHandler(Attributes.LinearLayout.DividerPadding,new StringAttributeProcessor<T>() {
            @SuppressLint("NewApi")
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setDividerPadding(Integer.parseInt(attributeValue));
            }
        });

        addHandler(Attributes.LinearLayout.ShowDividers,new StringAttributeProcessor<T>() 	{
            @SuppressLint("NewApi")
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                if(attributeValue.equals("end")){
                    view.setShowDividers(LinearLayout.SHOW_DIVIDER_END);
                }else if(attributeValue.equals("middle")){
                    view.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                }else if(attributeValue.equals("beginning")){
                    view.setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING);
                }else{
                    view.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
                }
            }
        });
    }
}
