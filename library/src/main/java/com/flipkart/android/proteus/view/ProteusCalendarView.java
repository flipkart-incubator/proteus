package com.flipkart.android.proteus.view;

import android.content.Context;
import android.widget.CalendarView;

import com.flipkart.android.proteus.view.manager.ProteusViewManager;

/**
 * Created by mac on 3/27/17.
 */

public class ProteusCalendarView extends CalendarView implements ProteusView {
    private ProteusViewManager viewManager;

    public ProteusCalendarView(Context context) {
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
