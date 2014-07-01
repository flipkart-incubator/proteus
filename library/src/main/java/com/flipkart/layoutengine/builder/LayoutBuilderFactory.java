package com.flipkart.layoutengine.builder;

import android.app.Activity;
import android.view.View;

import com.flipkart.layoutengine.datasource.DataSource;
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

    static public DataParsingLayoutBuilder createDataParsingLayoutBuilder(Activity activity, DataSource dataSource)
    {
        DataParsingLayoutBuilder builder = new DataParsingLayoutBuilder(activity,dataSource);
        registerBuiltInHandlers(builder);
        return builder;
    }

    static public SimpleLayoutBuilder createSimpleLayoutBuilder(Activity activity)
    {
        SimpleLayoutBuilder builder = new SimpleLayoutBuilder(activity);
        registerBuiltInHandlers(builder);
        return builder;
    }


    static private void registerBuiltInHandlers(SimpleLayoutBuilder layoutBuilder) {
        layoutBuilder.registerHandler("container.linear", new LinearLayoutParser());
        layoutBuilder.registerHandler("container.absolute", new FrameLayoutParser());
        layoutBuilder.registerHandler("container.verticalscroll", new ScrollViewParser());
        layoutBuilder.registerHandler("container.horizontalscroll", new HorizontalScrollViewParser());
        layoutBuilder.registerHandler("image", new ImageViewParser());
        layoutBuilder.registerHandler("text", new TextViewParser());
        layoutBuilder.registerHandler("pager", new ViewPagerParser());
        layoutBuilder.registerHandler("view", new ViewParser(View.class));

    }

}
