package com.flipkart.android.proteus.demo.models;

import com.flipkart.android.proteus.toolbox.Styles;
import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * JsonResource
 *
 * @author aditya.sharat
 */

public interface JsonResource {
    @GET("{path}")
    Call<JsonObject> get(@Path("path") String path);

    @GET("styles.json")
    Call<Styles> getStyles();

    @GET("layouts.json")
    Call<Map<String, JsonObject>> getLayouts();
}