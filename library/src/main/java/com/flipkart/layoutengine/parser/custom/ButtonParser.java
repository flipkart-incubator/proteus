package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class ButtonParser extends WrappableParser<TextView> {

    public ButtonParser(Parser<TextView> wrappedParser) {
        super(Button.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(Context context) {
        super.prepareHandlers(context);


    }
}
