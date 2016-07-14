package com.flipkart.android.proteus.view;

import android.content.Context;

import com.flipkart.android.proteus.view.manager.ProteusViewManager;

/**
 * HorizontalProgressBar
 *
 * @author aditya.sharat
 */
public class HorizontalProgressBar extends com.flipkart.android.proteus.view.custom.HorizontalProgressBar implements ProteusView {

    private ProteusViewManager viewManager;

    public HorizontalProgressBar(Context context) {
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
