package com.flipkart.layoutengine.toolbox;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Styles
 *
 * @author Aditya Sharat
 */
public class Styles extends HashMap<String, Map<String, JsonElement>> {

    public Map<String, JsonElement> getStyle(String name) {
        return this.get(name);
    }

    public boolean contains(String name) {
        return this.containsKey(name);
    }
}
