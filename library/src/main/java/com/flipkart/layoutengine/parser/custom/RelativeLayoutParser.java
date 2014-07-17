package com.flipkart.layoutengine.parser.custom;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.ViewParser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.widgets.AspectRatioFrameLayout;

/**
 * Created by kirankumar on 10/07/14.
 */
public class RelativeLayoutParser extends WrappableParser<RelativeLayout> {

    public RelativeLayoutParser(Parser<RelativeLayout> parentParser)
    {
        super(RelativeLayout.class,parentParser);
    }

}
