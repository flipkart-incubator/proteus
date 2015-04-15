package com.flipkart.layoutengine.widgets;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

public class ListViewAdapter extends BaseAdapter {
    private Context context;
    private JsonArray listViewItems;
    private JsonObject listViewItemLayout;

    public ListViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public void add(JsonArray listViewItems) {
    }

    public void add(JSONObject listViewItem) {
    }

    public void clear() {
    }

    public boolean setListViewItemLayout(JsonObject listViewItemLayout) {
        this.listViewItemLayout = listViewItemLayout;
        return this.isReady();
    }

    public boolean setListViewItems(JsonArray listViewItems) {
        this.listViewItems = listViewItems;
        return this.isReady();
    }

    public boolean isReady() {
        return this.listViewItemLayout != null && this.getCount() > 0;
    }
}
