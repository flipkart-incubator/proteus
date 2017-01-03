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

package com.flipkart.android.proteus.parser.custom;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flipkart.android.proteus.Layout;
import com.flipkart.android.proteus.parser.Attributes;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.parser.BaseTypeParser;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.processor.ColorResourceProcessor;
import com.flipkart.android.proteus.processor.DimensionAttributeProcessor;
import com.flipkart.android.proteus.processor.DrawableResourceProcessor;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.view.ProteusTextView;
import com.flipkart.android.proteus.view.ProteusView;
import com.google.gson.JsonObject;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class TextViewParser<T extends TextView> extends WrappableParser<T> {

    public TextViewParser(BaseTypeParser<T> wrappedParser) {
        super(wrappedParser);
    }

    @Override
    public ProteusView createView(ViewGroup parent, Layout layout, JsonObject data, Styles styles, int index) {
        return new ProteusTextView(parent.getContext());
    }

    @Override
    protected void registerAttributeProcessors() {
        super.registerAttributeProcessors();
        addAttributeProcessor(Attributes.TextView.HTML, new StringAttributeProcessor<T>() {
            @Override
            public void handle(T view, String value) {
                view.setText(Html.fromHtml(value));
            }
        });
        addAttributeProcessor(Attributes.TextView.Text, new StringAttributeProcessor<T>() {
            @Override
            public void handle(T view, String value) {
                view.setText(value);
            }
        });

        addAttributeProcessor(Attributes.TextView.DrawablePadding, new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                view.setCompoundDrawablePadding((int) dimension);
            }
        });

        addAttributeProcessor(Attributes.TextView.TextSize, new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimension);
            }
        });
        addAttributeProcessor(Attributes.TextView.Gravity, new StringAttributeProcessor<T>() {
            @Override
            public void handle(T view, String value) {
                view.setGravity(ParseHelper.parseGravity(value));
            }
        });

        addAttributeProcessor(Attributes.TextView.TextColor, new ColorResourceProcessor<T>() {

            @Override
            public void setColor(T view, int color) {
                view.setTextColor(color);
            }

            @Override
            public void setColor(T view, ColorStateList colors) {
                view.setTextColor(colors);
            }
        });

        addAttributeProcessor(Attributes.TextView.TextColorHint, new ColorResourceProcessor<T>() {

            @Override
            public void setColor(T view, int color) {
                view.setHintTextColor(color);
            }

            @Override
            public void setColor(T view, ColorStateList colors) {
                view.setHintTextColor(colors);
            }
        });

        addAttributeProcessor(Attributes.TextView.TextColorLink, new ColorResourceProcessor<T>() {

            @Override
            public void setColor(T view, int color) {
                view.setLinkTextColor(color);
            }

            @Override
            public void setColor(T view, ColorStateList colors) {
                view.setLinkTextColor(colors);
            }
        });

        addAttributeProcessor(Attributes.TextView.TextColorHighLight, new ColorResourceProcessor<T>() {

            @Override
            public void setColor(T view, int color) {
                view.setHighlightColor(color);
            }

            @Override
            public void setColor(T view, ColorStateList colors) {
                //
            }
        });

        addAttributeProcessor(Attributes.TextView.DrawableLeft, new DrawableResourceProcessor<T>() {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                Drawable[] compoundDrawables = view.getCompoundDrawables();
                view.setCompoundDrawablesWithIntrinsicBounds(drawable, compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
            }
        });
        addAttributeProcessor(Attributes.TextView.DrawableTop, new DrawableResourceProcessor<T>() {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                Drawable[] compoundDrawables = view.getCompoundDrawables();
                view.setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], drawable, compoundDrawables[2], compoundDrawables[3]);
            }
        });
        addAttributeProcessor(Attributes.TextView.DrawableRight, new DrawableResourceProcessor<T>() {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                Drawable[] compoundDrawables = view.getCompoundDrawables();
                view.setCompoundDrawablesWithIntrinsicBounds(drawable, compoundDrawables[1], drawable, compoundDrawables[3]);
            }
        });
        addAttributeProcessor(Attributes.TextView.DrawableBottom, new DrawableResourceProcessor<T>() {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                Drawable[] compoundDrawables = view.getCompoundDrawables();
                view.setCompoundDrawablesWithIntrinsicBounds(drawable, compoundDrawables[1], compoundDrawables[2], drawable);
            }
        });

        addAttributeProcessor(Attributes.TextView.MaxLines, new StringAttributeProcessor<T>() {
            @Override
            public void handle(T view, String value) {
                view.setMaxLines(ParseHelper.parseInt(value));
            }
        });

        addAttributeProcessor(Attributes.TextView.Ellipsize, new StringAttributeProcessor<T>() {
            @Override
            public void handle(T view, String value) {
                Enum ellipsize = ParseHelper.parseEllipsize(value);
                view.setEllipsize((android.text.TextUtils.TruncateAt) ellipsize);
            }
        });

        addAttributeProcessor(Attributes.TextView.PaintFlags, new StringAttributeProcessor<T>() {
            @Override
            public void handle(T view, String value) {
                if (value.equals("strike"))
                    view.setPaintFlags(view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        });

        addAttributeProcessor(Attributes.TextView.Prefix, new StringAttributeProcessor<T>() {
            @Override
            public void handle(T view, String value) {
                view.setText(value + view.getText());
            }
        });

        addAttributeProcessor(Attributes.TextView.Suffix, new StringAttributeProcessor<T>() {
            @Override
            public void handle(T view, String value) {
                view.setText(view.getText() + value);
            }
        });

        addAttributeProcessor(Attributes.TextView.TextStyle, new StringAttributeProcessor<T>() {
            @Override
            public void handle(T view, String value) {
                int typeface = ParseHelper.parseTextStyle(value);
                view.setTypeface(Typeface.defaultFromStyle(typeface));
            }
        });

        addAttributeProcessor(Attributes.TextView.SingleLine, new StringAttributeProcessor<T>() {
            @Override
            public void handle(T view, String value) {
                view.setSingleLine(ParseHelper.parseBoolean(value));
            }
        });

        addAttributeProcessor(Attributes.TextView.TextAllCaps, new StringAttributeProcessor<T>() {
            @Override
            public void handle(T view, String value) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    view.setAllCaps(ParseHelper.parseBoolean(value));
                }
            }
        });
        addAttributeProcessor(Attributes.TextView.Hint, new StringAttributeProcessor<T>() {
            @Override
            public void handle(T view, String value) {
                view.setHint(value);
            }
        });
    }
}
