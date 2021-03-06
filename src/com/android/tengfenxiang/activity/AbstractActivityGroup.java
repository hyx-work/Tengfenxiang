package com.android.tengfenxiang.activity;

import java.util.ArrayList;

import com.android.tengfenxiang.util.DensityUtil;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;

@SuppressWarnings("deprecation")
public abstract class AbstractActivityGroup extends ActivityGroup implements
		CompoundButton.OnCheckedChangeListener {

	protected ArrayList<RadioButton> radioButtons = new ArrayList<RadioButton>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTabBarButtons();
	}

	private ViewGroup container;

	private LocalActivityManager localActivityManager;

	abstract protected ViewGroup getContainer();

	protected void initTabBarButton(int id) {
		RadioButton btn = (RadioButton) findViewById(id);
		// 设置图片大小为22dp
		Drawable[] drawables = btn.getCompoundDrawables();
		drawables[1]
				.setBounds(new Rect(0, 0, DensityUtil.dip2px(getApplication(),
						22), DensityUtil.dip2px(getApplication(), 22)));
		btn.setCompoundDrawables(drawables[0], drawables[1], drawables[2],
				drawables[3]);
		btn.setOnCheckedChangeListener(this);
		radioButtons.add(btn);
	}

	abstract protected void initTabBarButtons();

	private Intent initIntent(Class<?> cls) {
		return new Intent(this, cls).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	}

	protected void setContainerView(String activityName,
			Class<?> activityClassTye) {
		if (null == localActivityManager) {
			localActivityManager = getLocalActivityManager();
		}

		if (null == container) {
			container = getContainer();
		}

		container.removeAllViews();
		localActivityManager.startActivity(activityName,
				initIntent(activityClassTye));

		container.addView(localActivityManager.getActivity(activityName)
				.getWindow().getDecorView(), new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}