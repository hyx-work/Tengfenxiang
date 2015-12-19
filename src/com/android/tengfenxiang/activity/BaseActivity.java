package com.android.tengfenxiang.activity;

import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.User;

import android.app.Activity;
import android.os.Bundle;

/**
 * activity基类
 * 
 * @author ccz
 * 
 */
public abstract class BaseActivity extends Activity {

protected static MainApplication application;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = ((MainApplication) getApplication());
	}

}