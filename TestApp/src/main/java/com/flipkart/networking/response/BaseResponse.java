package com.flipkart.networking.response;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {
	// nothing to do here
	
	@SerializedName("STATUS")
	private String status;

	@SerializedName("STATUS_CODE")
	private String statusCode;

}
