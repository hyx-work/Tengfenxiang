package com.android.tengfenxiang.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.application.MainApplication;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends AbstractActivityGroup {

	private static final String CONTENT_0 = "taskActivity";
	private static final String CONTENT_1 = "articleActivity";
	private static final String CONTENT_2 = "myProfitActivity";
	private static final String CONTENT_3 = "userInfoActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);

		MainApplication application = ((MainApplication) getApplication());
		application.setMainActivity(this);

		((RadioButton) findViewById(R.id.radio_button0)).setChecked(true);
		setContainerView(CONTENT_0, TaskActivity.class);
	}

	@Override
	protected ViewGroup getContainer() {
		return (ViewGroup) findViewById(R.id.container);
	}

	@Override
	protected void initTabBarButtons() {
		initTabBarButton(R.id.radio_button0);
		initTabBarButton(R.id.radio_button1);
		initTabBarButton(R.id.radio_button2);
		initTabBarButton(R.id.radio_button3);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			switch (buttonView.getId()) {

			case R.id.radio_button0:
				setContainerView(CONTENT_0, TaskActivity.class);
				break;

			case R.id.radio_button1:
				setContainerView(CONTENT_1, ArticleActivity.class);
				break;

			case R.id.radio_button2:
				setContainerView(CONTENT_2, MyProfitActivity.class);
				break;

			case R.id.radio_button3:
				setContainerView(CONTENT_3, UserInfoActivity.class);
				break;

			default:
				break;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click();
			return true;
		}
		return false;
	}

	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true;
			Toast.makeText(this, R.string.double_click_exit, Toast.LENGTH_SHORT)
					.show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false;
				}
			}, 2000);

		} else {
			finish();
			System.exit(0);
		}
	}
}