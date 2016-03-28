package com.android.tengfenxiang.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.Setting;
import com.android.tengfenxiang.view.textview.RiseNumberTextView;

public class GoldCoinActivity extends BaseActivity {

	private MediaPlayer mediaPlayer;
	private Button takeInButton;
	private RiseNumberTextView profitTextView;
	private WebView coinView;
	private RelativeLayout layout;
	private TextView goldTextView;
	private Setting setting;
	private int cash;
	private SharedPreferences preferences;

	private int[] goldText = { R.string.gold_text_01, R.string.gold_text_02,
			R.string.gold_text_03, R.string.gold_text_04,
			R.string.gold_text_05, R.string.gold_text_06, R.string.gold_text_07 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gold_coin);

		setting = application.getSetting();
		cash = application.getCurrentUser().getWithdrawableCash();
		preferences = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);

		mediaPlayer = MediaPlayer.create(GoldCoinActivity.this,
				R.raw.gold_coin_music);
		layout = (RelativeLayout) findViewById(R.id.gold_coin_layout);

		// 初始化银两显示文字
		profitTextView = (RiseNumberTextView) findViewById(R.id.profit_number_text);
		float pointsToCashRate = 0.1f;
		if (null != setting) {
			pointsToCashRate = (float) setting.getPointsToCashRate();
		} else {
			pointsToCashRate = preferences.getFloat("pointsToCashRate", 0.01f);
		}
		int point = (int) (cash / pointsToCashRate);
		profitTextView.withNumber(point);
		// 时间设置为2秒，跟音频和gif的时间一致
		profitTextView.setDuration(2000);

		// 初始化文案显示
		goldTextView = (TextView) findViewById(R.id.gold_text);
		int textIndex = 0;
		if (point < 1000) {
			textIndex = 0;
		} else if (point >= 1000 && point < 2000) {
			textIndex = 1;
		} else if (point >= 2000 && point < 3000) {
			textIndex = 2;
		} else if (point >= 3000 && point < 5000) {
			textIndex = 3;
		} else if (point >= 5000 && point < 10000) {
			textIndex = 4;
		} else if (point >= 10000 && point < 20000) {
			textIndex = 5;
		} else if (point >= 20000 && point < 500000) {
			textIndex = 6;
		}
		goldTextView.setText(goldText[textIndex]);

		// 初始化按钮事件
		takeInButton = (Button) findViewById(R.id.take_in_btn);
		takeInButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GoldCoinActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}
		});

		// 初始化webview用来显示gif图片
		coinView = (WebView) findViewById(R.id.coin_image);
		coinView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				super.onProgressChanged(view, newProgress);
				// 网页加载成功后开始播放声音，并且数字从0开始增长
				if (newProgress == 100) {
					// 数字开始增长
					profitTextView.start();
					// 开始播放声音
					mediaPlayer.start();
				}
			}
		});
		coinView.loadUrl("file:///android_asset/gold_coin.html");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 释放媒体播放器资源
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		mediaPlayer.release();

		// 释放webview资源
		if (null != coinView) {
			layout.removeView(coinView);
			coinView.removeAllViews();
			coinView.destroy();
		}
	}

}