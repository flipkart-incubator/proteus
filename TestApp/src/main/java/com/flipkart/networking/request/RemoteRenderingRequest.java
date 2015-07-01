package com.flipkart.networking.request;


import com.flipkart.config.Config;
import com.flipkart.networking.response.RemoteRenderingResponse;

public class RemoteRenderingRequest extends BaseRequest<RemoteRenderingResponse>{

	public RemoteRenderingRequest() {
		super(Config.instance.get(Config.StringKey.ApiBaseUrl), Config.instance.get(Config.ApiPathKey.RemoteRenderingRequest));
	}

	@Override
	public Class<RemoteRenderingResponse> getResponseClass() {
		return RemoteRenderingResponse.class;
	}


}
     
