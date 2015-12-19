package com.android.tengfenxiang.activity;

import java.util.HashMap;
import java.util.Map;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestUtil;
import com.android.tengfenxiang.util.ResponseUtil;
import com.android.tengfenxiang.util.VolleyErrorUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ModifyActivity extends BaseActivity {

	private LoadingDialog dialog;
	private TitleBar titleBar;

	private EditText oldPassword;
	private EditText newPassword;
	private EditText confirmPawword;
	private Button confirmButton;

	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_password);

		preferences = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);
		dialog = new LoadingDialog(this);
		initView();
	}

	private void initView() {
		oldPassword = (EditText) findViewById(R.id.old_password);
		newPassword = (EditText) findViewById(R.id.new_password);
		confirmPawword = (EditText) findViewById(R.id.confirm_password);

		titleBar = (TitleBar) findViewById(R.id.title_bar);
		titleBar.setOnClickListener(new OnTitleClickListener() {

			@Override
			public void OnClickRight() {

			}

			@Override
			public void OnClickLeft() {
				finish();
			}
		});

		confirmButton = (Button) findViewById(R.id.confirm_button);
		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String oldTmp = oldPassword.getText().toString();
				String newTmp = newPassword.getText().toString();
				String confirmTmp = confirmPawword.getText().toString();
				boolean tmp = validatePassword(oldTmp, newTmp, confirmTmp);
				if (tmp) {
					dialog.showDialog();
					modifyPassword(application.getCurrentUser().getId(),
							oldTmp, newTmp);
				}
			}
		});
	}

	private boolean validatePassword(String oldPassword, String newPassword,
			String confirmPassword) {
		if (oldPassword.equals("")) {
			Toast.makeText(getApplication(), R.string.empty_old_password,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (newPassword.equals("")) {
			Toast.makeText(getApplication(), R.string.new_password_hint,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (confirmPassword.equals("")) {
			Toast.makeText(getApplication(), R.string.confirm_password_hint,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!newPassword.equals(confirmPassword)) {
			Toast.makeText(getApplication(), R.string.different_password,
					Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	private void modifyPassword(final int userId, final String oldPassword,
			final String newPassword) {
		String url = Constant.MODIFY_PASSWORD_URL;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}
				Object result = ResponseUtil.handleResponse(getApplication(),
						response, null);
				if (null != result) {
					updatePassword(newPassword);
					Toast.makeText(getApplication(), R.string.modify_success,
							Toast.LENGTH_SHORT).show();
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
				VolleyErrorUtil.handleVolleyError(getApplication(), error);
			}
		};
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", userId + "");
				map.put("oldPassword", oldPassword);
				map.put("newPassword", newPassword);
				return map;
			}
		};
		RequestUtil.getRequestQueue(getApplication()).add(stringRequest);
	}

	private void updatePassword(String newPassword) {
		Editor editor = preferences.edit();
		editor.putString("password", newPassword);
		editor.commit();
	}

}