package com.flipkart.layoutengine.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by kiran.kumar on 13/05/14.
 */
public class AspectRatioFrameLayout extends FrameLayout {

        private int mAspectRatioWidth;
        private int mAspectRatioHeight;

        public AspectRatioFrameLayout(Context context)
        {
            super(context);
        }

        public AspectRatioFrameLayout(Context context, AttributeSet attrs)
        {
            super(context, attrs);

        }

        public AspectRatioFrameLayout(Context context, AttributeSet attrs, int defStyle)
        {
            super(context, attrs, defStyle);

        }


        @Override protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
        {
            if(mAspectRatioHeight>0 && mAspectRatioWidth>0) {
                int originalWidth = MeasureSpec.getSize(widthMeasureSpec);

                int originalHeight = MeasureSpec.getSize(heightMeasureSpec);

                int calculatedHeight = originalWidth * mAspectRatioHeight / mAspectRatioWidth;

                int finalWidth, finalHeight;

                if (calculatedHeight > originalHeight) {
                    finalWidth = originalHeight * mAspectRatioWidth / mAspectRatioHeight;
                    finalHeight = originalHeight;
                } else {
                    finalWidth = originalWidth;
                    finalHeight = calculatedHeight;
                }

                super.onMeasure(
                        MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
            }
            else
            {
                super.onMeasure(widthMeasureSpec,heightMeasureSpec);
            }
        }

    public void setAspectRatioWidth(int mAspectRatioWidth) {
        this.mAspectRatioWidth = mAspectRatioWidth;
    }

    public void setAspectRatioHeight(int mAspectRatioHeight) {
        this.mAspectRatioHeight = mAspectRatioHeight;
    }
}
