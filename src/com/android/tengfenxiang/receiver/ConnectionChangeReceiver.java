package com.android.tengfenxiang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 监听网络状态变化的广播
 * 
 * @author ccz
 * 
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {

	private OnNetworkChangedListener onNetworkChangedListener;

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		// try捕获错误
		try {
			if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
				if (null != onNetworkChangedListener) {
					onNetworkChangedListener.onUnusable();
				}
			} else {
				if (null != onNetworkChangedListener) {
					onNetworkChangedListener.onUsable();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public OnNetworkChangedListener getOnNetworkChangedListener() {
		return onNetworkChangedListener;
	}

	public void setOnNetworkChangedListener(
			OnNetworkChangedListener onNetworkChangedListener) {
		this.onNetworkChangedListener = onNetworkChangedListener;
	}

	/**
	 * 网络状态变化时的接口
	 * 
	 * @author ccz
	 * 
	 */
	public interface OnNetworkChangedListener {

		/**
		 * 变为可用时要执行的操作
		 */
		public void onUsable();

		/**
		 * 变为不可用时要执行的操作
		 */
		public void onUnusable();
	}
}