package com.flipkart.android.proteus.parser.custom;


import com.flipkart.android.proteus.parser.Parser;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.view.Button;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class ButtonParser<T extends Button> extends WrappableParser<T> {

    public ButtonParser(Parser<T> wrappedParser) {
        super(Button.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers() {
        super.prepareHandlers();
    }
}
