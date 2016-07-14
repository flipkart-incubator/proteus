package com.flipkart.android.proteus.parser.custom;


import com.flipkart.android.proteus.parser.Parser;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.view.EditText;

/**
 * Created by kirankumar on 25/11/14.
 */
public class EditTextParser<T extends EditText> extends WrappableParser<T> {
    public EditTextParser(Parser<T> wrappedParser) {
        super(EditText.class, wrappedParser);
    }
}
