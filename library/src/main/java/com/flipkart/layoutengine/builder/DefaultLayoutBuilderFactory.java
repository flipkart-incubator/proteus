package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.view.View;

import com.flipkart.layoutengine.parser.ViewParser;
import com.flipkart.layoutengine.parser.custom.ButtonParser;
import com.flipkart.layoutengine.parser.custom.CheckBoxParser;
import com.flipkart.layoutengine.parser.custom.EditTextParser;
import com.flipkart.layoutengine.parser.custom.FrameLayoutParser;
import com.flipkart.layoutengine.parser.custom.HorizontalScrollViewParser;
import com.flipkart.layoutengine.parser.custom.ImageButtonParser;
import com.flipkart.layoutengine.parser.custom.ImageViewParser;
import com.flipkart.layoutengine.parser.custom.LinearLayoutParser;
import com.flipkart.layoutengine.parser.custom.NetworkImageViewParser;
import com.flipkart.layoutengine.parser.custom.RatingBarParser;
import com.flipkart.layoutengine.parser.custom.RelativeLayoutParser;
import com.flipkart.layoutengine.parser.custom.ScrollViewParser;
import com.flipkart.layoutengine.parser.custom.TextViewParser;
import com.flipkart.layoutengine.parser.custom.ViewPagerParser;
import com.flipkart.layoutengine.parser.custom.WebViewParser;
import com.flipkart.layoutengine.provider.Provider;

/**
 * Factory class for creating Layout builders with different predefined behaviours. This is the
 * only way to create layout builder objects. To create a simple layout builder use
 * {@link DefaultLayoutBuilderFactory#createSimpleLayoutBuilder(android.content.Context)}
 */
public class DefaultLayoutBuilderFactory implements LayoutBuilderFactory {

    /**
     * Creates & returns a layout builder which can parse @data blocks as well as custom view blocks.
     * See {@link DataParsingLayoutBuilder}
     *
     * @param context {@link Context} of the activity
     * @return A new {@link DataAndViewParsingLayoutBuilder}
     */
    @Override
    public DataAndViewParsingLayoutBuilder createDataAndViewParsingLayoutBuilder(Context context,
                                                                                 Provider viewProvider) {
        DataAndViewParsingLayoutBuilder builder = new DataAndViewParsingLayoutBuilder(context, viewProvider);
        registerBuiltInHandlers(builder);
        return builder;
    }

    /**
     * Creates & returns a layout builder which can parse @data blocks. See {@link DataParsingLayoutBuilder}
     *
     * @param context {@link Context} of the activity
     * @return A new {@link DataParsingLayoutBuilder}
     */
    @Override
    public DataParsingLayoutBuilder createDataParsingLayoutBuilder(Context context) {
        DataParsingLayoutBuilder builder = new DataParsingLayoutBuilder(context);
        registerBuiltInHandlers(builder);
        return builder;
    }

    /**
     * Creates & returns a simple layout builder. See {@link SimpleLayoutBuilder}
     *
     * @param context {@link Context} of the activity
     * @return A new {@link SimpleLayoutBuilder}
     */
    @Override
    public SimpleLayoutBuilder createSimpleLayoutBuilder(Context context) {
        SimpleLayoutBuilder builder = new SimpleLayoutBuilder(context);
        registerBuiltInHandlers(builder);
        return builder;
    }


    /**
     * This method will register all the internal layout handlers to the builder specified.
     *
     * @param layoutBuilder
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
    }

}
