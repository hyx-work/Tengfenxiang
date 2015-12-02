package com.android.tengfenxiang.activity;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.receiver.LogoutReceiver;
import com.android.tengfenxiang.receiver.LogoutReceiver.OnLogoutListener;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

public class MainActivity extends AbstractActivityGroup {

	private static final String CONTENT_0 = "taskActivity";
	private static final String CONTENT_1 = "articleActivity";
	private static final String CONTENT_2 = "myProfitActivity";
	private static final String CONTENT_3 = "userInfoActivity";

	private IntentFilter intentFilter;
	private LogoutReceiver logoutReceiver;
	private LocalBroadcastManager localBroadcastManager;
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);

		// 接收注销操作的广播，如果接收到则销毁
		intentFilter = new IntentFilter(getPackageName());
		logoutReceiver = new LogoutReceiver();
		logoutReceiver.setOnLogoutListener(new OnLogoutListener() {

			@Override
			public void onLogout() {
				// TODO Auto-generated method stub
				logout();
			}
		});
		localBroadcastManager = LocalBroadcastManager.getInstance(this);
		localBroadcastManager.registerReceiver(logoutReceiver, intentFilter);

		preferences = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);

		((RadioButton) findViewById(R.id.radio_button0)).setChecked(true);
		setContainerView(CONTENT_0, TaskActivity.class);
	}

	@Override
	protected ViewGroup getContainer() {
		return (ViewGroup) findViewById(R.id.container);
	}

	@Override
	protected void initTabBarButtons() {
		initTabBarButton(R.id.radio_button0);
		initTabBarButton(R.id.radio_button1);
		initTabBarButton(R.id.radio_button2);
		initTabBarButton(R.id.radio_button3);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			switch (buttonView.getId()) {

			case R.id.radio_button0:
				setContainerView(CONTENT_0, TaskActivity.class);
				break;

			case R.id.radio_button1:
				setContainerView(CONTENT_1, ArticleActivity.class);
				break;

			case R.id.radio_button2:
				setContainerView(CONTENT_2, MyProfitActivity.class);
				break;

			case R.id.radio_button3:
				setContainerView(CONTENT_3, UserInfoActivity.class);
				break;

			default:
				break;
			}
		}
	}

	private void logout() {
		Editor editor = preferences.edit();
		editor.putString("phone", "");
		editor.putString("password", "");
		editor.commit();
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		localBroadcastManager.unregisterReceiver(logoutReceiver);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

	}
}