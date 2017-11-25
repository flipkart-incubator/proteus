/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 *
 * Copyright (c) 2017 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.flipkart.android.proteus.support.v7.layoutmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;

import com.flipkart.android.proteus.support.v7.widget.ProteusRecyclerView;
import com.flipkart.android.proteus.value.ObjectValue;

/**
 * @author adityasharat
 */
public class ProteusLinearLayoutManager extends LinearLayoutManager {

    private static final String ATTRIBUTE_ORIENTATION = "orientation";
    private static final String ATTRIBUTE_REVERSE_LAYOUT = "reverse";

    public static final LayoutManagerBuilder<ProteusLinearLayoutManager> BUILDER = new LayoutManagerBuilder<ProteusLinearLayoutManager>() {

        @NonNull
        @Override
        public ProteusLinearLayoutManager create(@NonNull ProteusRecyclerView view, @NonNull ObjectValue config) {

            int orientation = config.getAsInteger(ATTRIBUTE_ORIENTATION, LinearLayoutManager.VERTICAL);
            boolean reverseLayout = config.getAsBoolean(ATTRIBUTE_REVERSE_LAYOUT, false);

            return new ProteusLinearLayoutManager(view.getContext(), orientation, reverseLayout);
        }
    };

    public ProteusLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }
}
