package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.widget.Button;

import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class ButtonParser<T extends Button> extends WrappableParser<T> {

    public ButtonParser(Parser<T> wrappedParser) {
        super(Button.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(Context context) {
        super.prepareHandlers(context);
    }
}
