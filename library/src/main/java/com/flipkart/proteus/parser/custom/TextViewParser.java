package com.flipkart.proteus.parser.custom;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.util.TypedValue;

import com.flipkart.proteus.parser.Attributes;
import com.flipkart.proteus.parser.ParseHelper;
import com.flipkart.proteus.parser.Parser;
import com.flipkart.proteus.parser.WrappableParser;
import com.flipkart.proteus.processor.ColorResourceProcessor;
import com.flipkart.proteus.processor.DimensionAttributeProcessor;
import com.flipkart.proteus.processor.DrawableResourceProcessor;
import com.flipkart.proteus.processor.StringAttributeProcessor;
import com.flipkart.proteus.view.TextView;
import com.google.gson.JsonElement;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class TextViewParser<T extends TextView> extends WrappableParser<T> {

    public TextViewParser(Parser<T> wrappedParser) {
        super(TextView.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers() {
        super.prepareHandlers();
        addHandler(Attributes.TextView.HTML, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setText(Html.fromHtml(attributeValue));
            }
        });
        addHandler(Attributes.TextView.Text, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setText(attributeValue);
            }
        });

        addHandler(Attributes.TextView.DrawablePadding, new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(float dimension, T view, String key, JsonElement value) {
                view.setCompoundDrawablePadding((int) dimension);
            }
        });

        addHandler(Attributes.TextView.TextSize, new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(float dimension, T view, String key, JsonElement value) {
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimension);
            }
        });
        addHandler(Attributes.TextView.Gravity, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setGravity(ParseHelper.parseGravity(attributeValue));
            }
        });

        addHandler(Attributes.TextView.TextColor, new ColorResourceProcessor<T>() {

            @Override
            public void setColor(T view, int color) {
                view.setTextColor(color);
            }

            @Override
            public void setColor(T view, ColorStateList colors) {
                view.setTextColor(colors);
            }
        });

        addHandler(Attributes.TextView.TextColorHint, new ColorResourceProcessor<T>() {

            @Override
            public void setColor(T view, int color) {
                view.setHintTextColor(color);
            }

            @Override
            public void setColor(T view, ColorStateList colors) {
                view.setHintTextColor(colors);
            }
        });

        addHandler(Attributes.TextView.TextColorLink, new ColorResourceProcessor<T>() {

            @Override
            public void setColor(T view, int color) {
                view.setLinkTextColor(color);
            }

            @Override
            public void setColor(T view, ColorStateList colors) {
                view.setLinkTextColor(colors);
            }
        });

        addHandler(Attributes.TextView.TextColorHighLight, new ColorResourceProcessor<T>() {

            @Override
            public void setColor(T view, int color) {
                view.setHighlightColor(color);
            }

            @Override
            public void setColor(T view, ColorStateList colors) {
                //
            }
        });

        addHandler(Attributes.TextView.DrawableLeft, new DrawableResourceProcessor<T>() {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                Drawable[] compoundDrawables = view.getCompoundDrawables();
                view.setCompoundDrawablesWithIntrinsicBounds(drawable, compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
            }
        });
        addHandler(Attributes.TextView.DrawableTop, new DrawableResourceProcessor<T>() {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                Drawable[] compoundDrawables = view.getCompoundDrawables();
                view.setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], drawable, compoundDrawables[2], compoundDrawables[3]);
            }
        });
        addHandler(Attributes.TextView.DrawableRight, new DrawableResourceProcessor<T>() {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                Drawable[] compoundDrawables = view.getCompoundDrawables();
                view.setCompoundDrawablesWithIntrinsicBounds(drawable, compoundDrawables[1], drawable, compoundDrawables[3]);
            }
        });
        addHandler(Attributes.TextView.DrawableBottom, new DrawableResourceProcessor<T>() {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                Drawable[] compoundDrawables = view.getCompoundDrawables();
                view.setCompoundDrawablesWithIntrinsicBounds(drawable, compoundDrawables[1], compoundDrawables[2], drawable);
            }
        });

        addHandler(Attributes.TextView.MaxLines, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setMaxLines(ParseHelper.parseInt(attributeValue));
            }
        });

        addHandler(Attributes.TextView.Ellipsize, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                Enum ellipsize = ParseHelper.parseEllipsize(attributeValue);
                view.setEllipsize((android.text.TextUtils.TruncateAt) ellipsize);
            }
        });

        addHandler(Attributes.TextView.PaintFlags, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                if (attributeValue.equals("strike"))
                    view.setPaintFlags(view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        });

        addHandler(Attributes.TextView.Prefix, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setText(attributeValue + view.getText());
            }
        });

        addHandler(Attributes.TextView.Suffix, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setText(view.getText() + attributeValue);
            }
        });

        addHandler(Attributes.TextView.TextStyle, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                int typeface = ParseHelper.parseTextStyle(attributeValue);
                view.setTypeface(Typeface.defaultFromStyle(typeface));
            }
        });

        addHandler(Attributes.TextView.SingleLine, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setSingleLine(ParseHelper.parseBoolean(attributeValue));
            }
        });

        addHandler(Attributes.TextView.TextAllCaps, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    view.setAllCaps(ParseHelper.parseBoolean(attributeValue));
                }
            }
        });
        addHandler(Attributes.TextView.Hint, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setHint(attributeValue);
            }
        });
    }
}
