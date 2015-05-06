package com.flipkart.layoutengine.toolbox;

import android.util.Log;

import com.flipkart.layoutengine.provider.Provider;
import com.google.gson.JsonElement;

/**
 * Created by Aditya Sharat on 08-04-2015.
 */
public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static JsonElement getElementFromData(Character prefix, JsonElement element,
                                                 Provider dataProvider, int childIndex) {
        if (element.isJsonPrimitive()) {
            String dataSourceKey = element.getAsString();
            if (dataSourceKey.length() > 0 && dataSourceKey.charAt(0) == prefix) {
                JsonElement tempElement = dataProvider.getObject(dataSourceKey.substring(1), childIndex);
                if (tempElement != null) {
                    element = tempElement;
                } else {
                    Log.e(TAG, "Got null element for " + dataSourceKey);
                }
            }
        }
        return element;
    }
}
