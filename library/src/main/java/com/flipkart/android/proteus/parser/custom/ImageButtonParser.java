package com.flipkart.android.proteus.parser.custom;

import com.flipkart.android.proteus.parser.Parser;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.view.ImageButton;

/**
 * Created by kirankumar on 25/11/14.
 */
public class ImageButtonParser<T extends ImageButton> extends WrappableParser<T> {
    public ImageButtonParser(Parser<T> parentParser) {
        super(ImageButton.class, parentParser);
    }
}
