package com.flipkart.networking.response;


import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class RemoteRenderingResponse extends BaseResponse {

    @SerializedName("RESPONSE")
    private RemoteRenderingResponseCore response;

    public RemoteRenderingResponseCore getResponse() {
        return response;
    }


    public void setResponse(RemoteRenderingResponseCore response) {
        this.response = response;
    }

    public static class RemoteRenderingResponseCore {
        private JsonObject layout;
        private int id;

        public JsonObject getLayout() {
            return layout;
        }

        public int getId() {
            return id;
        }
    }


}
