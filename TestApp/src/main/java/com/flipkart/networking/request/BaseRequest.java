package com.flipkart.networking.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.flipkart.networking.request.components.OnRequestErrorListener;
import com.flipkart.networking.request.components.OnRequestFinishListener;
import com.flipkart.networking.request.components.OnRequestStartListener;
import com.flipkart.networking.request.components.RequestError;
import com.google.gson.Gson;


public abstract class BaseRequest<T> {

	private String baseUrl;
	private Map<String, String> params = new HashMap<String, String>();
	private String path;
	private String generatedUrl = null;
	private OnRequestStartListener<T> onStartListener;
	private OnRequestErrorListener<T> onRequestErrorListener;
	private OnRequestFinishListener<T> onRequestFinishListener;
	private T response;
	private String rawResponse;

	public BaseRequest(String baseUrl, String path) {
		this.baseUrl = baseUrl;
		this.path = path;
	}

	public void addParam(String key, String value) {
		params.put(key, value);
	}

	protected String getParamsString() {
		String paramsString = "";
		Iterator<Map.Entry<String, String>> entries = params.entrySet().iterator();

		if (entries.hasNext()) {
			paramsString = "?";
		}

		while (entries.hasNext()) {
			if (paramsString != "?") {
				paramsString += "&";
			}
			Map.Entry<String, String> entry = entries.next();
			try {
				paramsString += URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
			} catch (NullPointerException e) {
			}
		}
		return paramsString;
	}

	public String getGeneratedUrl() {
		if (generatedUrl == null) {
			generatedUrl = generateFullUrl();
		}
		return generatedUrl;
	}

	protected String generateFullUrl() {
		String url = "";
		url += baseUrl;
		url += "/" + path;
		url += getParamsString();
		return url;
	}

	public void setOnStartListener(OnRequestStartListener<T> listener) {
		this.onStartListener = listener;
	}

	public void setOnErrorListener(OnRequestErrorListener<T> listener) {
		this.onRequestErrorListener = listener;
	}

	public void setOnResponseListener(OnRequestFinishListener<T> listener) {
		this.onRequestFinishListener = listener;
	}

	public void onResponse(String response) {
		this.rawResponse = response;
		Class<T> responseClass = getResponseClass();
		if (responseClass == null) {
			throw new IllegalStateException("Request's getResponseClass() should not return null");
		}
		try {
			this.response = parseResponse(response,responseClass);
		} catch (Exception e) {
			onError("Json Parse error: " + e.getMessage());
		}
		if (this.response != null && this.onRequestFinishListener != null) {
			this.onRequestFinishListener.onRequestFinish(this);
		}
	}

    protected T parseResponse(String rawResponse,Class<T> responseClass)
    {
        Gson gson = createParser();
        return gson.fromJson(rawResponse, responseClass);

    }

    protected Gson createParser()
    {
        return new Gson();
    }


	public void onError(final String error) {
		if (this.onRequestErrorListener != null) {
			this.onRequestErrorListener.onRequestError(this, new RequestError() {

				@Override
				public String getReason() {
					return error;
				}
			});
		}
	}

	public void onStart() {
		if (this.onStartListener != null) {
			this.onStartListener.onRequestStart(this);
		}
	}

	public T getResponse() {
		return response;
	}

	public abstract Class<T> getResponseClass();

	public String getRawResponse() {
		return rawResponse;
	}

}
