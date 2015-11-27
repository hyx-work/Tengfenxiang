package com.android.tengfenxiang.activity;

import java.util.ArrayList;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.SimpleListAdapter;
import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class GenderActivity extends Activity {

	private User currentUser;
	private ListView genderListView;
	private LoadingDialog dialog;
	private TitleBar titleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_infos_layout);

		MainApplication application = ((MainApplication) getApplication());
		currentUser = application.getCurrentUser();
		dialog = new LoadingDialog(this);

		initView();
	}

	private void initView() {
		genderListView = (ListView) findViewById(R.id.list);
		ArrayList<String> infos = new ArrayList<String>();
		infos.add(getString(R.string.male));
		infos.add(getString(R.string.female));
		SimpleListAdapter adapter = new SimpleListAdapter(GenderActivity.this,
				infos);
		genderListView.setAdapter(adapter);
		genderListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.showDialog();
				saveGender(arg2);
			}
		});

		titleBar = (TitleBar) findViewById(R.id.title_bar);
		titleBar.setTitleText(getString(R.string.select_gender_title));
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

	private void saveGender(int index) {

	}
}