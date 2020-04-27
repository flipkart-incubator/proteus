package com.flipkart.android.proteus.support.design.parser;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.support.design.widget.ProteusAppCompatEditText;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;

/**
 * Created by Prasad Rao on 27-04-2020 10:58
 **/
public class AppCompatEditTextParser<V extends AppCompatEditText> extends ViewTypeParser<V> {
    @NonNull
    @Override
    public String getType() {
        return "AppCompatEditText";
    }

    @Nullable
    @Override
    public String getParentType() {
        return "EditText";
    }

    @NonNull
    @Override
    public ProteusView createView(@NonNull ProteusContext context, @NonNull Layout layout,
        @NonNull ObjectValue data, @Nullable ViewGroup parent, int dataIndex) {
        return new ProteusAppCompatEditText(context);
    }

    @Override
    protected void addAttributeProcessors() {

    }
}
