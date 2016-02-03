package com.flipkart.layoutengine.parser.custom;


import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.view.EditText;

/**
 * Created by kirankumar on 25/11/14.
 */
public class EditTextParser<T extends EditText> extends WrappableParser<T> {
    public EditTextParser(Parser<T> wrappedParser) {
        super(EditText.class, wrappedParser);
    }
}
