package com.android.tengfenxiang.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
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

		Activity contentActivity = localActivityManager
				.getActivity(activityName);
		if (null == contentActivity) {
			localActivityManager.startActivity(activityName,
					initIntent(activityClassTye));
		}

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