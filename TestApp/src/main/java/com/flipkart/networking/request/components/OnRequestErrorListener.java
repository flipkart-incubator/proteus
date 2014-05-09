package com.flipkart.networking.request.components;

import com.flipkart.networking.request.BaseRequest;

public interface OnRequestErrorListener<T> {
	public abstract void onRequestError(BaseRequest<T> request, RequestError error);
}
