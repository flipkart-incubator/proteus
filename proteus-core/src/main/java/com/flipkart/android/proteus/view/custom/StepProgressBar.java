package com.flipkart.android.proteus.view.custom;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.flipkart.android.proteus.ProteusView;

/**
 * Created by Prasad Rao on 02-03-2020 12:47
 **/
public class StepProgressBar extends StepProgressView implements ProteusView {

    private Manager manager;

    public StepProgressBar(Context context) {
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
