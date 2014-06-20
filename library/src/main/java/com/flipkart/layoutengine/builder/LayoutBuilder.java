package com.flipkart.layoutengine.builder;

import android.app.Activity;
import android.view.View;

import com.flipkart.layoutengine.parser.ViewParser;
import com.flipkart.layoutengine.parser.custom.FrameLayoutParser;
import com.flipkart.layoutengine.parser.custom.HorizontalScrollViewParser;
import com.flipkart.layoutengine.parser.custom.ImageViewParser;
import com.flipkart.layoutengine.parser.custom.LinearLayoutParser;
import com.flipkart.layoutengine.parser.custom.ScrollViewParser;
import com.flipkart.layoutengine.parser.custom.TextViewParser;
import com.flipkart.layoutengine.parser.custom.ViewPagerParser;
import com.flipkart.layoutengine.parser.data.DataParsingAdapter;

/**
 * Created by kirankumar on 19/06/14.
 */
public class LayoutBuilder extends LayoutBuilderImpl {
    public LayoutBuilder(Activity activity) {
        super(activity);
        init(activity);
    }

    private void init(Activity activity) {
        registerHandlers();
    }

    private void registerHandlers() {
        registerDataParsingHandler("container.linear", new LinearLayoutParser());
        registerDataParsingHandler("container.absolute", new FrameLayoutParser());
        registerDataParsingHandler("container.verticalscroll", new ScrollViewParser());
        registerDataParsingHandler("container.horizontalscroll", new HorizontalScrollViewParser());
        registerDataParsingHandler("image", new ImageViewParser());
        registerDataParsingHandler("text", new TextViewParser());
        registerDataParsingHandler("pager", new ViewPagerParser());
        registerDataParsingHandler("view", new ViewParser(View.class));

    }

    private void registerDataParsingHandler(String viewType, LayoutHandler handler)
    {
        registerHandler(viewType, new DataParsingAdapter(handler));
    }


}
