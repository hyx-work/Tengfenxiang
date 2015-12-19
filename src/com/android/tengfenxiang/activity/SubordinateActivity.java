package com.android.tengfenxiang.activity;

import java.util.ArrayList;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.SimpleListAdapter;
import com.android.tengfenxiang.bean.Subordinate;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestUtil;
import com.android.tengfenxiang.util.ResponseUtil;
import com.android.tengfenxiang.util.VolleyErrorUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.os.Bundle;
import android.widget.ListView;

public class SubordinateActivity extends BaseActivity {

	private Subordinate subordinate;

	private ListView subordinateListView;
	private TitleBar titleBar;
	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subordinate);

		dialog = new LoadingDialog(this);
		dialog.showDialog();
		getSubordinate(application.getCurrentUser().getId());
	}

	private void initView() {
		subordinateListView = (ListView) findViewById(R.id.subordinate_info);
		ArrayList<String> infos = new ArrayList<String>();
		infos.add(getString(R.string.my_subordinate_count));
		infos.add(getString(R.string.my_subordinate_points));

		ArrayList<String> values = new ArrayList<String>();
		if (null != subordinate) {
			values.add(subordinate.getSubordinateCount()
					+ getString(R.string.unit_people));
			values.add(subordinate.getSubordinatePoints()
					+ getString(R.string.unit_yuan));
		}

		SimpleListAdapter adapter = new SimpleListAdapter(
				SubordinateActivity.this, infos, values);
		subordinateListView.setAdapter(adapter);

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

	private void getSubordinate(int userId) {
		String url = Constant.SUBORDINATE_URL + "?userId=" + userId;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				subordinate = (Subordinate) ResponseUtil.handleResponse(
						getApplicationContext(), response, Subordinate.class);
				initView();
				if (dialog.isShowing()) {
					dialog.cancelDialog();
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
				initView();
			}
		};

		StringRequest request = new StringRequest(url, listener, errorListener);
		RequestUtil.getRequestQueue(getApplication()).add(request);
	}
}