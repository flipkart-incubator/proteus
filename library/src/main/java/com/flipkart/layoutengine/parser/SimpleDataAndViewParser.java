package com.flipkart.layoutengine.parser;

import android.view.View;

import org.json.JSONObject;

/**
 * A DataAndViewParser which
 * Created by Aditya Sharat on 02-04-2015.
 */
public class SimpleDataAndViewParser implements DataAndViewParser {
    @Override
    public View getView() {
        return null;
    }

    @Override
    public View updateView(JSONObject data) {
        return null;
    }
}
