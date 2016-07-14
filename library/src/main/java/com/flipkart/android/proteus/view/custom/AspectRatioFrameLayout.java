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
