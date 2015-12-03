package com.android.tengfenxiang.activity;

import java.util.HashMap;
import java.util.Map;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestManager;
import com.android.tengfenxiang.util.ResponseUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class WelcomeActivity extends BaseActivity {

	private final int SPLASH_DISPLAY_LENGHT = 2000;
	private SharedPreferences preferences;
	private Editor editor;
	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);

		dialog = new LoadingDialog(this);
		preferences = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (preferences.getBoolean("firststart", true)) {
					editor = preferences.edit();
					editor.putBoolean("firststart", false);
					editor.commit();
					Intent intent = new Intent();
					intent.setClass(WelcomeActivity.this, IntrudeActivity.class);
					WelcomeActivity.this.startActivity(intent);
					WelcomeActivity.this.finish();
				} else {
					String phone = preferences.getString("phone", "");
					String password = preferences.getString("password", "");

					if (phone.equals("") || password.equals("")) {
						Intent intent = new Intent();
						intent.setClass(WelcomeActivity.this,
								LoginActivity.class);
						startActivity(intent);
						finish();
					} else {
						dialog.showDialog();
						login(phone, password);
					}
				}
			}
		}, SPLASH_DISPLAY_LENGHT);
	}

	private void login(final String phone, final String password) {
		String url = Constant.LOGIN_URL;
		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}
				currentUser = (User) ResponseUtil.handleResponse(
						getApplication(), response, User.class);
				application.setCurrentUser(currentUser);

				Intent intent = new Intent(WelcomeActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}
		};
		// 请求失败的回调函数
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}

				// 登录失败设置空的User对象
				currentUser = new User();
				application.setCurrentUser(currentUser);

				Intent intent = new Intent(WelcomeActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
				Toast.makeText(getApplication(), R.string.unknow_error,
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
		RequestManager.getRequestQueue(getApplication()).add(stringRequest);
	}
}