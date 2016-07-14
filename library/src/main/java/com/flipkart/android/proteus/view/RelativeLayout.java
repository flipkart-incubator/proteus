package com.flipkart.android.proteus.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import com.flipkart.android.proteus.view.manager.ProteusViewManager;

/**
 * RelativeLayout
 *
 * @author aditya.sharat
 */
public class RelativeLayout extends android.widget.RelativeLayout implements ProteusView {

    private ProteusViewManager viewManager;

    public RelativeLayout(Context context) {
        super(context);
    }

    public RelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
