package com.flipkart.android.proteus.processor;

import android.content.res.Resources;
import android.view.View;

import com.flipkart.android.proteus.parser.Attributes;
import com.flipkart.android.proteus.toolbox.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by mac on 3/27/17.
 */

public abstract class StyleProcessor<V extends View> extends AttributeProcessor<V> {
    @Override
    public void handle(String key, JsonElement value, V view) {
        if (!value.isJsonObject() || value.isJsonNull()) {
            return;
        }
        JsonObject data = value.getAsJsonObject();
        String resourceIdString = Utils.getPropertyAsString(data, key);
        Resources r = view.getContext().getResources();
        int resourceId = -1;
        resourceId = r.getIdentifier(resourceIdString, "style", view.getContext().getPackageName());
        if (resourceId != -1) {
            setResource(resourceId, view);
        }
    }

    public abstract void setResource(int resourceID, View view);
}
