package com.flipkart.layoutengine.view;

import android.view.View;

import com.flipkart.layoutengine.binding.Binding;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * A {@link ProteusView} implementation for simple views associated with a
 * {@link android.view.View} built using a
 * {@link com.flipkart.layoutengine.builder.LayoutBuilder}.
 *
 * @author Aditya Sharat {@literal <aditya.sharat@flipkart.com>}
 */
public class SimpleProteusView implements ProteusView {
    //private static final String TAG = SimpleProteusView.class.getSimpleName();
    protected View view;

    public SimpleProteusView(View view) {
        this.view = view;
    }

    @Override
    public View getView() {
        return this.view;
    }

    @Override
    public ArrayList<Binding> getBindings() {
        return null;
    }

    @Override
    public View updateView(JsonObject data) {
        return updateViewImpl(data);
    }

    protected View updateViewImpl(JsonObject data) {
        return this.view;
    }
}
