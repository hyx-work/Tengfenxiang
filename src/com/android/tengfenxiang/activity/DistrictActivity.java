package com.android.tengfenxiang.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.SimpleListAdapter;
import com.android.tengfenxiang.bean.City;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.db.UserDao;
import com.android.tengfenxiang.util.CityUtil;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestUtil;
import com.android.tengfenxiang.util.ResponseUtil;
import com.android.tengfenxiang.util.VolleyErrorUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class DistrictActivity extends BaseActivity {
	private TitleBar titleBar;
	private ListView districtListView;
	private ArrayList<String> code = new ArrayList<String>();
	private ArrayList<String> name = new ArrayList<String>();
	private String provinceCode;
	private String cityCode;
	private LoadingDialog dialog;
	private Intent intent;
	private UserDao userDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_infos_layout);

		userDao = UserDao.getInstance(getApplication());
		dialog = new LoadingDialog(this);
		intent = getIntent();
		provinceCode = intent.getStringExtra("provinceCode");
		cityCode = intent.getStringExtra("cityCode");
		initView();
	}

	private void initView() {
		getDistricts();
		districtListView = (ListView) findViewById(R.id.list);
		SimpleListAdapter adapter = new SimpleListAdapter(
				DistrictActivity.this, name);
		districtListView.setAdapter(adapter);
		districtListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.showDialog();
				saveCityInfo(application.getCurrentUser().getId(),
						provinceCode, cityCode, code.get(arg2));
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
	 * 
	 * @param province
	 * @param city
	 */
	protected void saveCityInfo(final int userId, final String province,
			final String city, final String district) {
		String url = Constant.MODIFY_INFO_URL;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}
				User result = (User) ResponseUtil.handleResponse(
						getApplication(), response, User.class);
				if (null != result) {
					Toast.makeText(getApplication(), R.string.modify_success,
							Toast.LENGTH_SHORT).show();
					if (null == result.getToken()) {
						result.setToken(application.getCurrentUser().getToken());
					}
					// 更新内存中的用户对象
					application.setCurrentUser(result);
					// 更新数据库中缓存的用户对象
					userDao.update(result);
					setResult(-1, intent);
					finish();
				}
			}
		};
		// 请求失败的回调函数
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}
				VolleyErrorUtil.handleVolleyError(getApplication(), error);
			}
		};
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", userId + "");
				map.put("province", province);
				map.put("city", city);
				map.put("district", district);
				return map;
			}
		};
		RequestUtil.getRequestQueue(getApplication()).add(stringRequest);
	}

	private void getDistricts() {
		CityUtil util = CityUtil.getInstance(application);
		List<City> infos = util.getDistricts(application, cityCode);
		for (City info : infos) {
			code.add(info.getCountyId());
			name.add(info.getCountyName());
		}
	}
}