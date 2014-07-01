package com.flipkart.layoutengine.builder;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.datasource.DataSource;
import com.flipkart.layoutengine.parser.ViewParser;
import com.flipkart.layoutengine.parser.custom.FrameLayoutParser;
import com.flipkart.layoutengine.parser.custom.HorizontalScrollViewParser;
import com.flipkart.layoutengine.parser.custom.ImageViewParser;
import com.flipkart.layoutengine.parser.custom.LinearLayoutParser;
import com.flipkart.layoutengine.parser.custom.ScrollViewParser;
import com.flipkart.layoutengine.parser.custom.TextViewParser;
import com.flipkart.layoutengine.parser.custom.ViewPagerParser;
import com.flipkart.layoutengine.datasource.DataParsingAdapter;
import com.google.gson.JsonObject;

/**
 * Created by kirankumar on 19/06/14.
 */
public class DataParsingLayoutBuilder extends SimpleLayoutBuilder {
    private final DataSource dataSource;

    DataParsingLayoutBuilder(Activity activity, DataSource dataSource) {
        super(activity);
        this.dataSource = dataSource;
    }


    @Override
    public void registerHandler(String viewType, LayoutHandler handler) {
        super.registerHandler(viewType, new DataParsingAdapter(dataSource,handler));
    }




    @Override
    public View build(ViewGroup parent, JsonObject jsonObject) {

        return super.build(parent, jsonObject);
    }
}
