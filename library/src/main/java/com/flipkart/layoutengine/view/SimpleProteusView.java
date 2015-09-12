package com.flipkart.layoutengine.view;

import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.toolbox.Styles;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ProteusView} implementation built using a
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
    }

    public SimpleProteusView(View view, JsonObject layout, int index, List<ProteusView> children, ProteusView parent) {
        this.view = view;
        this.layout = layout;
        this.index = index;
        this.parent = parent;
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
    public ProteusView getParent() {
        return this.parent;
    }

    @Override
    public void unsetParent() {
        this.parent = null;
    }

    @Override
    public void addView(ProteusView child, int index) {
        if (child == null) {
            return;
        }
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        if (index < 0) {
            index = this.children.size();
        }
        this.children.add(index, child);
        if (view != null && child.getView() != null) {
            ((ViewGroup) view).addView(child.getView(), index);
        }
    }

    @Override
    public void addView(ProteusView child) {
        addView(child, -1);
    }

    @Override
    public List<ProteusView> getChildren() {
        return this.children;
    }

    @Override
    public void replaceView(ProteusView child) {
        this.children = child.getChildren();
        this.layout = child.getLayout();
        this.styles = child.getStyles();
        if (parent != null) {
            // remove the parent if the child view already has one
            if (child.getView().getParent() != null) {
                ((ViewGroup) child.getView().getParent()).removeView(child.getView());
            }
            parent.removeView(index).destroy();
            parent.addView(child, index);
        }
    }

    @Override
    public void removeView() {
        destroy();
    }

    @Override
    public ProteusView removeView(int childIndex) {
        ((ViewGroup) view).removeViewAt(childIndex);
        ProteusView child = children.remove(childIndex);
        child.unsetParent();
        return child;
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
    public View updateData(JsonObject data) {
        return updateDataImpl(data);
    }

    protected View updateDataImpl(JsonObject data) {
        return this.view;
    }

    @Override
    public void destroy() {
        if (parent != null && parent.getView() != null && view != null) {
            parent.removeView(index);
            parent = null;
        }
        view = null;
        children = null;
        layout = null;
        styles = null;
    }
}
