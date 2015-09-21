package com.flipkart.layoutengine.parser.custom;

import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.widgets.HorizontalProgressBar;

/**
 * HorizontalProgressBarParser
 *
 * @author Aditya Sharat
 */
public class HorizontalProgressBarParser<T extends HorizontalProgressBar> extends WrappableParser<T> {
    public HorizontalProgressBarParser(Parser<T> wrappedParser) {
        super(HorizontalProgressBar.class, wrappedParser);
    }
}
