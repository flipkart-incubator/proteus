package com.flipkart.android.proteus.support.v7.layoutmanager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.flipkart.android.proteus.support.v7.widget.ProteusRecyclerView;
import com.flipkart.android.proteus.value.ObjectValue;

public class ProteusGridLayoutManager extends GridLayoutManager {


    private static final String ATTRIBUTE_COL = "numCols";
    private static final String ATTRIBUTE_ORIENTATION = "orientation";
    private static final String ATTRIBUTE_REVERSE_LAYOUT = "reverse";


    public static final LayoutManagerBuilder<ProteusGridLayoutManager> BUILDER = new LayoutManagerBuilder<ProteusGridLayoutManager>() {

        @NonNull
        @Override
        public ProteusGridLayoutManager create(@NonNull ProteusRecyclerView view, @NonNull ObjectValue config) {

             int orientation = config.getAsInteger(ATTRIBUTE_ORIENTATION, GridLayoutManager.VERTICAL);
             boolean reverseLayout = config.getAsBoolean(ATTRIBUTE_REVERSE_LAYOUT, false);

             //todo get column from config attribute
            int col = config.getAsInteger(ATTRIBUTE_COL, 1);
            

            return new ProteusGridLayoutManager(view.getContext(), col, orientation, reverseLayout);
        }
    };
    
    
    public ProteusGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }





}
