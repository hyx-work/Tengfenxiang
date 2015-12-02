package com.android.tengfenxiang.activity;

import java.util.HashMap;
import java.util.Map;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.application.MainApplication;
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private LoadingDialog dialog;
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		preferences = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);
		dialog = new LoadingDialog(this);
		
		dialog.showDialog();
		login("13826473672", "cczccz");
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
				MainApplication application = ((MainApplication) getApplication());
				User user = (User) ResponseUtil.handleResponse(
						getApplication(), response, User.class);
				application.setCurrentUser(user);

				Editor editor = preferences.edit();
				editor.putString("phone", phone);
				editor.putString("password", password);
				editor.commit();

				Intent intent = new Intent(LoginActivity.this,
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
		RequestManager.getRequestQueue().add(stringRequest);
	}
}