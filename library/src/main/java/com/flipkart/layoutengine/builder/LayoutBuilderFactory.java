package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.os.Build;
import android.view.View;

import com.flipkart.layoutengine.parser.ViewParser;
import com.flipkart.layoutengine.parser.custom.ButtonParser;
import com.flipkart.layoutengine.parser.custom.CheckBoxParser;
import com.flipkart.layoutengine.parser.custom.EditTextParser;
import com.flipkart.layoutengine.parser.custom.FrameLayoutParser;
import com.flipkart.layoutengine.parser.custom.HorizontalProgressBarParser;
import com.flipkart.layoutengine.parser.custom.HorizontalScrollViewParser;
import com.flipkart.layoutengine.parser.custom.ImageButtonParser;
import com.flipkart.layoutengine.parser.custom.ImageViewParser;
import com.flipkart.layoutengine.parser.custom.LinearLayoutParser;
import com.flipkart.layoutengine.parser.custom.NetworkImageViewParser;
import com.flipkart.layoutengine.parser.custom.ProgressBarParser;
import com.flipkart.layoutengine.parser.custom.RatingBarParser;
import com.flipkart.layoutengine.parser.custom.RelativeLayoutParser;
import com.flipkart.layoutengine.parser.custom.ScrollViewParser;
import com.flipkart.layoutengine.parser.custom.TextViewParser;
import com.flipkart.layoutengine.parser.custom.ViewPagerParser;
import com.flipkart.layoutengine.parser.custom.WebViewParser;
import com.flipkart.layoutengine.toolbox.Formatter;
import com.flipkart.layoutengine.toolbox.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Factory class for creating Layout builders with different predefined behaviours. This is the
 * only way to create layout builder objects. To create a simple layout builder use
 * {@link LayoutBuilderFactory#getSimpleLayoutBuilder(android.content.Context)}
 */
public class LayoutBuilderFactory {

    private SimpleLayoutBuilder simpleLayoutBuilderInstance;
    private DataParsingLayoutBuilder dataParsingLayoutBuilderInstance;
    private DataAndViewParsingLayoutBuilder dataAndViewParsingLayoutBuilderInstance;

    /**
     * Returns a layout builder which can parse @data blocks as well as custom view blocks.
     * See {@link DataParsingLayoutBuilder}
     *
     * @param context {@link Context} of the activity
     * @return A new {@link DataAndViewParsingLayoutBuilder}
     */
    public DataAndViewParsingLayoutBuilder getDataAndViewParsingLayoutBuilder(Context context,
                                                                              JsonObject viewProvider) {
        if (dataAndViewParsingLayoutBuilderInstance == null) {
            dataAndViewParsingLayoutBuilderInstance = new DataAndViewParsingLayoutBuilder(context, viewProvider);
            registerBuiltInHandlers(dataAndViewParsingLayoutBuilderInstance);
            registerFormatter(dataAndViewParsingLayoutBuilderInstance);
        }
        return dataAndViewParsingLayoutBuilderInstance;
    }

    /**
     * Returns a layout builder which can parse @data blocks. See {@link DataParsingLayoutBuilder}
     *
     * @param context {@link Context} of the activity
     * @return A new {@link DataParsingLayoutBuilder}
     */
    public DataParsingLayoutBuilder getDataParsingLayoutBuilder(Context context) {
        if (dataParsingLayoutBuilderInstance == null) {
            dataParsingLayoutBuilderInstance = new DataParsingLayoutBuilder(context);
            registerBuiltInHandlers(dataParsingLayoutBuilderInstance);
            registerFormatter(dataParsingLayoutBuilderInstance);
        }
        return dataParsingLayoutBuilderInstance;
    }

    /**
     * Returns a simple layout builder. See {@link SimpleLayoutBuilder}
     *
     * @param context {@link Context} of the activity
     * @return A new {@link SimpleLayoutBuilder}
     */
    public SimpleLayoutBuilder getSimpleLayoutBuilder(Context context) {
        if (simpleLayoutBuilderInstance == null) {
            simpleLayoutBuilderInstance = new SimpleLayoutBuilder(context);
            registerBuiltInHandlers(simpleLayoutBuilderInstance);
        }
        return simpleLayoutBuilderInstance;
    }


    /**
     * This method will register all the internal layout handlers to the builder specified.
     *
     * @param layoutBuilder The layout builder which will have handlers registered to it.
     */
    protected void registerBuiltInHandlers(LayoutBuilder layoutBuilder) {
        ViewParser viewParser = new ViewParser(View.class);
        ImageViewParser imageViewParser = new ImageViewParser(viewParser);
        ImageButtonParser imageButtonParser = new ImageButtonParser(imageViewParser);
        NetworkImageViewParser networkImageViewParser = new NetworkImageViewParser(imageViewParser);
        RelativeLayoutParser relativeLayoutParser = new RelativeLayoutParser(viewParser);
        LinearLayoutParser linearLayoutParser = new LinearLayoutParser(viewParser);
        FrameLayoutParser frameLayoutParser = new FrameLayoutParser(viewParser);
        ScrollViewParser scrollViewParser = new ScrollViewParser(viewParser);
        HorizontalScrollViewParser horizontalScrollViewParser = new HorizontalScrollViewParser(viewParser);
        TextViewParser textViewParser = new TextViewParser(viewParser);
        EditTextParser editTextParser = new EditTextParser(textViewParser);
        ButtonParser buttonParser = new ButtonParser(textViewParser);
        ViewPagerParser viewPagerParser = new ViewPagerParser(viewParser);
        WebViewParser webViewParser = new WebViewParser(viewParser);
        RatingBarParser ratingBarParser = new RatingBarParser(viewParser);
        CheckBoxParser checkBoxParser = new CheckBoxParser(buttonParser);
        ProgressBarParser progressBarParser = new ProgressBarParser(viewParser);
        HorizontalProgressBarParser horizontalProgressBarParser = new HorizontalProgressBarParser(progressBarParser);

        layoutBuilder.registerHandler("View", viewParser);
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
        layoutBuilder.registerHandler("NetworkImageView", networkImageViewParser);
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
