package com.flipkart.layoutengine.parser.custom;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.flipkart.layoutengine.parser.ViewParser;

/**
 * Created by kirankumar on 10/07/14.
 */
public class RelativeLayoutParser extends ViewParser<RelativeLayout> {

    public RelativeLayoutParser(Class viewClass)
    {
        super(viewClass);
    }
    public RelativeLayoutParser() {
        super(RelativeLayout.class);
    }

    @Override
    protected void prepareHandlers(Activity activity) {
        super.prepareHandlers(activity);
    }
}
