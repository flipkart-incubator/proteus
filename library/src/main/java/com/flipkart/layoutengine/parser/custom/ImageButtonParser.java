package com.flipkart.layoutengine.parser.custom;

import android.widget.ImageButton;

import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;

/**
 * Created by kirankumar on 25/11/14.
 */
public class ImageButtonParser<T extends ImageButton> extends WrappableParser<T> {
    public ImageButtonParser(Parser<T> parentParser) {
        super(ImageButton.class,parentParser);
    }
}
