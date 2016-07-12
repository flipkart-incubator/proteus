package com.flipkart.proteus.parser.custom;


import com.flipkart.proteus.parser.Parser;
import com.flipkart.proteus.parser.WrappableParser;
import com.flipkart.proteus.view.EditText;

/**
 * Created by kirankumar on 25/11/14.
 */
public class EditTextParser<T extends EditText> extends WrappableParser<T> {
    public EditTextParser(Parser<T> wrappedParser) {
        super(EditText.class, wrappedParser);
    }
}
