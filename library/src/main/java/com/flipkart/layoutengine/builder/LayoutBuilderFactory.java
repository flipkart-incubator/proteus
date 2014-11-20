package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.view.View;

import com.flipkart.layoutengine.parser.ViewParser;
import com.flipkart.layoutengine.parser.custom.ButtonParser;
import com.flipkart.layoutengine.parser.custom.FrameLayoutParser;
import com.flipkart.layoutengine.parser.custom.HorizontalScrollViewParser;
import com.flipkart.layoutengine.parser.custom.ImageViewParser;
import com.flipkart.layoutengine.parser.custom.LinearLayoutParser;
import com.flipkart.layoutengine.parser.custom.NetworkImageViewParser;
import com.flipkart.layoutengine.parser.custom.RelativeLayoutParser;
import com.flipkart.layoutengine.parser.custom.ScrollViewParser;
import com.flipkart.layoutengine.parser.custom.TextViewParser;
import com.flipkart.layoutengine.parser.custom.ViewPagerParser;
import com.flipkart.layoutengine.provider.Provider;

/**
 * Factory class for creating Layout builders with different predefined behaviours. This is the only way to create layout builder objects.
 * To create a simple layout builder use {@link LayoutBuilderFactory#createSimpleLayoutBuilder(android.content.Context)}
 */
public class LayoutBuilderFactory {

    /**
     * Creates & returns a layout builder which can parse @data blocks as well as custom view blocks. See {@link DataParsingLayoutBuilder}
     * @param context
     * @param dataProvider
     * @return
     */
    static public DataParsingLayoutBuilder createDataAndViewParsingLayoutBuilder(Context context, Provider dataProvider, Provider viewProvider)
    {
        DataParsingLayoutBuilder builder = new DataAndViewParsingLayoutBuilder(context, dataProvider, viewProvider);
        registerBuiltInHandlers(builder);
        return builder;
    }

    /**
     * Creates & returns a layout builder which can parse @data blocks. See {@link DataParsingLayoutBuilder}
     * @param context
     * @param dataProvider
     * @return
     */
    static public DataParsingLayoutBuilder createDataParsingLayoutBuilder(Context context, Provider dataProvider)
    {
        DataParsingLayoutBuilder builder = new DataParsingLayoutBuilder(context, dataProvider);
        registerBuiltInHandlers(builder);
        return builder;
    }

    /**
     * Creates & returns a simple layout builder. See {@link SimpleLayoutBuilder}
     * @param context
     * @return
     */
    static public SimpleLayoutBuilder createSimpleLayoutBuilder(Context context)
    {
        SimpleLayoutBuilder builder = new SimpleLayoutBuilder(context);
        registerBuiltInHandlers(builder);
        return builder;
    }


    /**
     * This method will register all the internal layout handlers to the builder specified.
     * @param layoutBuilder
     */
    static private void registerBuiltInHandlers(SimpleLayoutBuilder layoutBuilder) {
        ViewParser viewParser = new ViewParser(View.class);
        ImageViewParser imageViewParser = new ImageViewParser(viewParser);
        NetworkImageViewParser networkImageViewParser = new NetworkImageViewParser(imageViewParser);
        RelativeLayoutParser relativeLayoutParser = new RelativeLayoutParser(viewParser);
        LinearLayoutParser linearLayoutParser = new LinearLayoutParser(viewParser);
        FrameLayoutParser frameLayoutParser = new FrameLayoutParser(viewParser);
        ScrollViewParser scrollViewParser = new ScrollViewParser(viewParser);
        HorizontalScrollViewParser horizontalScrollViewParser = new HorizontalScrollViewParser(viewParser);
        TextViewParser textViewParser = new TextViewParser(viewParser);
        ButtonParser buttonParser = new ButtonParser(textViewParser);
        ViewPagerParser viewPagerParser = new ViewPagerParser(viewParser);



        layoutBuilder.registerHandler("RelativeLayout",relativeLayoutParser);
        layoutBuilder.registerHandler("LinearLayout",linearLayoutParser);
        layoutBuilder.registerHandler("FrameLayout",frameLayoutParser);
        layoutBuilder.registerHandler("ScrollView",scrollViewParser);
        layoutBuilder.registerHandler("HorizontalScrollView",horizontalScrollViewParser);
        layoutBuilder.registerHandler("NetworkImageView",networkImageViewParser);
        layoutBuilder.registerHandler("TextView",textViewParser);
        layoutBuilder.registerHandler("Button",buttonParser);
        layoutBuilder.registerHandler("ViewPager",viewPagerParser);
        layoutBuilder.registerHandler("View",viewParser);

    }

}
