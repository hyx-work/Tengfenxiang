package com.android.tengfenxiang.activity;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class WithdrawActivity extends Activity {

	private TitleBar titleBar;
	private ListView recordsList;

	private User currentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.withdraw_records);

		MainApplication application = ((MainApplication) getApplication());
		currentUser = application.getCurrentUser();

		initView();
	}

	private void initView() {
		titleBar = (TitleBar) findViewById(R.id.title_bar);
		titleBar.setOnClickListener(new OnTitleClickListener() {

			@Override
			public void OnClickLeft() {
				finish();
			}

			@Override
			public void OnClickRight() {
				
			}
		});
	}

	private void getWithdrawRecords() {
		
	}
}