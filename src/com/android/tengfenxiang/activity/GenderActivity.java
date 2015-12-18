package com.android.tengfenxiang.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.SimpleListAdapter;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestUtil;
import com.android.tengfenxiang.util.ResponseUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GenderActivity extends BaseActivity {

	private ListView genderListView;
	private LoadingDialog dialog;
	private TitleBar titleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_infos_layout);

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
				saveGender(currentUser.getId(), arg2);
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

	private void saveGender(final int userId, final int index) {
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
					application.setCurrentUser(result);
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
				Toast.makeText(getApplication(), R.string.unknow_error,
						Toast.LENGTH_SHORT).show();
			}
		};
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", userId + "");
				map.put("gender", index + "");
				return map;
			}
		};
		RequestUtil.getRequestQueue(getApplication()).add(stringRequest);
	}
}