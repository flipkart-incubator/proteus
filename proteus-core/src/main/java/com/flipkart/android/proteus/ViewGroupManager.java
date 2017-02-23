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

package com.flipkart.android.proteus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.value.Layout;
import com.google.gson.JsonObject;

/**
 * ViewGroupManager
 *
 * @author adityasharat
 */

public class ViewGroupManager extends ViewManager {

    public ViewGroupManager(@NonNull ProteusContext context, @NonNull ViewTypeParser parser,
                            @NonNull View view, @NonNull Layout layout, @NonNull Scope scope) {
        super(context, parser, view, layout, scope);
    }

    @Override
    public void update(@Nullable JsonObject data) {
        super.update(data);
        // update the child views
        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            int count = parent.getChildCount();
            View child;

            for (int index = 0; index < count; index++) {
                child = parent.getChildAt(index);
                if (child instanceof ProteusView) {
                    ((ProteusView) child).getViewManager().update(scope.getData());
                }
            }
        }
    }
}
