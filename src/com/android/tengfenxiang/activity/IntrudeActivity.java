package com.android.tengfenxiang.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.ViewPagerAdapter;
import com.bumptech.glide.Glide;

public class IntrudeActivity extends Activity implements OnClickListener,
		OnPageChangeListener {

	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;
	private Button button;
	private TextView jumpButton;

	private SharedPreferences preferences;
	private Editor editor;

	/**
	 * 引导图片资源
	 */
	private static final int[] pics = { R.drawable.launch_picture_01,
			R.drawable.launch_picture_02, R.drawable.launch_picture_03,
			R.drawable.launch_picture_04 };

	private ImageView[] dots;

	/**
	 * 记录当前选中位置
	 */
	private int currentIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intrude);
		button = (Button) findViewById(R.id.button);
		jumpButton = (TextView) findViewById(R.id.jump_btn);
		views = new ArrayList<View>();

		preferences = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);

		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(this);
			iv.setScaleType(ScaleType.FIT_XY);
			iv.setLayoutParams(mParams);
			Glide.with(getApplication()).load(pics[i]).into(iv);
			views.add(iv);
		}
		vp = (ViewPager) findViewById(R.id.viewpager);
		vpAdapter = new ViewPagerAdapter(views);
		vp.setAdapter(vpAdapter);
		vp.setOnPageChangeListener(this);
		initDots();
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				editor = preferences.edit();
				editor.putBoolean("firststart", false);
				editor.commit();

				Intent intent = new Intent();
				intent.setClass(IntrudeActivity.this, LoginActivity.class);
				IntrudeActivity.this.startActivity(intent);
				finish();
			}
		});

		jumpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				editor = preferences.edit();
				editor.putBoolean("firststart", false);
				editor.commit();

				Intent intent = new Intent();
				intent.setClass(IntrudeActivity.this, LoginActivity.class);
				IntrudeActivity.this.startActivity(intent);
				finish();
			}
		});
	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

		dots = new ImageView[pics.length];

		// 循环取得小点图片
		for (int i = 0; i < pics.length; i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(true);
			dots[i].setOnClickListener(this);
			dots[i].setTag(i);
		}

		currentIndex = 0;
		dots[currentIndex].setEnabled(false);
	}

	private void setCurView(int position) {
		if (position < 0 || position >= pics.length) {
			return;
		}

		vp.setCurrentItem(position);
	}

	private void setCurDot(int positon) {
		if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
			return;
		}

		dots[positon].setEnabled(false);
		dots[currentIndex].setEnabled(true);

		currentIndex = positon;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		setCurDot(arg0);
		if (arg0 == 3) {
			button.setVisibility(View.VISIBLE);
			jumpButton.setVisibility(View.GONE);
		} else {
			button.setVisibility(View.GONE);
			jumpButton.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		setCurView(position);
		setCurDot(position);
	}

}