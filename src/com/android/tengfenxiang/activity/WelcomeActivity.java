package com.android.tengfenxiang.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.db.UserDao;
import com.android.tengfenxiang.util.DeviceInfoUtil;
import com.android.tengfenxiang.util.LoginUtil;
import com.android.tengfenxiang.util.LoginUtil.OnLoginListener;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.volley.NetworkError;
import com.android.volley.VolleyError;

public class WelcomeActivity extends BaseActivity {

	private final int SPLASH_DISPLAY_LENGHT = 2000;
	private SharedPreferences preferences;

	private LoadingDialog dialog;
	private TextView versionTextView;
	private LoginUtil loginUtil;
	private UserDao userDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		initView();

		loginUtil = application.getLoginUtil();
		userDao = UserDao.getInstance(getApplication());
		dialog = new LoadingDialog(this);
		preferences = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (preferences.getBoolean("firststart", true)) {
					Intent intent = new Intent();
					intent.setClass(WelcomeActivity.this, IntrudeActivity.class);
					startActivity(intent);
					finish();
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
						if (null == userDao.findUser(phone)) {
							Intent intent = new Intent();
							intent.setClass(WelcomeActivity.this,
									LoginActivity.class);
							startActivity(intent);
							finish();
						} else {
							dialog.showDialog();
							loginUtil.setOnLoginListener(onLoginListener);
							loginUtil.login(phone, password);
						}
					}
				}
			}
		}, SPLASH_DISPLAY_LENGHT);
	}

	private void initView() {
		versionTextView = (TextView) findViewById(R.id.version);
		DeviceInfoUtil util = DeviceInfoUtil.getInstance(getApplication());
		versionTextView.setText(getString(R.string.version_text)
				+ util.getAppVersion());
	}

	private OnLoginListener onLoginListener = new OnLoginListener() {

		@Override
		public void onLoginSuccess() {
			// TODO Auto-generated method stub
			if (dialog.isShowing()) {
				dialog.cancelDialog();
			}
			Intent intent = new Intent(WelcomeActivity.this,
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
			Intent intent = new Intent(WelcomeActivity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
		}

		@Override
		public void onLoginError(VolleyError error) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			// 当登录出错时，判断是不是断网状态下
			// 如果是断网状态下，则用数据库缓存的对象来初始化
			if (error instanceof NetworkError) {
				intent.setClass(WelcomeActivity.this, MainActivity.class);
				String phone = preferences.getString("phone", "");
				User user = userDao.findUser(phone);
				application.setCurrentUser(user);
				startActivity(intent);
			} else {
				intent.setClass(WelcomeActivity.this, LoginActivity.class);
				startActivity(intent);
			}
			if (dialog.isShowing()) {
				dialog.cancelDialog();
			}
			finish();
		}
	};
}