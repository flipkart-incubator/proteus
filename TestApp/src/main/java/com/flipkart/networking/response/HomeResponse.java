package com.flipkart.networking.response;


import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;


import java.util.List;

public class HomeResponse extends BaseResponse {

    @SerializedName("RESPONSE")
    private HomeResponseCore response;

    public HomeResponseCore getResponse() {
        return response;
    }


    public void setResponse(HomeResponseCore response) {
        this.response = response;
    }

    public static class HomeResponseCore {
        private JsonObject layout;
        private JsonObject data;

        public JsonObject getViews() {
            return views;
        }

        private JsonObject views;

        public JsonObject getLayout() {
            return layout;
        }

        public JsonObject getData() {
            return data;
        }
    }


}
