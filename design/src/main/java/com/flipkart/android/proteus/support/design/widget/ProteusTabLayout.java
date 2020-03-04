package com.flipkart.android.proteus.support.design.widget;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.flipkart.android.proteus.ProteusView;
import com.google.android.material.tabs.TabLayout;

/**
 * Created by Prasad Rao on 28-02-2020 18:17
 **/
public class ProteusTabLayout extends TabLayout implements ProteusView {

    private Manager manager;

    public ProteusTabLayout(@NonNull Context context) {
        super(context);
    }

    @Override
    public Manager getViewManager() {
        return manager;
    }

    @Override
    public void setViewManager(@NonNull Manager manager) {
        this.manager = manager;
    }

    @NonNull
    @Override
    public View getAsView() {
        return this;
    }
}
