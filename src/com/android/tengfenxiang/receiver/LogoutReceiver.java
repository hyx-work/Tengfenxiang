package com.android.tengfenxiang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 监听注销登录的广播
 * 
 * @author ccz
 * 
 */
public class LogoutReceiver extends BroadcastReceiver {

	private OnLogoutListener onLogoutListener;

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		if (null != onLogoutListener) {
			onLogoutListener.onLogout();
		}
	}

	public OnLogoutListener getOnLogoutListener() {
		return onLogoutListener;
	}

	public void setOnLogoutListener(OnLogoutListener onLogoutListener) {
		this.onLogoutListener = onLogoutListener;
	}

	/**
	 * 实现注销的接口
	 * 
	 * @author ccz
	 * 
	 */
	public static interface OnLogoutListener {
		public void onLogout();
	}
}