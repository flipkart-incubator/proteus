package com.flipkart.networking.response;


import com.google.gson.annotations.SerializedName;

public class BitmapUploadResponse extends BaseResponse {

    @SerializedName("RESPONSE")
    private BitmapUploadResponseCore response;

    public BitmapUploadResponseCore getResponse() {
        return response;
    }


    public void setResponse(BitmapUploadResponseCore response) {
        this.response = response;
    }

    public static class BitmapUploadResponseCore {
        private boolean success;

        public boolean isSuccess() {
            return success;
        }
    }


}
