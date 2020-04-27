package com.flipkart.android.proteus.support.v7.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.flipkart.android.proteus.DataContext;
import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusLayoutInflater;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.flipkart.android.proteus.value.Value;

import java.util.Map;
import java.util.Objects;

/**
 * Created by Prasad Rao on 23-04-2020 17:25
 **/
public class SingleSelectionListAdapter extends ProteusRecyclerViewAdapter<ProteusViewHolder> {

    private static final String ATTRIBUTE_ITEM_LAYOUT = "item-layout";
    private static final String ATTRIBUTE_ITEM_COUNT = "item-count";
    private static final String ATTRIBUTE_ITEMS = "items";

    private int checkedPosition = -1;

    public static final Builder<SingleSelectionListAdapter> BUILDER = (view, config) -> {
        Layout layout = config.getAsObject().getAsLayout(ATTRIBUTE_ITEM_LAYOUT);
        Integer count = config.getAsObject().getAsInteger(ATTRIBUTE_ITEM_COUNT);
        ObjectValue data = view.getViewManager().getDataContext().getData();
        ProteusContext context = (ProteusContext) view.getContext();

        return new SingleSelectionListAdapter(context.getInflater(), data,
            Objects.requireNonNull(layout),
            count != null ? count : 0);
    };

    private ProteusLayoutInflater inflater;

    private ObjectValue data;
    private int count;
    private Layout layout;
    private Map<String, Value> scope;

    private SingleSelectionListAdapter(ProteusLayoutInflater inflater, ObjectValue data,
        Layout layout, int count) {
        this.inflater = inflater;
        this.data = data;
        this.count = count;
        this.layout = new Layout(layout.type, layout.attributes, null, layout.extras);
        this.scope = layout.data;
    }

    @NonNull
    @Override
    public ProteusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        ProteusView view = inflater.inflate(layout, new ObjectValue());
        return new ProteusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProteusViewHolder holder, int position) {
        DataContext context = DataContext.create(holder.context, data, position, scope);
        holder.view.getViewManager().update(context.getData());
        holder.view.getAsView().setSelected(checkedPosition == position);
        holder.view.getAsView().setOnClickListener(v -> {
            if (checkedPosition != position) {
                notifyDataSetChanged();
                checkedPosition = position;
                System.out.println("============> "+getSelectedItem());
            }
        });
    }

    @Override
    public int getItemCount() {
        return count;
    }

    public String getSelectedItem() {
        return data.get(ATTRIBUTE_ITEMS).getAsArray().get(checkedPosition).getAsString();
    }
}
