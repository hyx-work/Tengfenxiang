package com.android.tengfenxiang.activity;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.Message;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class MessageActivity extends Activity {

	private ListView messageListView;
	private Message message;

	private TitleBar titleBar;
	private LoadingDialog dialog;
	private User currentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_message);

		MainApplication application = ((MainApplication) getApplication());
		currentUser = application.getCurrentUser();
		dialog = new LoadingDialog(this);
	}

	private void initView() {
		messageListView = (ListView) findViewById(R.id.messages);
		titleBar = (TitleBar) findViewById(R.id.title_bar);
		titleBar.setOnClickListener(new OnTitleClickListener() {
			
			@Override
			public void OnClickRight() {
				
			}
			
			@Override
			public void OnClickLeft() {
				finish();
			}
		});
	}
}