package com.flipkart.networking.request;


import com.flipkart.config.Config;
import com.flipkart.networking.response.HomeResponse;

public class HomeRequest extends BaseRequest<HomeResponse>{

	public HomeRequest() {
		super(Config.instance.get(Config.StringKey.ApiBaseUrl), Config.instance.get(Config.ApiPathKey.HomeRequest));
	}

	@Override
	public Class<HomeResponse> getResponseClass() {
		return HomeResponse.class;
	}

}
     
