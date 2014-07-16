package com.flipkart.layoutengine.builder;

import android.app.Activity;
import android.view.View;

import com.flipkart.layoutengine.parser.custom.NetworkImageViewParser;
import com.flipkart.layoutengine.parser.custom.RelativeLayoutParser;
import com.flipkart.layoutengine.provider.Provider;
import com.flipkart.layoutengine.parser.ViewParser;
import com.flipkart.layoutengine.parser.custom.FrameLayoutParser;
import com.flipkart.layoutengine.parser.custom.HorizontalScrollViewParser;
import com.flipkart.layoutengine.parser.custom.ImageViewParser;
import com.flipkart.layoutengine.parser.custom.LinearLayoutParser;
import com.flipkart.layoutengine.parser.custom.ScrollViewParser;
import com.flipkart.layoutengine.parser.custom.TextViewParser;
import com.flipkart.layoutengine.parser.custom.ViewPagerParser;

/**
 * Factory class for creating Layout builders with different predefined behaviours. This is the only way to create layout builder objects.
 * To create a simple layout builder use {@link LayoutBuilderFactory#createSimpleLayoutBuilder(android.app.Activity)}
 */
public class LayoutBuilderFactory {

    /**
     * Creates & returns a layout builder which can parse @data blocks as well as custom view blocks. See {@link DataParsingLayoutBuilder}
     * @param activity
     * @param dataProvider
     * @return
     */
    static public DataParsingLayoutBuilder createDataAndViewParsingLayoutBuilder(Activity activity, Provider dataProvider, Provider viewProvider)
    {
        DataParsingLayoutBuilder builder = new DataAndViewParsingLayoutBuilder(activity, dataProvider, viewProvider);
        registerBuiltInHandlers(builder);
        return builder;
    }

    /**
     * Creates & returns a layout builder which can parse @data blocks. See {@link DataParsingLayoutBuilder}
     * @param activity
     * @param dataProvider
     * @return
     */
    static public DataParsingLayoutBuilder createDataParsingLayoutBuilder(Activity activity, Provider dataProvider)
    {
        DataParsingLayoutBuilder builder = new DataParsingLayoutBuilder(activity, dataProvider);
        registerBuiltInHandlers(builder);
        return builder;
    }

    /**
     * Creates & returns a simple layout builder. See {@link SimpleLayoutBuilder}
     * @param activity
     * @return
     */
    static public SimpleLayoutBuilder createSimpleLayoutBuilder(Activity activity)
    {
        SimpleLayoutBuilder builder = new SimpleLayoutBuilder(activity);
        registerBuiltInHandlers(builder);
        return builder;
    }


    /**
     * This method will register all the internal layout handlers to the builder specified.
     * @param layoutBuilder
     */
    static private void registerBuiltInHandlers(SimpleLayoutBuilder layoutBuilder) {
        layoutBuilder.registerHandler("container.relative",new RelativeLayoutParser());
        layoutBuilder.registerHandler("container.linear", new LinearLayoutParser());
        layoutBuilder.registerHandler("container.absolute", new FrameLayoutParser());
        layoutBuilder.registerHandler("container.verticalScroll", new ScrollViewParser());
        layoutBuilder.registerHandler("container.horizontalScroll", new HorizontalScrollViewParser());
        layoutBuilder.registerHandler("networkImage", new NetworkImageViewParser());
        layoutBuilder.registerHandler("image", new ImageViewParser());
        layoutBuilder.registerHandler("text", new TextViewParser());
        layoutBuilder.registerHandler("pager", new ViewPagerParser());
        layoutBuilder.registerHandler("view", new ViewParser(View.class));

    }

}
