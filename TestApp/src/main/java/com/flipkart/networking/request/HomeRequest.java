package com.flipkart.networking.request;


import com.flipkart.config.Config;
import com.flipkart.networking.response.HomeResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.json.JSONObject;

import java.lang.reflect.Type;

public class HomeRequest extends BaseRequest<HomeResponse>{

	public HomeRequest() {
		super(Config.instance.get(Config.StringKey.ApiBaseUrl), Config.instance.get(Config.ApiPathKey.HomeRequest));
	}

	@Override
	public Class<HomeResponse> getResponseClass() {
		return HomeResponse.class;
	}

    @Override
    protected Gson createParser() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.registerTypeAdapter(JSONObject.class, new JSONObjectDeserializer());

        return gsonBuilder.create();
    }

}
     
