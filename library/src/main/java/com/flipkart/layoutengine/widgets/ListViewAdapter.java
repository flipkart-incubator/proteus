package com.flipkart.layoutengine.widgets;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.flipkart.layoutengine.EventType;
import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.builder.DefaultLayoutBuilderFactory;
import com.flipkart.layoutengine.builder.LayoutBuilder;
import com.flipkart.layoutengine.builder.LayoutBuilderCallback;
import com.flipkart.layoutengine.provider.DataParsingAdapter;
import com.flipkart.layoutengine.provider.GsonProvider;
import com.flipkart.layoutengine.toolbox.Utils;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class ListViewAdapter extends BaseAdapter {

    private static final Character PREFIX = DataParsingAdapter.PREFIX;
    private String dataContext;
    private Context context;
    private JsonArray listViewItems;
    private JsonObject listViewItemLayout;
    private Map<View, ProteusView> viewMap = new HashMap<View, ProteusView>();

    public ListViewAdapter(Context context, ParserContext parserContext, JsonObject listViewItemLayout, JsonElement dataContext) {
        this.context = context;
        this.listViewItemLayout = listViewItemLayout;
        this.listViewItems = this.getListViewItemsFromData(parserContext, dataContext);
        this.dataContext = dataContext.getAsString().substring(1);
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

        JsonElement item = this.getItem(position);
        JsonObject itemDataPlaceHolder = new JsonObject();
        itemDataPlaceHolder.add(this.dataContext, item);

        if (convertView != null) {
            ProteusView savedProteusView = this.viewMap.get(convertView);
            savedProteusView.updateView(itemDataPlaceHolder);
        } else {
            LayoutBuilder layoutBuilder = new DefaultLayoutBuilderFactory()
                    .createDataAndViewParsingLayoutBuilder(this.context,
                            new GsonProvider(itemDataPlaceHolder),
                            new GsonProvider(this.listViewItemLayout));

            layoutBuilder.setListener(new LayoutBuilderCallback() {
                @Override
                public void onUnknownAttribute(ParserContext context, String attribute, JsonElement element, JsonObject object, View view, int childIndex) {
                    Log.d("asd", "asdasd");
                }

                @Override
                public ProteusView onUnknownViewType(ParserContext context, String viewType, JsonObject object, ViewGroup parent, int childIndex) {
                    return null;
                }

                @Override
                public View onEvent(ParserContext context, View view, JsonElement attributeValue, EventType eventType) {
                    return null;
                }
            });

            ProteusView view = layoutBuilder.build(parent, this.listViewItemLayout);
            convertView = view.getView();

            this.viewMap.put(convertView, view);
        }

        return convertView;
    }

    private JsonArray getListViewItemsFromData(ParserContext parserContext, JsonElement dataContext) {
        return Utils.getElementFromData(PREFIX, dataContext, parserContext.getDataProvider(), 0).getAsJsonArray();
    }
}
