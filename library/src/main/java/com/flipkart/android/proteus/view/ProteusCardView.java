package com.flipkart.android.proteus.view;

import android.content.Context;
import android.support.v7.widget.CardView;

import com.flipkart.android.proteus.view.manager.ProteusViewManager;

/**
 * Created by mac on 3/24/17.
 */

public class ProteusCardView extends CardView implements ProteusView {

    private ProteusViewManager viewManager;

    public ProteusCardView(Context context) {
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
