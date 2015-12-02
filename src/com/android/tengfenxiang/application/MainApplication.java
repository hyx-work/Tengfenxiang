package com.android.tengfenxiang.application;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.ItemInfo;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.receiver.ConnectionChangeReceiver;
import com.android.tengfenxiang.receiver.ConnectionChangeReceiver.OnNetworkChangedListener;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.ImageLoadUtil;
import com.android.tengfenxiang.util.RequestManager;
import com.android.tengfenxiang.util.ResponseUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

/**
 * 复写application，初始化参数
 * 
 * @author ccz
 * 
 */
public class MainApplication extends Application {

	private static User currentUser;
	private ConnectionChangeReceiver myReceiver;
	private SharedPreferences preferences;
	
	public void onCreate() {
		super.onCreate();
		// 初始化ImageLoader对象
		ImageLoadUtil.initImageLoader(getApplicationContext());

		preferences = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);
		// 注册广播
		registerReceiver();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		// 内存不足时清除缓存
		ImageLoadUtil.clearMemoryCache();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		// 销毁时注销广播
		unregisterReceiver();
	}

	private void login(final String phone, final String password) {
		String url = Constant.LOGIN_URL;
		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				currentUser = (User) ResponseUtil.handleResponse(
						MainApplication.this, response, User.class);
			}
		};
		// 请求失败的回调函数
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				currentUser = new User();
				Toast.makeText(MainApplication.this, R.string.unknow_error,
						Toast.LENGTH_SHORT).show();
			}
		};
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("phone", phone);
				map.put("password", password);
				map.put("deviceId", "1");
				map.put("deviceInfo", "1");
				map.put("pushToken", "22");
				map.put("appVersion", "1.0.0");
				map.put("os", "android");
				map.put("osVersion", "4.4.4");
				map.put("model", "model");
				return map;
			}
		};
		RequestManager.getRequestQueue().add(stringRequest);
	}

	/**
	 * 注册广播监听器
	 */
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		myReceiver = new ConnectionChangeReceiver();
		myReceiver.setOnNetworkChangedListener(new OnNetworkChangedListener() {

			@Override
			public void onUsable() {
				String phone = preferences.getString("phone", "");
				String password = preferences.getString("password", "");
				// 当网络从断开到连接上时，且本机上已经登录过，需要进行登录操作
				if (!phone.equals("") && !password.equals("")) {
					login(phone, password);
				}
			}

			@Override
			public void onUnusable() {
				// 当网络从连接到断开，需要给出提示
				Toast.makeText(MainApplication.this, R.string.network_error,
						Toast.LENGTH_SHORT).show();
			}
		});
		registerReceiver(myReceiver, filter);
	}

	/**
	 * 注销广播监听器
	 */
	private void unregisterReceiver() {
		unregisterReceiver(myReceiver);
	}

	public User getCurrentUser() {
		return currentUser;
	}

	@SuppressWarnings("static-access")
	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

}