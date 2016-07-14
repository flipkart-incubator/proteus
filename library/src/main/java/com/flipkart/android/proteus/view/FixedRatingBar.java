package com.flipkart.android.proteus.view;

import android.content.Context;
import android.util.AttributeSet;

import com.flipkart.android.proteus.view.manager.ProteusViewManager;

/**
 * FixedRatingBar
 *
 * @author aditya.sharat
 */
public class FixedRatingBar extends com.flipkart.android.proteus.view.custom.FixedRatingBar implements ProteusView {

    private ProteusViewManager viewManager;

    public FixedRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FixedRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedRatingBar(Context context) {
        super(context);
    }

    @Override
    public ProteusViewManager getViewManager() {
        return viewManager;
    }

    @Override
    public void setViewManager(ProteusViewManager proteusViewManager) {
        this.viewManager = proteusViewManager;
    }
}
