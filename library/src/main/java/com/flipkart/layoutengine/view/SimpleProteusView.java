package com.flipkart.layoutengine.view;

import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.toolbox.Styles;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ProteusView} implementation for simple views associated with a
 * {@link android.view.View} built using a
 * {@link com.flipkart.layoutengine.builder.LayoutBuilder}.
 *
 * @author Aditya Sharat {@literal <aditya.sharat@flipkart.com>}
 */
public class SimpleProteusView implements ProteusView {

    protected ProteusView parent;
    protected JsonObject layout;
    protected View view;
    protected int index;
    protected List<ProteusView> children;
    protected Styles styles;

    public SimpleProteusView(View view, int index, ProteusView parent) {
        this.view = view;
        this.index = index;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public SimpleProteusView(View view, JsonObject layout, int index, List<ProteusView> children, ProteusView parent) {
        this.view = view;
        this.layout = layout;
        this.index = index;
        this.parent = parent;
        if (children == null) {
            children = new ArrayList<>();
        }
        this.children = children;
    }

    public SimpleProteusView(View view, JsonObject layout, int index, ProteusView parent) {
        this.view = view;
        this.layout = layout;
        this.index = index;
        this.parent = parent;
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
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public ProteusView getParent() {
        return this.parent;
    }

    @Override
    public void addView(ProteusView child) {
        if (child == null) {
            return;
        }
        if (view != null && child.getView() != null) {
            unsetParent(child.getView());
            this.children.add(child);
            ((ViewGroup) view).addView(child.getView());
        }
    }

    @Override
    public List<ProteusView> getChildren() {
        return this.children;
    }

    @Override
    public View updateData(JsonObject data) {
        return this.view;
    }

    @Override
    public void replaceView(ProteusView view) {
        this.children = view.getChildren();
        this.layout = view.getLayout();
        this.styles = view.getStyles();
        ViewGroup parent = (ViewGroup) this.view.getParent();
        if (parent != null) {
            int index = parent.indexOfChild(this.view);
            parent.removeView(this.view);
            unsetParent(view.getView());
            parent.addView(view.getView(), index);
        }
    }

    @Override
    public void removeView() {
        destroy();
    }

    @Override
    public ProteusView removeView(int childIndex) {
        if (childIndex < children.size()) {
            ProteusView proteusView = getChildren().remove(childIndex);
            unsetParent(proteusView.getView());
            return proteusView;
        }
        return null;
    }

    @Override
    public JsonObject getLayout() {
        return layout;
    }

    @Override
    public void setStyles(Styles styles) {
        this.styles = styles;
    }

    @Override
    public Styles getStyles() {
        return styles;
    }

    @Override
    public void destroy() {
        this.view = null;
        this.children = null;
        this.layout = null;
        this.styles = null;
        this.parent = null;
    }

    protected void unsetParent(View child) {
        if (child.getParent() != null) {
            ((ViewGroup) child.getParent()).removeView(child);
        }
    }
}
