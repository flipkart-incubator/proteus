package com.flipkart.layoutengine.parser.custom;

import android.widget.RelativeLayout;

import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;

/**
 * Created by kirankumar on 10/07/14.
 */
public class RelativeLayoutParser<T extends RelativeLayout> extends WrappableParser<T> {

    public RelativeLayoutParser(Parser<T> parentParser)
    {
        super(RelativeLayout.class,parentParser);
    }

}
