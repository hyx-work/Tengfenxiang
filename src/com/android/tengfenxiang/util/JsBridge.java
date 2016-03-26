package com.android.tengfenxiang.util;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class JsBridge {

	private Context context;

	private JsBridgeListener jsBridgeListener;

	public JsBridge(Context context) {
		this.context = context;
	}

	@JavascriptInterface
	public String getAppVersion() {
		if (null != jsBridgeListener) {
			jsBridgeListener.getAppVersion();
		}
		return DeviceInfoUtil.getInstance(context).getAppVersion();
	}

	@JavascriptInterface
	public void shareWechat() {
		if (null != jsBridgeListener) {
			jsBridgeListener.shareWechat();
		}
	}

	@JavascriptInterface
	public void shareWechatSession() {
		if (null != jsBridgeListener) {
			jsBridgeListener.shareWechatSession();
		}
	}

	@JavascriptInterface
	public void showRecommend(String data) {
		if (null != jsBridgeListener) {
			jsBridgeListener.showRecommend(data);
		}
	}

	public JsBridgeListener getJsBridgeListener() {
		return jsBridgeListener;
	}

	public void setJsBridgeListener(JsBridgeListener jsBridgeListener) {
		this.jsBridgeListener = jsBridgeListener;
	}

	/**
	 * 监听器，当JavaScript调用了JsBridge的方法时，执行回调
	 * 
	 * @author 陈楚昭
	 * 
	 */
	public interface JsBridgeListener {
		public void getAppVersion();

		public void shareWechat();

		public void shareWechatSession();

		public void showRecommend(String data);
	}

}