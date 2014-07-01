package com.flipkart.layoutengine.datasource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by kirankumar on 24/06/14.
 */
public class GsonDataSource implements DataSource {


    private final JsonElement obj;

    public GsonDataSource(JsonElement jsonElement) {
        this.obj = jsonElement;
    }


    @Override
    public JsonElement getObject(String key) {
        return getFromObject(key);
    }

    private JsonElement getFromObject(String path) {
        JsonElement jObj = this.obj;
        String[] split = path.split("\\.");
        JsonElement el = null;

        for (String e : split) {
            char lastChar = e.charAt(e.length()-1);

            if(']'==lastChar){
                while (']'==lastChar) {
                    String index = e.substring(e.lastIndexOf('[')+1, e.length()-1);
                    Integer iindex = Integer.valueOf(index);
                    e = e.substring(0,e.lastIndexOf('['));
                    if("".equals(e)){
                        if(el==null){
                            el = jObj.getAsJsonArray().get(iindex);
                            lastChar = (char)0;
                        } else {
                            el = el.getAsJsonArray().get(iindex);
                            lastChar = (char)0;
                        }
                    } else {
                        lastChar = e.charAt(e.length()-1); // new last char

                        // if next is object
                        if(lastChar!=']'){
                            if(el==null){
                                el = jObj.getAsJsonObject().get(e).getAsJsonArray().get(iindex);
                            } else {
                                if(el.isJsonObject()){
                                    el = el.getAsJsonObject().get(e).getAsJsonArray().get(iindex);
                                } else if(el.isJsonArray()){
                                    el = el.getAsJsonArray().get(iindex);
                                }
                            }
                        } else { // next is array
                            if(el==null){
                                if(jObj.isJsonObject()){
                                    String locale = e.substring(0,e.indexOf('['));
                                    el = jObj.getAsJsonObject().get(locale).getAsJsonArray().get(iindex);
                                } else if(jObj.isJsonArray()){
                                    el = jObj.getAsJsonArray().get(iindex);
                                }
                            } else {
                                if(e.indexOf('[')>-1){
                                    String locale = e.substring(0,e.indexOf('['));
                                    el = el.getAsJsonObject().get(locale).getAsJsonArray().get(iindex);
                                } else {
                                    el = el.getAsJsonArray().get(iindex);
                                };

                            }
                        }
                    }
                }
            } else{
                // plain obj
                if(el==null){
                    el = jObj.getAsJsonObject().get(e);
                } else {
                    el = el.getAsJsonObject().get(e);
                }

            }

        }
        return el;
    }


}
