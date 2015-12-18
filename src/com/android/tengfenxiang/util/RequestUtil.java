package com.android.tengfenxiang.util;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Request管理类，在这里保存seeion
 * 
 * @author ccz
 * 
 */
public class RequestUtil {
	private static RequestQueue mRequestQueue;
	private static AbstractHttpClient mHttpClient;

	private RequestUtil() {

	}

	private static void init(Context context) {
		mHttpClient = new DefaultHttpClient();
		mRequestQueue = Volley.newRequestQueue(context, new HttpClientStack(
				mHttpClient));
	}

	public static RequestQueue getRequestQueue(Context context) {
		if (mRequestQueue == null) {
			init(context);
		}
		return mRequestQueue;
	}

	public static void addRequest(Request<?> request, Object tag) {
		if (tag != null) {
			request.setTag(tag);
		}
		mRequestQueue.add(request);
	}

	public static void cancelAll(Object tag) {
		mRequestQueue.cancelAll(tag);
	}

}