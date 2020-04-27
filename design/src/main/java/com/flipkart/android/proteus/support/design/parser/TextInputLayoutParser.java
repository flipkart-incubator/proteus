package com.flipkart.android.proteus.support.design.parser;

import android.content.res.ColorStateList;
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
import com.flipkart.android.proteus.support.design.widget.ProteusTextInputLayout;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Created by Prasad Rao on 23-04-2020 17:52
 **/
public class TextInputLayoutParser<V extends TextInputLayout> extends ViewTypeParser<V> {
    @NonNull
    @Override
    public String getType() {
        return "TextInputLayout";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "LinearLayout";
    }

    @NonNull
    @Override
    public ProteusView createView(@NonNull ProteusContext context, @NonNull Layout layout,
        @NonNull ObjectValue data, @Nullable ViewGroup parent, int dataIndex) {
        return new ProteusTextInputLayout(context);
    }

    @Override
    protected void addAttributeProcessors() {

        addAttributeProcessor("boxCornerRadius", new DimensionAttributeProcessor<V>() {
            @Override
            public void setDimension(V view, float dimension) {
                view.setBoxCornerRadii(dimension, dimension, dimension, dimension);
            }
        });

        addAttributeProcessor("boxStrokeColor", new ColorResourceProcessor<V>() {
            @Override
            public void setColor(V view, int color) {
                view.setBoxStrokeColor(color);
            }

            @Override
            public void setColor(V view, ColorStateList colors) {
                throw new IllegalArgumentException("'boxStrokeColor' must be a color!");
            }
        });

        addAttributeProcessor("counterEnabled", new BooleanAttributeProcessor<V>() {
            @Override
            public void setBoolean(V view, boolean value) {
                view.setCounterEnabled(value);
            }
        });

        addAttributeProcessor("counterMaxLength", new NumberAttributeProcessor<V>() {
            @Override
            public void setNumber(V view, @NonNull Number value) {
                view.setCounterMaxLength(value.intValue());
            }
        });

        addAttributeProcessor("counterTextColor", new ColorResourceProcessor<V>() {
            @Override
            public void setColor(V view, int color) {
                throw new IllegalArgumentException("'counterTextColor' must be a color state list");
            }

            @Override
            public void setColor(V view, ColorStateList colors) {
                view.setCounterTextColor(colors);
            }
        });
    }
}
