package com.android.tengfenxiang.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.receiver.LogoutReceiver;
import com.android.tengfenxiang.receiver.LogoutReceiver.OnLogoutListener;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.DensityUtil;
import com.android.tengfenxiang.util.ImageLoadUtil;
import com.android.tengfenxiang.util.RequestUtil;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends AbstractActivityGroup {

	private static final String CONTENT_0 = "taskActivity";
	private static final String CONTENT_1 = "articleActivity";
	private static final String CONTENT_2 = "myProfitActivity";
	private static final String CONTENT_3 = "userInfoActivity";

	private IntentFilter intentFilter;
	private LogoutReceiver logoutReceiver;
	private LocalBroadcastManager localBroadcastManager;
	private SharedPreferences preferences;

	private SparseIntArray normalIcons = new SparseIntArray();
	private SparseIntArray selectedIcons = new SparseIntArray();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);

		normalIcons.put(R.id.radio_button0, R.drawable.tab_icon_1);
		normalIcons.put(R.id.radio_button1, R.drawable.tab_icon_2);
		normalIcons.put(R.id.radio_button2, R.drawable.tab_icon_3);
		normalIcons.put(R.id.radio_button3, R.drawable.tab_icon_4);

		selectedIcons.put(R.id.radio_button0, R.drawable.tab_icon_1_highlight);
		selectedIcons.put(R.id.radio_button1, R.drawable.tab_icon_2_highlight);
		selectedIcons.put(R.id.radio_button2, R.drawable.tab_icon_3_highlight);
		selectedIcons.put(R.id.radio_button3, R.drawable.tab_icon_4_highlight);

		// 接收注销操作的广播，如果接收到则销毁
		intentFilter = new IntentFilter(Constant.LOGOUT_BROADCAST);
		logoutReceiver = new LogoutReceiver();
		logoutReceiver.setOnLogoutListener(new OnLogoutListener() {

			@Override
			public void onLogout() {
				// TODO Auto-generated method stub
				// 清除缓存的密码
				Editor editor = preferences.edit();
				editor.putString("password", "");
				editor.commit();
				// 清除cookie缓存
				RequestUtil.removeCookie(getApplication());

				// 跳转到登录界面
				Intent intent = new Intent(MainActivity.this,
						LoginActivity.class);
				startActivity(intent);
				finish();
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
				changeTabStyle(buttonView.getId());
				setContainerView(CONTENT_0, TaskActivity.class);
				break;

			case R.id.radio_button1:
				changeTabStyle(buttonView.getId());
				setContainerView(CONTENT_1, ArticleActivity.class);
				break;

			case R.id.radio_button2:
				changeTabStyle(buttonView.getId());
				setContainerView(CONTENT_2, MyProfitActivity.class);
				break;

			case R.id.radio_button3:
				changeTabStyle(buttonView.getId());
				setContainerView(CONTENT_3, UserInfoActivity.class);
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 改变tab显示的图片和文字颜色
	 * 
	 * @param id
	 */
	private void changeTabStyle(int id) {
		for (RadioButton button : radioButtons) {
			if (button.getId() == id) {
				button.setTextColor(getResources().getColor(R.color.base_color));
				button.setCompoundDrawablesWithIntrinsicBounds(0,
						selectedIcons.get(button.getId()), 0, 0);
			} else {
				button.setTextColor(getResources().getColor(R.color.gray));
				button.setCompoundDrawablesWithIntrinsicBounds(0,
						normalIcons.get(button.getId()), 0, 0);
			}

			// 设置图片大小为30dp
			Drawable[] drawables = button.getCompoundDrawables();
			drawables[1].setBounds(new Rect(0, 0, DensityUtil.dip2px(
					getApplication(), 25), DensityUtil.dip2px(getApplication(),
					25)));
			button.setCompoundDrawables(drawables[0], drawables[1],
					drawables[2], drawables[3]);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		localBroadcastManager.unregisterReceiver(logoutReceiver);
		// 清除cookie缓存
		RequestUtil.removeCookie(getApplication());
		// 清除内存中的图片缓存
		ImageLoadUtil.clearMemoryCache(getApplication());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true;
			Toast.makeText(this, R.string.double_click_exit, Toast.LENGTH_SHORT)
					.show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false;
				}
			}, 2000);
		} else {
			finish();
			System.exit(0);
		}
	}
}