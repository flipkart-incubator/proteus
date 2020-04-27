package com.flipkart.android.proteus.support.design.parser;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.support.design.widget.ProteusTextInputEditText;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Created by Prasad Rao on 27-04-2020 10:54
 **/
public class TextInputEditTextParser<V extends TextInputEditText> extends ViewTypeParser<V> {
    @NonNull
    @Override
    public String getType() {
        return "TextInputEditText";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "AppCompatEditText";
    }

    @NonNull
    @Override
    public ProteusView createView(@NonNull ProteusContext context, @NonNull Layout layout,
        @NonNull ObjectValue data, @Nullable ViewGroup parent, int dataIndex) {
        return new ProteusTextInputEditText(context);
    }

    @Override
    protected void addAttributeProcessors() {

    }
}
