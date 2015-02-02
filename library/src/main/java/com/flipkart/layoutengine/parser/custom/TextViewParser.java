package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

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
public class TextViewParser<T extends TextView> extends WrappableParser<T> {

    public TextViewParser(Parser<T> wrappedParser) {
        super(TextView.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(Context context) {
        super.prepareHandlers(context);
        addHandler(Attributes.TextView.Text, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setText(attributeValue);
            }
        });

        addHandler(Attributes.TextView.DrawablePadding,new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setCompoundDrawablePadding(ParseHelper.parseDimension(attributeValue));
            }
        });

        addHandler(Attributes.TextView.TextSize,new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setTextSize(ParseHelper.parseDimension(attributeValue));
            }
        });
        addHandler(Attributes.TextView.Gravity,new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setGravity(ParseHelper.parseGravity(attributeValue));
            }
        });

        addHandler(Attributes.TextView.TextColor,new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setTextColor(ParseHelper.parseColor(attributeValue));
            }
        });

        addHandler(Attributes.TextView.DrawableLeft,new ResourceReferenceProcessor<T>(context) {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                Drawable[] compoundDrawables = view.getCompoundDrawables();
                view.setCompoundDrawablesWithIntrinsicBounds(drawable,compoundDrawables[1],compoundDrawables[2],compoundDrawables[3]);
            }
        });
        addHandler(Attributes.TextView.DrawableTop,new ResourceReferenceProcessor<T>(context) {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                Drawable[] compoundDrawables = view.getCompoundDrawables();
                view.setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0],drawable,compoundDrawables[2],compoundDrawables[3]);
            }
        });
        addHandler(Attributes.TextView.DrawableRight,new ResourceReferenceProcessor<T>(context) {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                Drawable[] compoundDrawables = view.getCompoundDrawables();
                view.setCompoundDrawablesWithIntrinsicBounds(drawable,compoundDrawables[1],drawable,compoundDrawables[3]);
            }
        });
        addHandler(Attributes.TextView.DrawableBottom,new ResourceReferenceProcessor<T>(context) {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                Drawable[] compoundDrawables = view.getCompoundDrawables();
                view.setCompoundDrawablesWithIntrinsicBounds(drawable,compoundDrawables[1],compoundDrawables[2],drawable);
            }
        });

        addHandler(Attributes.TextView.MaxLines,new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setMaxLines(Integer.valueOf(attributeValue));
            }
        });

        addHandler(Attributes.TextView.Ellipsize,new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                Enum ellipsize = ParseHelper.parseEllipsize(attributeValue);
                view.setEllipsize((android.text.TextUtils.TruncateAt) ellipsize);
               }
        });

        addHandler(Attributes.TextView.PaintFlags,new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                if(attributeValue.equals("strike"))
                    view.setPaintFlags(view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        });

        addHandler(Attributes.TextView.Prefix,new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                    view.setText(attributeValue + view.getText());
            }
        });

        addHandler(Attributes.TextView.Suffix,new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setText(view.getText() + attributeValue);
            }
        });

        addHandler(Attributes.TextView.TextStyle,new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                int typeface = ParseHelper.parseTypeFace(attributeValue);
                view.setTypeface(Typeface.defaultFromStyle(typeface));
            }
        });

        addHandler(Attributes.TextView.SingleLine,new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setSingleLine(ParseHelper.parseBoolean(attributeValue));
            }
        });
    }
}
