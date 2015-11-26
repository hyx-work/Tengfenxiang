package com.android.tengfenxiang.activity;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public class EditActivity extends Activity {

	private String attributeName;
	private String attributeValue;
	private String title;

	private EditText information;
	private TitleBar titleBar;

	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);

		Intent intent = getIntent();
		attributeName = intent.getStringExtra("attributeName");
		attributeValue = intent.getStringExtra("attributeValue");
		title = intent.getStringExtra("title");

		dialog = new LoadingDialog(this);
		initView();
	}

	private void initView() {
		information = (EditText) findViewById(R.id.infomation);
		information.setText(attributeValue);
		titleBar = (TitleBar) findViewById(R.id.title_bar);
		titleBar.setTitleText(title);
		titleBar.setOnClickListener(new OnTitleClickListener() {
			
			@Override
			public void OnClickRight() {
				dialog.showDialog();
				saveInformation();
			}
			
			@Override
			public void OnClickLeft() {
				finish();
			}
		});
	}

	private void saveInformation() {
		
	}
}