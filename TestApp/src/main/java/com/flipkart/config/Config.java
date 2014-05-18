package com.flipkart.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public enum Config {

	instance;

	public enum ApiPathKey {
		HomeRequest;
	}

	public enum StringKey {
		AppName,ApiBaseUrl;
	}

	public enum DateKey {

	}

	public enum IntKey {

	}

	public enum FloatKey {

	}

	private Config() {
		initCommonValues();
		initApiValues();
	}

	private void initApiValues() {
		values.put(StringKey.ApiBaseUrl, "http://192.168.1.105/layoutengine");
		values.put(ApiPathKey.HomeRequest, "fetch.php?view=home");
	}

	private void initCommonValues() {
		values.put(StringKey.AppName, "Layout engine demo");

		
	}

	protected Map<Object, Object> values = new HashMap<Object, Object>();

	protected <T> T get(Object key, Class<T> type) {
		return type.cast(values.get(key));
	}

	public String get(StringKey key) {
		return get(key, String.class);
	}

	public Date get(DateKey key) {
		return get(key, Date.class);
	}

	public Integer get(IntKey key) {
		return get(key, Integer.class);
	}

	public Float get(FloatKey key) {
		return get(key, Float.class);
	}

	public String get(ApiPathKey key) {
		return get(key, String.class);
	}

}