package com.lorant.mobile.android.util.ws;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lorant.mobile.android.constant.Config;

public class RestClient {

  private static AsyncHttpClient client = new AsyncHttpClient();

  public static void get(String method, RequestParams params
		       , AsyncHttpResponseHandler responseHandler) {
	client.get(getAbsoluteUrl(method), params, responseHandler);
  }
  
  public static void post(String method, RequestParams params
		        , AsyncHttpResponseHandler responseHandler) {
	client.post(getAbsoluteUrl(method), params, responseHandler);
  }
  
  private static String getAbsoluteUrl(String method) {
	return Config.WS_BASE_PATH + method;
  }

}
