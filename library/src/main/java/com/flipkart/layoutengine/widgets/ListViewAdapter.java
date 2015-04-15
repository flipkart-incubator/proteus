package com.flipkart.layoutengine.widgets;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ListViewAdapter extends BaseAdapter {
    private Context context;
    private JsonArray listViewItems;
    private JsonObject listViewItemLayout;

    public ListViewAdapter(Context context, JsonObject listViewItemLayout, JsonArray listViewItems) {
        this.context = context;
        this.listViewItemLayout = listViewItemLayout;
        this.listViewItems = listViewItems;
    }

    @Override
    public int getCount() {
        return this.listViewItems.size();
    }

    @Override
    public JsonElement getItem(int position) {
        return this.listViewItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

}
