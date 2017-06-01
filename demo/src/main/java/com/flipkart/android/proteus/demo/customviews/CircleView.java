/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 *
 * Copyright (c) 2017 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.flipkart.android.proteus.demo.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.flipkart.android.proteus.ProteusView;

/**
 * CircleView
 *
 * @author aditya.sharat
 */

public class CircleView extends View implements ProteusView {

    Manager viewManager;
    private String HEX_COLOR = "#45ba8a";
    private Paint drawPaint;
    float radius;

    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setColor(String HEX_COLOR) {
        this.HEX_COLOR = HEX_COLOR;
    }

    @Override
    public Manager getViewManager() {
        return viewManager;
    }

    @Override
    public void setViewManager(@NonNull Manager viewManager) {
        this.viewManager = viewManager;
    }

    private void init() {
        drawPaint = new Paint();
        drawPaint.setColor(Color.parseColor(HEX_COLOR));
        drawPaint.setAntiAlias(true);
        setOnMeasureCallback();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(radius, radius, radius, drawPaint);
    }

    private void setOnMeasureCallback() {
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                removeOnGlobalLayoutListener(this);
                radius = getMeasuredWidth() / 2;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void removeOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < 16) {
            //noinspection deprecation
            getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    @NonNull
    @Override
    public View getAsView() {
        return this;
    }

}
