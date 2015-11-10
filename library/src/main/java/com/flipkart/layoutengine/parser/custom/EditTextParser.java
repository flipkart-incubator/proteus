package com.flipkart.layoutengine.parser.custom;

import android.widget.EditText;

import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;

/**
 * Created by kirankumar on 25/11/14.
 */
public class EditTextParser<T extends EditText> extends WrappableParser<T> {
    public EditTextParser(Parser<T> wrappedParser) {
        super(EditText.class, wrappedParser);
    }
}
