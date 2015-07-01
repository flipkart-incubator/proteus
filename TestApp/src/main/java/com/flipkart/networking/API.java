package com.flipkart.networking;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flipkart.networking.request.BaseRequest;
import com.flipkart.networking.internal.MultipartRequest;

import org.apache.http.entity.mime.MultipartEntity;

public class API {
	private static API instance;
	private RequestQueue queue;

	/**
	 * Use application context here since its used for caching
	 * 
	 * @param context
	 */
	private API(Context context) {
		queue = Volley.newRequestQueue(context);
	}

	public static synchronized API getInstance(Context context) {
		if (instance == null) {
			instance = new API(context);
		}
		return instance;
	}

	public <T> void processAsync(final BaseRequest<T> request) {
		request.onStart();
		Listener<String> listener = getSuccessListener(request);
		ErrorListener errorListener = getErrorListener(request);
        MultipartEntity multiPartEntity = request.getMultiPartEntity();
        if(multiPartEntity==null) {
            StringRequest volleyRequest = new StringRequest(
                    request.getGeneratedUrl(), listener, errorListener);
            process(volleyRequest);
        }
        else
        {
            MultipartRequest multipartRequest = new MultipartRequest(request.getGeneratedUrl(),listener, errorListener, multiPartEntity);
            process(multipartRequest);
        }
	}

    public void processAsync(Request request)
    {
        process(request);
    }

	private <T> ErrorListener getErrorListener(final BaseRequest<T> request) {
		return new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				String responseBody = null;
				if (error != null && error.networkResponse != null) {
					responseBody = error.getMessage();
				}
				request.onError(responseBody);
			}
		};
	}

	private <T> Listener<String> getSuccessListener(
			final BaseRequest<T> request) {
		return new Listener<String>() {
			@Override
			public void onResponse(String response) {
				request.onResponse(response);
			}
		};
	}

	private void process(Request req) {
		queue.add(req);
	}

}
