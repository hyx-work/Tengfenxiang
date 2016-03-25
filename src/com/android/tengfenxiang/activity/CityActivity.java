package com.android.tengfenxiang.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.SimpleListAdapter;
import com.android.tengfenxiang.bean.City;
import com.android.tengfenxiang.util.CityUtil;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;

public class CityActivity extends BaseActivity {

	private TitleBar titleBar;
	private ListView cityListView;
	private ArrayList<String> code = new ArrayList<String>();
	private ArrayList<String> name = new ArrayList<String>();
	private Intent intent;
	private String provinceCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_infos_layout);

		intent = getIntent();
		provinceCode = intent.getStringExtra("provinceCode");
		initView();
	}

	private void initView() {
		getCitys();
		cityListView = (ListView) findViewById(R.id.list);
		SimpleListAdapter adapter = new SimpleListAdapter(CityActivity.this,
				name);
		cityListView.setAdapter(adapter);
		cityListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(CityActivity.this,
						DistrictActivity.class);
				intent.putExtra("provinceCode", provinceCode);
				intent.putExtra("cityCode", code.get(arg2));
				startActivityForResult(intent, 0);
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

	private void getCitys() {
		CityUtil util = CityUtil.getInstance(application);
		List<City> infos = util.getCities(application, provinceCode);
		for (City info : infos) {
			code.add(info.getCityId());
			name.add(info.getCityName());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == -1) {
			setResult(-1, intent);
			finish();
		}
	}
}
