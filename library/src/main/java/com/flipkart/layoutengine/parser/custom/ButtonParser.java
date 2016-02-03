package com.flipkart.layoutengine.parser.custom;


import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.view.Button;

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
