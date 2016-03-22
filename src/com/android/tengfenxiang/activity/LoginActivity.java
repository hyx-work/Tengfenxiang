package com.android.tengfenxiang.activity;

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

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.LoginUtil;
import com.android.tengfenxiang.util.LoginUtil.OnLoginListener;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.volley.VolleyError;

public class LoginActivity extends BaseActivity {

	private LoadingDialog dialog;
	private SharedPreferences preferences;
	private EditText phoneEditText;
	private EditText passwordEditText;
	private CheckBox saveCheckBox;
	private Button loginButton;
	private TextView registerTextView;
	private LoginUtil loginUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		loginUtil = application.getLoginUtil();
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
		saveCheckBox.setChecked(preferences.getBoolean("remember", true));

		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String phone = phoneEditText.getText().toString();
				String password = passwordEditText.getText().toString();
				boolean result = validateParams(phone, password);
				if (result) {
					dialog.showDialog();
					loginUtil.setOnLoginListener(onLoginListener);
					loginUtil.login(phone, password);
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
		editor.putBoolean("remember", saveCheckBox.isChecked());
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

	private OnLoginListener onLoginListener = new OnLoginListener() {

		@Override
		public void onLoginSuccess() {
			// TODO Auto-generated method stub
			String phone = phoneEditText.getText().toString();
			String password = passwordEditText.getText().toString();
			savePassword(phone, password);
			if (dialog.isShowing()) {
				dialog.cancelDialog();
			}
			Intent intent = new Intent(LoginActivity.this,
					GoldCoinActivity.class);
			startActivity(intent);
			finish();
		}

		@Override
		public void onLoginFail() {
			// TODO Auto-generated method stub
			if (dialog.isShowing()) {
				dialog.cancelDialog();
			}
		}

		@Override
		public void onLoginError(VolleyError error) {
			// TODO Auto-generated method stub
			if (dialog.isShowing()) {
				dialog.cancelDialog();
			}
		}
	};

	/**
	 * 将注册提示文字修改为超链接的格式
	 * 
	 * @return
	 */
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