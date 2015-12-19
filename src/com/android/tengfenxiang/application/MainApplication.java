package com.android.tengfenxiang.application;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.receiver.ConnectionChangeReceiver;
import com.android.tengfenxiang.receiver.ConnectionChangeReceiver.OnNetworkChangedListener;
import com.android.tengfenxiang.util.ImageLoadUtil;
import com.android.tengfenxiang.util.LoginUtil;
import com.android.tengfenxiang.util.LoginUtil.OnLoginListener;
import com.android.volley.VolleyError;

/**
 * 复写application，初始化参数
 * 
 * @author ccz
 * 
 */
public class MainApplication extends Application {

	private static User currentUser;
	private ConnectionChangeReceiver myReceiver;
	private static LoginUtil loginUtil;
	private SharedPreferences preferences;

	public void onCreate() {
		super.onCreate();
		// 初始化ImageLoader对象
		ImageLoadUtil.initImageLoader(getApplicationContext());
		// 初始化账户登录工具类
		loginUtil = LoginUtil.getInstance(this);
		// 注册广播
		registerReceiver();
		preferences = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);
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
				if (!phone.equals("") && !password.equals("")
						&& null != getCurrentUser()
						&& null == getCurrentUser().getToken()) {
					Toast.makeText(MainApplication.this, R.string.try_to_login,
							Toast.LENGTH_SHORT).show();
					loginUtil.setOnLoginListener(onLoginListener);
					loginUtil.login(phone, password);
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

	private OnLoginListener onLoginListener = new OnLoginListener() {

		@Override
		public void onLoginSuccess() {
			// TODO Auto-generated method stub
			Toast.makeText(MainApplication.this, R.string.login_success,
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onLoginFail() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLoginError(VolleyError error) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * 注销广播监听器
	 */
	private void unregisterReceiver() {
		unregisterReceiver(myReceiver);
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		MainApplication.currentUser = currentUser;
	}

	public LoginUtil getLoginUtil() {
		return loginUtil;
	}

	public void setLoginUtil(LoginUtil loginUtil) {
		MainApplication.loginUtil = loginUtil;
	}

}