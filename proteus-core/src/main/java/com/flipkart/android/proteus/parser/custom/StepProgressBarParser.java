package com.flipkart.android.proteus.parser.custom;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.processor.BooleanAttributeProcessor;
import com.flipkart.android.proteus.processor.ColorResourceProcessor;
import com.flipkart.android.proteus.processor.DimensionAttributeProcessor;
import com.flipkart.android.proteus.processor.NumberAttributeProcessor;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.flipkart.android.proteus.view.custom.StepProgressBar;
import com.flipkart.android.proteus.view.custom.StepProgressView;

/**
 * Created by Prasad Rao on 02-03-2020 19:27
 **/
public class StepProgressBarParser<T extends StepProgressView> extends ViewTypeParser<T> {
    @NonNull
    @Override
    public String getType() {
        return "StepProgressView";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "View";
    }

    @NonNull
    @Override
    public ProteusView createView(@NonNull ProteusContext context, @NonNull Layout layout, @NonNull ObjectValue data,
        @Nullable ViewGroup parent, int dataIndex) {
        final ViewGroup.LayoutParams lp =
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        StepProgressBar stepProgressBar = new StepProgressBar(context);
        stepProgressBar.setLayoutParams(lp);
        return stepProgressBar;
    }

    @Override
    protected void addAttributeProcessors() {
        addAttributeProcessor("markers", new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                view.setMarkers(value);
            }
        });

        addAttributeProcessor("textFont", new StringAttributeProcessor<T>() {
            @Override
            public void setString(T view, String value) {
                Typeface typeface;
                typeface = Typeface.createFromAsset(view.getContext().getAssets(), value);
                if (typeface != null) {
                    view.setTextFont(typeface);
                }
            }
        });

        addAttributeProcessor("currentProgress", new NumberAttributeProcessor<T>() {
            @Override
            public void setNumber(T view, @NonNull Number value) {
                view.setCurrentProgress(value.intValue());
            }
        });

        addAttributeProcessor("totalProgress", new NumberAttributeProcessor<T>() {
            @Override
            public void setNumber(T view, @NonNull Number value) {
                view.setTotalProgress(value.intValue());
            }
        });

        addAttributeProcessor("progressColor", new ColorResourceProcessor<T>() {
            @Override
            public void setColor(T view, int color) {
                view.setProgressColor(color);
            }

            @Override
            public void setColor(T view, ColorStateList colors) {
                view.setProgressColor(colors.getDefaultColor());
            }
        });

        addAttributeProcessor("markerWidth", new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                view.setMarkerWidth(dimension);
            }
        });

        addAttributeProcessor("textMargin", new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                view.setTextMargin(dimension);
            }
        });

        addAttributeProcessor("markerTextSize", new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                view.setMarkerTextSize(dimension);
            }
        });

        addAttributeProcessor("progressBackgroundColor", new ColorResourceProcessor<T>() {
            @Override
            public void setColor(T view, int color) {
                view.setProgressBackgroundColor(color);
            }

            @Override
            public void setColor(T view, ColorStateList colors) {
                view.setProgressBackgroundColor(colors.getDefaultColor());
            }
        });

        addAttributeProcessor("markerTextColor", new ColorResourceProcessor<T>() {
            @Override
            public void setColor(T view, int color) {
                view.setMarkerTextColor(color);
            }

            @Override
            public void setColor(T view, ColorStateList colors) {
                view.setMarkerTextColor(colors.getDefaultColor());
            }
        });

        addAttributeProcessor("markerColor", new ColorResourceProcessor<T>() {
            @Override
            public void setColor(T view, int color) {
                view.setMarkerColor(color);
            }

            @Override
            public void setColor(T view, ColorStateList colors) {
                view.setMarkerColor(colors.getDefaultColor());
            }
        });

        addAttributeProcessor("edgeRadius", new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                view.setEdgeRadius(dimension);
            }
        });

        addAttributeProcessor("progressBarHeight", new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(T view, float dimension) {
                view.setProgressBarHeight(dimension);
            }
        });

        addAttributeProcessor("showMarkerText", new BooleanAttributeProcessor<T>() {
            @Override
            public void setBoolean(T view, boolean value) {
                view.setShowMarkerText(value);
            }
        });

        addAttributeProcessor("stepSize", new NumberAttributeProcessor<T>() {
            @Override
            public void setNumber(T view, @NonNull Number value) {
                view.addMarkersWithStepSize(value.intValue());
            }
        });
    }
}
