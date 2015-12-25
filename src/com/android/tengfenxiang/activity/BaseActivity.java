package com.android.tengfenxiang.activity;

import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.receiver.LogoutReceiver;
import com.android.tengfenxiang.receiver.LogoutReceiver.OnLogoutListener;
import com.android.tengfenxiang.util.Constant;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

/**
 * activity基类
 * 
 * @author ccz
 * 
 */
public abstract class BaseActivity extends Activity {

	protected static MainApplication application;
	protected IntentFilter intentFilter;
	protected LogoutReceiver logoutReceiver;
	protected LocalBroadcastManager localBroadcastManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = ((MainApplication) getApplication());

		// 接收注销操作的广播，如果接收到则销毁
		intentFilter = new IntentFilter(Constant.LOGOUT_BROADCAST);
		logoutReceiver = new LogoutReceiver();
		logoutReceiver.setOnLogoutListener(new OnLogoutListener() {

			@Override
			public void onLogout() {
				// TODO Auto-generated method stub
				finish();
			}
		});
		localBroadcastManager = LocalBroadcastManager.getInstance(this);
		localBroadcastManager.registerReceiver(logoutReceiver, intentFilter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		localBroadcastManager.unregisterReceiver(logoutReceiver);
	}

}