package com.android.tengfenxiang.application;

import android.app.Application;

import com.android.tengfenxiang.util.ImageLoadTools;

/**
 * 复写application，初始化图片加载的参数
 * @author ccz
 *
 */
public class MainApplication extends Application {
	public void onCreate() {
		super.onCreate();
		// 初始化ImageLoader对象
		ImageLoadTools.initImageLoader(getApplicationContext());
	}
}