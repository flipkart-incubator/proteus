package com.flipkart.layoutengine.widgets;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.provider.DataParsingAdapter;
import com.flipkart.layoutengine.toolbox.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ListViewAdapter extends BaseAdapter {

    private static final Character PREFIX = DataParsingAdapter.PREFIX;
    private Context context;
    private JsonArray listViewItems;
    private JsonObject listViewItemLayout;

    public ListViewAdapter(Context context,ParserContext parserContext, JsonObject listViewItemLayout, JsonElement dataContext) {
        this.context = context;
        this.listViewItemLayout = listViewItemLayout;
        this.listViewItems = this.getListViewItemsFromData(parserContext, dataContext);
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

    private JsonArray getListViewItemsFromData(ParserContext parserContext, JsonElement dataContext) {
        return Utils.getElementFromData(PREFIX, dataContext, parserContext.getDataProvider(), 0).getAsJsonArray();
    }
}
