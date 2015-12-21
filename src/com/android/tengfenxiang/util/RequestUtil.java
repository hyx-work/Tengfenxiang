package com.android.tengfenxiang.util;

import java.util.List;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;

import org.apache.http.cookie.Cookie;
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

	/**
	 * 同步cookie
	 * 
	 * @param context
	 * @param url
	 */
	public static void synCookies(Context context, String url) {
		// 如果httpclient尚未初始化则直接return，例如注册页面
		if (null == mHttpClient) {
			return;
		}
		List<Cookie> cookies = mHttpClient.getCookieStore().getCookies();
		Cookie cookie = null;
		if (null != cookies && !cookies.isEmpty()) {
			for (int i = 0; i < cookies.size(); i++) {
				cookie = cookies.get(i);
			}
		}
		if (null != cookie) {
			CookieSyncManager.createInstance(context);
			CookieManager cookieManager = CookieManager.getInstance();
			StringBuffer buffer = new StringBuffer();
			buffer.append(cookie.getName()).append("=")
					.append(cookie.getValue()).append(";domain=")
					.append(cookie.getDomain()).append(";path=")
					.append(cookie.getPath()).append(";version=")
					.append(cookie.getVersion()).append(";expiry=")
					.append(cookie.getExpiryDate());
			cookieManager.setCookie(url, buffer.toString());
			CookieSyncManager.getInstance().sync();
		}
	}

	/**
	 * 移除cookie，注销时调用
	 * 
	 * @param context
	 */
	public static void removeCookie(Context context) {
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		CookieSyncManager.getInstance().sync();
	}
}