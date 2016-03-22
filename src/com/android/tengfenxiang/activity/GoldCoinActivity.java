package com.android.tengfenxiang.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.Setting;
import com.android.tengfenxiang.view.textview.RiseNumberTextView;
import com.bumptech.glide.Glide;

public class GoldCoinActivity extends BaseActivity {

	private MediaPlayer mediaPlayer;
	private Button takeInButton;
	private RiseNumberTextView profitTextView;
	private ImageView coinImageView;
	private Setting setting;
	private int cash;
	private SharedPreferences preferences;

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

		coinImageView = (ImageView) findViewById(R.id.coin_image);
		profitTextView = (RiseNumberTextView) findViewById(R.id.profit_number_text);

		float pointsToCashRate = 0.1f;
		if (null != setting) {
			pointsToCashRate = (float) setting.getPointsToCashRate();
		} else {
			pointsToCashRate = preferences.getFloat("pointsToCashRate", 0.02f);
		}
		int point = (int) (cash / pointsToCashRate);
		profitTextView.withNumber(point);
		profitTextView.setDuration(2000);

		// 开始显示gif图片
		Glide.with(this.getApplicationContext()).load(R.drawable.gold_coin)
				.into(coinImageView);
		// 数字开始增长
		profitTextView.start();
		// 开始播放声音
		mediaPlayer.start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		mediaPlayer.release();
	}

}