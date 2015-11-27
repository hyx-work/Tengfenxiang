package com.android.tengfenxiang.activity;

import java.util.ArrayList;
import java.util.List;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.SimpleListAdapter;
import com.android.tengfenxiang.bean.CityInfo;
import com.android.tengfenxiang.util.CityUtil;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ProvinceActivity extends Activity {

	private TitleBar titleBar;
	private ListView provinceListView;
	private ArrayList<String> code = new ArrayList<String>();
	private ArrayList<String> name = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_infos_layout);

		initView();
	}

	private void initView() {
		getProvinces();
		provinceListView = (ListView) findViewById(R.id.list);
		SimpleListAdapter adapter = new SimpleListAdapter(ProvinceActivity.this, name);
		provinceListView.setAdapter(adapter);
		provinceListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(ProvinceActivity.this, CityActivity.class);
				intent.putExtra("provinceCode", code.get(arg2));
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

	private void getProvinces() {
		CityUtil cityUtil = CityUtil.getInstance(getApplication());
		List<CityInfo> infos = cityUtil.getProvince_list();
		for (CityInfo info : infos) {
			code.add(info.getId());
			name.add(info.getCity_name());
		}
	}

	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	    if(requestCode==0 && resultCode==-1) {  
	        finish();  
	    }  
	} 
}