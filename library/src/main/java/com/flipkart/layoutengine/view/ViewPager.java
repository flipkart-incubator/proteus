package com.flipkart.layoutengine.view;

import android.content.Context;
import android.util.AttributeSet;

import com.flipkart.layoutengine.view.manager.ProteusViewManager;

/**
 * ViewPager
 *
 * @author aditya.sharat
 */
public class ViewPager extends android.support.v4.view.ViewPager implements ProteusView {

    private ProteusViewManager viewManager;

    public ViewPager(Context context) {
        super(context);
    }

    public ViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
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
