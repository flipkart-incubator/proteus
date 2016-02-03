package com.flipkart.layoutengine.view.custom;

import android.content.Context;
import android.widget.ProgressBar;

/**
 * HorizontalProgressBar
 *
 * @author Aditya Sharat
 */
public class HorizontalProgressBar extends ProgressBar {
    public HorizontalProgressBar(Context context) {
        super(context, null, android.R.attr.progressBarStyleHorizontal);
    }
}
