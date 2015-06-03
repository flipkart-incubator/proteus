package com.flipkart.layoutengine.view;

import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * A {@link ProteusView} implementation for simple views associated with a
 * {@link android.view.View} built using a
 * {@link com.flipkart.layoutengine.builder.LayoutBuilder}.
 *
 * @author Aditya Sharat {@literal <aditya.sharat@flipkart.com>}
 */
public class SimpleProteusView implements ProteusView {
    protected ProteusView parent;
    protected View view;
    protected int index;
    protected ArrayList<ProteusView<View>> children;

    public SimpleProteusView(View view, int index, ProteusView parent) {
        this.view = view;
        this.index = index;
        this.parent = parent;
    }

    @Override
    public View getView() {
        return this.view;
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public ProteusView getParent() {
        return this.parent;
    }

    @Override
    public void addChild(ProteusView view) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(view);
        ((ViewGroup) getView()).addView(view.getView());
    }

    @Override
    public ArrayList<ProteusView<View>> getChildren() {
        return this.children;
    }

    @Override
    public View updateData(JsonObject data) {
        return updateDataImpl(data);
    }

    @Override
    public void replaceView(ProteusView view) {
        this.children = view.getChildren();
        ViewGroup parent = (ViewGroup) this.view.getParent();
        int index = parent.indexOfChild(this.view);
        parent.removeView(this.view);
        parent.addView(view.getView(), index);
    }

    protected View updateDataImpl(JsonObject data) {
        return this.view;
    }
}
