/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Flipkart Internet Pvt. Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.flipkart.android.proteus.builder;

import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;

import com.flipkart.android.proteus.parser.ViewParser;
import com.flipkart.android.proteus.parser.custom.ButtonParser;
import com.flipkart.android.proteus.parser.custom.CheckBoxParser;
import com.flipkart.android.proteus.parser.custom.EditTextParser;
import com.flipkart.android.proteus.parser.custom.FrameLayoutParser;
import com.flipkart.android.proteus.parser.custom.HorizontalProgressBarParser;
import com.flipkart.android.proteus.parser.custom.HorizontalScrollViewParser;
import com.flipkart.android.proteus.parser.custom.ImageButtonParser;
import com.flipkart.android.proteus.parser.custom.ImageViewParser;
import com.flipkart.android.proteus.parser.custom.LinearLayoutParser;
import com.flipkart.android.proteus.parser.custom.ProgressBarParser;
import com.flipkart.android.proteus.parser.custom.RatingBarParser;
import com.flipkart.android.proteus.parser.custom.RelativeLayoutParser;
import com.flipkart.android.proteus.parser.custom.ScrollViewParser;
import com.flipkart.android.proteus.parser.custom.TextViewParser;
import com.flipkart.android.proteus.parser.custom.ViewGroupParser;
import com.flipkart.android.proteus.parser.custom.ViewPagerParser;
import com.flipkart.android.proteus.parser.custom.WebViewParser;
import com.flipkart.android.proteus.toolbox.Formatter;
import com.flipkart.android.proteus.toolbox.IdGenerator;
import com.flipkart.android.proteus.toolbox.IdGeneratorImpl;
import com.flipkart.android.proteus.toolbox.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Factory class for creating Layout builders with different predefined behaviours. This is the
 * only way to create layout builder objects. To create a simple layout builder use
 * {@link LayoutBuilderFactory#getSimpleLayoutBuilder(IdGenerator)}
 */
public class LayoutBuilderFactory {

    private SimpleLayoutBuilder simpleLayoutBuilderInstance;
    private DataParsingLayoutBuilder dataParsingLayoutBuilderInstance;
    private DataAndViewParsingLayoutBuilder dataAndViewParsingLayoutBuilderInstance;

    /**
     * Returns a layout builder which can parse @data blocks as well as custom view blocks.
     * See {@link DataParsingLayoutBuilder}
     *
     * @return A new {@link DataAndViewParsingLayoutBuilder}
     */
    public DataAndViewParsingLayoutBuilder getDataAndViewParsingLayoutBuilder(Map<String, JsonObject> layouts, @NonNull IdGenerator idGenerator) {
        if (dataAndViewParsingLayoutBuilderInstance == null) {
            dataAndViewParsingLayoutBuilderInstance = new DataAndViewParsingLayoutBuilder(layouts, idGenerator);
            registerBuiltInHandlers(dataAndViewParsingLayoutBuilderInstance);
            registerFormatter(dataAndViewParsingLayoutBuilderInstance);
        }
        return dataAndViewParsingLayoutBuilderInstance;
    }

    public DataAndViewParsingLayoutBuilder getDataAndViewParsingLayoutBuilder(Map<String, JsonObject> layouts) {
        if (dataAndViewParsingLayoutBuilderInstance == null) {
            dataAndViewParsingLayoutBuilderInstance = new DataAndViewParsingLayoutBuilder(layouts, new IdGeneratorImpl());
            registerBuiltInHandlers(dataAndViewParsingLayoutBuilderInstance);
            registerFormatter(dataAndViewParsingLayoutBuilderInstance);
        }
        return dataAndViewParsingLayoutBuilderInstance;
    }

    /**
     * Returns a layout builder which can parse @data blocks. See {@link DataParsingLayoutBuilder}
     *
     * @return A new {@link DataParsingLayoutBuilder}
     */
    public DataParsingLayoutBuilder getDataParsingLayoutBuilder(@NonNull IdGenerator idGenerator) {
        if (dataParsingLayoutBuilderInstance == null) {
            dataParsingLayoutBuilderInstance = new DataParsingLayoutBuilder(idGenerator);
            registerBuiltInHandlers(dataParsingLayoutBuilderInstance);
            registerFormatter(dataParsingLayoutBuilderInstance);
        }
        return dataParsingLayoutBuilderInstance;
    }

    public DataParsingLayoutBuilder getDataParsingLayoutBuilder() {
        if (dataParsingLayoutBuilderInstance == null) {
            dataParsingLayoutBuilderInstance = new DataParsingLayoutBuilder(new IdGeneratorImpl());
            registerBuiltInHandlers(dataParsingLayoutBuilderInstance);
            registerFormatter(dataParsingLayoutBuilderInstance);
        }
        return dataParsingLayoutBuilderInstance;
    }

    /**
     * Returns a simple layout builder. See {@link SimpleLayoutBuilder}
     *
     * @return A new {@link SimpleLayoutBuilder}
     */
    public SimpleLayoutBuilder getSimpleLayoutBuilder(@NonNull IdGenerator idGenerator) {
        if (simpleLayoutBuilderInstance == null) {
            simpleLayoutBuilderInstance = new SimpleLayoutBuilder(idGenerator);
            registerBuiltInHandlers(simpleLayoutBuilderInstance);
        }
        return simpleLayoutBuilderInstance;
    }

    public SimpleLayoutBuilder getSimpleLayoutBuilder() {
        if (simpleLayoutBuilderInstance == null) {
            simpleLayoutBuilderInstance = new SimpleLayoutBuilder(new IdGeneratorImpl());
            registerBuiltInHandlers(simpleLayoutBuilderInstance);
        }
        return simpleLayoutBuilderInstance;
    }


    /**
     * This method will register all the internal layout handlers to the builder specified.
     *
     * @param layoutBuilder The layout builder which will have handlers registered to it.
     */
    @SuppressWarnings("unchecked")
    protected void registerBuiltInHandlers(LayoutBuilder layoutBuilder) {
        ViewParser viewParser = new ViewParser();
        ImageViewParser imageViewParser = new ImageViewParser(viewParser);
        ImageButtonParser imageButtonParser = new ImageButtonParser(imageViewParser);
        ViewGroupParser viewGroupParser = new ViewGroupParser(viewParser);
        RelativeLayoutParser relativeLayoutParser = new RelativeLayoutParser(viewGroupParser);
        LinearLayoutParser linearLayoutParser = new LinearLayoutParser(viewGroupParser);
        FrameLayoutParser frameLayoutParser = new FrameLayoutParser(viewGroupParser);
        ScrollViewParser scrollViewParser = new ScrollViewParser(frameLayoutParser);
        HorizontalScrollViewParser horizontalScrollViewParser = new HorizontalScrollViewParser(frameLayoutParser);
        TextViewParser textViewParser = new TextViewParser(viewParser);
        EditTextParser editTextParser = new EditTextParser(textViewParser);
        ButtonParser buttonParser = new ButtonParser(textViewParser);
        ViewPagerParser viewPagerParser = new ViewPagerParser(viewGroupParser);
        WebViewParser webViewParser = new WebViewParser(viewParser);
        RatingBarParser ratingBarParser = new RatingBarParser(viewParser);
        CheckBoxParser checkBoxParser = new CheckBoxParser(buttonParser);
        ProgressBarParser progressBarParser = new ProgressBarParser(viewParser);
        HorizontalProgressBarParser horizontalProgressBarParser = new HorizontalProgressBarParser(progressBarParser);

        layoutBuilder.registerHandler("View", viewParser);
        layoutBuilder.registerHandler("ViewGroup", viewGroupParser);
        layoutBuilder.registerHandler("RelativeLayout", relativeLayoutParser);
        layoutBuilder.registerHandler("LinearLayout", linearLayoutParser);
        layoutBuilder.registerHandler("FrameLayout", frameLayoutParser);
        layoutBuilder.registerHandler("ScrollView", scrollViewParser);
        layoutBuilder.registerHandler("HorizontalScrollView", horizontalScrollViewParser);
        layoutBuilder.registerHandler("ImageView", imageViewParser);
        layoutBuilder.registerHandler("TextView", textViewParser);
        layoutBuilder.registerHandler("EditText", editTextParser);
        layoutBuilder.registerHandler("Button", buttonParser);
        layoutBuilder.registerHandler("ImageButton", imageButtonParser);
        layoutBuilder.registerHandler("ViewPager", viewPagerParser);
        layoutBuilder.registerHandler("WebView", webViewParser);
        layoutBuilder.registerHandler("RatingBar", ratingBarParser);
        layoutBuilder.registerHandler("CheckBox", checkBoxParser);
        layoutBuilder.registerHandler("ProgressBar", progressBarParser);
        layoutBuilder.registerHandler("HorizontalProgressBar", horizontalProgressBarParser);
    }

    protected void registerFormatter(DataParsingLayoutBuilder layoutBuilder) {

        Formatter NumberFormatter = new Formatter() {
            @Override
            public String format(JsonElement elementValue) {
                double valueAsNumber;
                try {
                    valueAsNumber = Double.parseDouble(elementValue.getAsString());
                } catch (NumberFormatException e) {
                    return elementValue.toString();
                }
                NumberFormat numberFormat = new DecimalFormat("#,###");
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
                    numberFormat.setRoundingMode(RoundingMode.FLOOR);
                }
                numberFormat.setMinimumFractionDigits(0);
                numberFormat.setMaximumFractionDigits(2);
                return numberFormat.format(valueAsNumber);
            }

            @Override
            public String getName() {
                return "number";
            }
        };

        Formatter DateFormatter = new Formatter() {
            @Override
            public String format(JsonElement elementValue) {
                try {
                    // 2015-06-18 12:01:37
                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(elementValue.getAsString());
                    return new SimpleDateFormat("d MMM, E").format(date);
                } catch (Exception e) {
                    return elementValue.toString();
                }
            }

            @Override
            public String getName() {
                return "date";
            }
        };

        Formatter IndexFormatter = new Formatter() {
            @Override
            public String format(JsonElement elementValue) {
                int valueAsNumber;
                try {
                    valueAsNumber = Integer.parseInt(elementValue.getAsString());
                } catch (NumberFormatException e) {
                    return elementValue.toString();
                }
                return String.valueOf(valueAsNumber + 1);
            }

            @Override
            public String getName() {
                return "index";
            }
        };

        Formatter joinFormatter = new Formatter() {
            @Override
            public String format(JsonElement elementValue) {
                if (elementValue.isJsonArray()) {
                    return Utils.getStringFromArray(elementValue.getAsJsonArray(), ",");
                } else {
                    return elementValue.toString();
                }
            }

            @Override
            public String getName() {
                return "join";
            }
        };

        layoutBuilder.registerFormatter(NumberFormatter);
        layoutBuilder.registerFormatter(DateFormatter);
        layoutBuilder.registerFormatter(IndexFormatter);
        layoutBuilder.registerFormatter(joinFormatter);
    }
}
