package com.android.tengfenxiang.activity;

import java.util.ArrayList;
import java.util.List;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.SimpleListAdapter;
import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.CityInfo;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.util.CityUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class CityActivity extends Activity {
	private TitleBar titleBar;
	private ListView cityListView;
	private ArrayList<String> code = new ArrayList<String>();
	private ArrayList<String> name = new ArrayList<String>();
	private String provinceCode;
	private LoadingDialog dialog;
	private Intent intent;
	private User currentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_infos_layout);

		MainApplication application = ((MainApplication) getApplication());
		currentUser = application.getCurrentUser();
		dialog = new LoadingDialog(this);
		intent = getIntent();
		provinceCode = intent.getStringExtra("provinceCode");
		initView();
	}

	private void initView() {
		getProvinces();
		cityListView = (ListView) findViewById(R.id.list);
		SimpleListAdapter adapter = new SimpleListAdapter(CityActivity.this, name);
		cityListView.setAdapter(adapter);
		cityListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.showDialog();
				saveCityInfo(provinceCode, code.get(arg2));
			}
		});

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

	/**
	 * 将数据保存到后台
	 * @param province
	 * @param city
	 */
	protected void saveCityInfo(String province, String city) {
//		dialog.cancelDialog();
//		setResult(-1, intent);
//		finish();
	}

	private void getProvinces() {
		CityUtil cityUtil = CityUtil.getInstance(getApplication());
		List<CityInfo> infos = cityUtil.getCity_map().get(provinceCode);
		for (CityInfo info : infos) {
			code.add(info.getId());
			name.add(info.getCity_name());
		}
	}
}