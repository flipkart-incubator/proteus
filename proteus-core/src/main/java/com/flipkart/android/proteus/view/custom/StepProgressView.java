package com.flipkart.android.proteus.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prasad Rao on 02-03-2020 18:17
 **/
public class StepProgressView extends View {

    private int totalProgress = 100;
    private List<Integer> markers = new ArrayList<>();
    private int currentProgress = 30;
    private float markerWidth = pxValue(1f);
    private float rectRadius = pxValue(3f);
    private float textMargin = pxValue(10f);
    private float progressBarHeight = pxValue(15f);
    private float markerTextSize = pxValue(12f);
    private int markerColor = Color.WHITE;
    private int progressColor = Color.GREEN;
    private int progressBackgroundColor = Color.parseColor("#334d6cd9");
    private int markerTextColor = Color.BLACK;
    private Paint paintBackground;
    private Paint paintMarkers;
    private Paint paintProgress;
    private TextPaint paintText;
    private RectF rBar = new RectF();
    private Path rectRoundPath = new Path();
    private Path drawingPath = new Path();
    private RectF arcRect = new RectF();
    private int textHeight = 0;
    private float textVerticalCenter = 0f;
    private float extraWidthLeftText = 0f;

    public StepProgressView(Context context) {
        super(context);
        initialize();
    }

    public StepProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public StepProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        this.paintText = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        this.paintText.setColor(markerTextColor);
        this.paintText.setStyle(Paint.Style.FILL);
        this.paintText.setTextSize(markerTextSize);
        this.paintText.setTextAlign(Paint.Align.CENTER);
        this.paintText.setTypeface(Typeface.DEFAULT);

        this.paintMarkers = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paintMarkers.setColor(markerColor);

        this.paintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paintProgress.setColor(progressColor);

        this.paintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paintBackground.setColor(progressBackgroundColor);
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
        invalidate();
    }

    public void setTotalProgress(int totalProgress) {
        this.totalProgress = totalProgress;
        invalidate();
    }

    public void setTextMargin(float textMargin) {
        this.textMargin = textMargin;
        invalidate();
    }

    public void setMarkerWidth(float markerWidth) {
        this.markerWidth = markerWidth;
        requestLayout();
    }

    public void setMarkerTextSize(float markerTextSize) {
        this.markerTextSize = markerTextSize;
        requestLayout();
    }

    public void setProgressBackgroundColor(int progressBackgroundColor) {
        this.progressBackgroundColor = progressBackgroundColor;
        invalidate();
    }

    public void setMarkerColor(int markerColor) {
        this.markerColor = markerColor;
        invalidate();
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        this.paintProgress.setColor(progressColor);
        invalidate();
    }

    public void setMarkerTextColor(int markerTextColor) {
        this.markerTextColor = markerTextColor;
        this.paintText.setColor(markerTextColor);
        invalidate();
    }

    public void setTextFont(Typeface font) {
        this.paintText.setTypeface(font);
        invalidate();
    }

    public void setEdgeRadius(float radius) {
        this.rectRadius = radius;
        invalidate();
    }

    public void setMarkers(String markers) {
        this.markers.clear();

        String[] input = markers.split(",");

        for (String marker : input) {
            int step = Integer.parseInt(marker);
            if (step >= 1 && step < totalProgress) {
                this.markers.add(step);
            }
        }
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawingPath.reset();

        if (1 <= currentProgress && currentProgress < totalProgress) {

            float progressX = (currentProgress / (float) totalProgress) * (rBar.right - rBar.left);

            if (progressX > rectRadius) { //if progress exceeds beyond left corner avoid redrawing

                //progressX-1 is used so that there is no gap between progressRect drawing and
                // backgroundRect Drawing
                drawingPath.addPath(drawRoundedRightRect((progressX - 1),
                    rBar.top,
                    rBar.right,
                    rBar.bottom,
                    rectRadius,
                    paintBackground,
                    canvas
                ));

                float progressRight;
                boolean drawProgressInRightCorner = progressX > (rBar.right - rectRadius);

                if (drawProgressInRightCorner) {
                    progressRight = rBar.right - rectRadius;
                } else {
                    progressRight = progressX;

                }

                drawingPath.addPath(drawRoundedLeftRect(rBar.left,
                    rBar.top,
                    progressRight,
                    rBar.bottom,
                    rectRadius,
                    paintProgress,
                    canvas
                ));

                canvas.save();
                canvas.clipPath(drawingPath);

                if (drawProgressInRightCorner) {
                    canvas.drawRect((rBar.right - rectRadius), rBar.top, progressX, rBar.bottom, paintProgress);
                }
            } else {
                drawCompleteProgressBar(canvas, paintBackground);
                canvas.drawRect(rBar.left, rBar.top, progressX, rBar.bottom, paintProgress);
            }
        } else {
            //incase there is no progress only draw background progress bar
            final Paint paint;
            if (currentProgress > 0) { paint = paintProgress; } else {paint = paintBackground; }

            drawCompleteProgressBar(canvas, paint);

        }

        for (Integer marker : markers) {
            if (marker >= 1 && marker <= totalProgress) {
                float left = (marker / (float) totalProgress) * (rBar.right - rBar.left) + extraWidthLeftText;
                canvas.drawRect(left - markerWidth / 2, rBar.top, left + markerWidth / 2, rBar.bottom, paintMarkers);
            }
        }

        canvas.restore();

        // using one more for loop instead of saving & restoring canvas for text since,
        // that would be more precious
        for (Integer marker : markers) {
            if (marker >= 1 && marker <= totalProgress) {
                float left = (marker / (float) totalProgress) * (rBar.right - rBar.left) + extraWidthLeftText;
                canvas.drawText(marker.toString(), left, textVerticalCenter, paintText);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        rBar.left = extraWidthLeftText;
        rBar.top = 0f;
        rBar.right = getMeasuredWidth();
        rBar.bottom = progressBarHeight;
        textVerticalCenter = (progressBarHeight + textMargin) + textHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        System.out.println("=========== " + getMeasuredWidth());
        int desiredWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int desiredHeight = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();
        System.out.println("======= " + desiredWidth + " " + desiredHeight + " =======");
        setMeasuredDimension(measureDimension(desiredWidth, widthMeasureSpec),
            measureDimension(desiredHeight, heightMeasureSpec)
        );
    }

    private int measureDimension(int desiredSize, int measureSpec) {
        System.out.println("desiredSize = " + desiredSize + ", measureSpec = " + measureSpec);
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = desiredSize;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        if (result < desiredSize) {
            Log.e("ChartView", "The view is too small, the content might get cut");
        }
        return result;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return (int) Math.ceil(progressBarHeight + setTextHeight());
    }

    private float setTextHeight() {
        if (markers.size() == 0) return 0F;

        Rect rect = new Rect();
        String text = "8";
        paintText.getTextBounds(text, 0, text.length(), rect);
        textHeight = rect.height();
        return textHeight + textMargin;
    }

    private void drawCompleteProgressBar(Canvas canvas, Paint paint) {
        drawingPath.addRoundRect(rBar, rectRadius, rectRadius, Path.Direction.CW);
        canvas.drawPath(drawingPath, paint);
        canvas.save();
        canvas.clipPath(drawingPath);
    }

    private Path drawRoundedLeftRect(float left, float top, float right, float bottom, float cornerRadius, Paint paint,
        Canvas canvas) {
        rectRoundPath.reset();

        arcRect.left = left;
        arcRect.top = top;
        arcRect.right = left + (2 * cornerRadius);
        arcRect.bottom = bottom;

        rectRoundPath.addArc(arcRect, 90F, 180F);

        rectRoundPath.addRect(left + cornerRadius, top, right, bottom, Path.Direction.CW);

        canvas.drawPath(rectRoundPath, paint);
        return rectRoundPath;
    }

    private Path drawRoundedRightRect(float left, float top, float right, float bottom, float cornerRadius, Paint paint,
        Canvas canvas) {
        arcRect.left = right - (2 * cornerRadius);
        arcRect.top = top;
        arcRect.right = right;
        arcRect.bottom = bottom;

        rectRoundPath.reset();
        if (right - left > cornerRadius) {
            rectRoundPath.addRect(left, top, right - cornerRadius, bottom, Path.Direction.CW);

        }
        rectRoundPath.addArc(arcRect, -90F, 180F);
        canvas.drawPath(rectRoundPath, paint);
        return rectRoundPath;
    }

    private float pxValue(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}
