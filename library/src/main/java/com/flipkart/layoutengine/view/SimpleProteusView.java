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
    protected ArrayList<ProteusView> children;

    public SimpleProteusView(View view, int index, ProteusView parent) {
        this.view = view;
        this.index = index;
        this.parent = parent;
    }

    public SimpleProteusView(View view, int index, ArrayList<ProteusView> children, ProteusView parent) {
        this.view = view;
        this.index = index;
        this.parent = parent;
        this.children = children;
    }

    @Override
    public View getView() {
        return this.view;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public ProteusView getParent() {
        return this.parent;
    }

    @Override
    public void addChild(ProteusView proteusView) {
        if (proteusView == null) {
            return;
        }
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(proteusView);
        if (view != null && proteusView.getView() != null) {
            ((ViewGroup) view).addView(proteusView.getView());
        }
    }

    @Override
    public ArrayList<ProteusView> getChildren() {
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
        if (parent != null) {
            int index = parent.indexOfChild(this.view);
            parent.removeView(this.view);
            parent.addView(view.getView(), index);
        }
    }

    @Override
    public void removeView() {
        if (getParent() != null && getParent().getView() != null && view != null) {
            ((ViewGroup) getParent().getView()).removeView(view);
        }
    }

    @Override
    public void removeChild(int childIndex) {
        if (children != null && childIndex < children.size()) {
            ProteusView proteusView = getChildren().get(childIndex);
            proteusView.removeView();
            getChildren().remove(childIndex);
        }
    }

    protected View updateDataImpl(JsonObject data) {
        return this.view;
    }
}
