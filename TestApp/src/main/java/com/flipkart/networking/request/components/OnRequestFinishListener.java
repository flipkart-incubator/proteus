package com.flipkart.networking.request.components;

import com.flipkart.networking.request.BaseRequest;

public interface OnRequestFinishListener<T> {
	public void onRequestFinish(BaseRequest<T> request);
}
