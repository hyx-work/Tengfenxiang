package com.android.tengfenxiang.activity;

import java.util.HashMap;
import java.util.Map;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.DeviceInfoUtil;
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
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private LoadingDialog dialog;
	private SharedPreferences preferences;
	private EditText phoneEditText;
	private EditText passwordEditText;
	private CheckBox saveCheckBox;
	private Button loginButton;
	private TextView registerTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		preferences = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);
		dialog = new LoadingDialog(this);
		initView();
	}

	private void initView() {
		phoneEditText = (EditText) findViewById(R.id.phone);
		phoneEditText.setText(preferences.getString("phone", ""));
		passwordEditText = (EditText) findViewById(R.id.password);
		passwordEditText.setText(preferences.getString("password", ""));
		loginButton = (Button) findViewById(R.id.login_btn);
		saveCheckBox = (CheckBox) findViewById(R.id.save_password);

		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String phone = phoneEditText.getText().toString();
				String password = passwordEditText.getText().toString();
				boolean result = validateParams(phone, password);
				if (result) {
					dialog.showDialog();
					login(phone, password);
				}
			}
		});
		registerTextView = (TextView) findViewById(R.id.register);
		registerTextView.setText(getRegisterUrl());
		registerTextView.setMovementMethod(LinkMovementMethod.getInstance());
	}

	private void savePassword(String phone, String password) {
		Editor editor = preferences.edit();
		editor.putString("phone", phone);
		if (saveCheckBox.isChecked()) {
			editor.putString("password", password);
		}
		editor.commit();
	}

	private boolean validateParams(String phone, String password) {
		// TODO Auto-generated method stub
		if (null == phone || phone.equals("")) {
			Toast.makeText(getApplication(), R.string.empty_phone,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (null == password || password.equals("")) {
			Toast.makeText(getApplication(), R.string.empty_password,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void login(final String phone, final String password) {
		String url = Constant.LOGIN_URL;
		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				System.err.println(response);
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}
				MainApplication application = ((MainApplication) getApplication());
				Object object = ResponseUtil.handleResponse(getApplication(),
						response, User.class);
				if (null != object) {
					User user = (User) object;
					application.setCurrentUser(user);
					savePassword(phone, password);

					Intent intent = new Intent(LoginActivity.this,
							MainActivity.class);
					startActivity(intent);
					finish();
				}
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
				DeviceInfoUtil util = DeviceInfoUtil
						.getInstance(getApplication());
				map.put("phone", phone);
				map.put("password", password);
				map.put("deviceId", util.getDeviceId());
				map.put("deviceInfo", util.getDeviceInfo());
				if (util.getPushToken() != null
						&& !util.getPushToken().equals("")) {
					map.put("pushToken", util.getPushToken());
				}
				map.put("appVersion", util.getAppVersion());
				map.put("os", util.getOs());
				map.put("osVersion", util.getOsVersion());
				map.put("model", util.getModel());
				return map;
			}
		};
		RequestManager.getRequestQueue(getApplication()).add(stringRequest);
	}

	private SpannableString getRegisterUrl() {
		SpannableString spanStr = new SpannableString(
				getString(R.string.register_app_acount));
		spanStr.setSpan(new UnderlineSpan(), 0, spanStr.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spanStr.setSpan(new ClickableSpan() {

			@Override
			public void onClick(View widget) {

				Intent intent = new Intent(LoginActivity.this,
						WebActivity.class);
				intent.putExtra("url", Constant.REGISTER_URL);
				intent.putExtra("title", getString(R.string.register));
				startActivity(intent);
			}
		}, 0, spanStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spanStr.setSpan(new ForegroundColorSpan(Color.BLUE), 0,
				spanStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanStr;
	}
}