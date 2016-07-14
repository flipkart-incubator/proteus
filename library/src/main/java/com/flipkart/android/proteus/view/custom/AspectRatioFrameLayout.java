/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Flipkart Internet Pvt. Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.flipkart.android.proteus.view.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by kiran.kumar on 13/05/14.
 */
public class AspectRatioFrameLayout extends FrameLayout {

    private int mAspectRatioWidth;
    private int mAspectRatioHeight;

    public AspectRatioFrameLayout(Context context) {
        super(context);
    }

    public AspectRatioFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AspectRatioFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mAspectRatioHeight > 0 && mAspectRatioWidth > 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            int originalWidth = getMeasuredWidth();

            int originalHeight = getMeasuredHeight();

            int finalWidth, finalHeight;

            if (originalHeight == 0) {
                finalHeight = originalWidth * mAspectRatioHeight / mAspectRatioWidth;
                finalWidth = originalWidth;
            } else if (originalWidth == 0) {
                finalWidth = originalHeight * mAspectRatioWidth / mAspectRatioHeight;
                finalHeight = originalHeight;
            } else {
                finalHeight = originalHeight;
                finalWidth = originalWidth;
            }


            super.onMeasure(
                    MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setAspectRatioWidth(int mAspectRatioWidth) {
        this.mAspectRatioWidth = mAspectRatioWidth;
    }

    public void setAspectRatioHeight(int mAspectRatioHeight) {
        this.mAspectRatioHeight = mAspectRatioHeight;
    }
}
