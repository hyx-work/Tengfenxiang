package com.android.tengfenxiang.activity;

import com.android.tengfenxiang.R;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class WelcomeActivity extends Activity {

	private final int SPLASH_DISPLAY_LENGHT = 2000;
	private SharedPreferences preferences;
	private Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		preferences = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (preferences.getBoolean("firststart", true)) {
					editor = preferences.edit();
					editor.putBoolean("firststart", false);
					editor.commit();
					Intent intent = new Intent();
					intent.setClass(WelcomeActivity.this, IntrudeActivity.class);
					WelcomeActivity.this.startActivity(intent);
					WelcomeActivity.this.finish();
				} else {
					Intent intent = new Intent();
					intent.setClass(WelcomeActivity.this, MainActivity.class);
					WelcomeActivity.this.startActivity(intent);
					WelcomeActivity.this.finish();
				}
			}
		}, SPLASH_DISPLAY_LENGHT);
	}

}