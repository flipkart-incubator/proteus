package com.flipkart.networking.request.components;

import com.flipkart.networking.request.BaseRequest;

public interface OnRequestStartListener<T> {
	public abstract void onRequestStart(BaseRequest<T> request);
}
