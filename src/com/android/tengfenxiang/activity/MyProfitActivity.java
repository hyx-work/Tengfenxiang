package com.android.tengfenxiang.activity;

import java.util.ArrayList;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.MyProfitListAdapter;
import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.Summary;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestManager;
import com.android.tengfenxiang.util.ResponseTools;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MyProfitActivity extends Activity {

	private ListView summaryListView;

	private User currentUser;
	private Summary summary;
	private ArrayList<String> titles = new ArrayList<String>();

	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_profit);

		MainApplication application = ((MainApplication) getApplication());
		currentUser = application.getCurrentUser();

		dialog = new LoadingDialog(this);
		dialog.showDialog();
		getSummary(currentUser.getId());
	}

	private void initView() {
		summaryListView = (ListView) findViewById(R.id.summary);
		fillList();
		addListener();
	}

	private void addListener() {
		summaryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				switch (arg2) {
				case 0:
				case 1:
				case 2:
					intent.setClass(MyProfitActivity.this,
							IntegralActivity.class);
					intent.putExtra("title", titles.get(arg2));
					break;
				case 3:
					intent.setClass(MyProfitActivity.this,
							WithdrawActivity.class);
					break;
				default:
					intent.setClass(MyProfitActivity.this,
							ApplyWithdrawActivity.class);
					intent.putExtra("withdrawPoints",
							summary.getWithdrawablePoints());
					break;
				}
				startActivity(intent);
			}
		});
	}

	private void fillList() {
		titles = new ArrayList<String>();
		titles.add(getString(R.string.realtime_points));
		titles.add(getString(R.string.yesterday_points));
		titles.add(getString(R.string.total_points));
		titles.add(getString(R.string.withdraw_points));
		titles.add(getString(R.string.withdrawable_points));
		ArrayList<String> values = new ArrayList<String>();
		values.add(summary.getRealtimePoints() + getString(R.string.unit_point));
		values.add(summary.getYesterdayPoints()
				+ getString(R.string.unit_point));
		values.add(summary.getTotalPoints() + getString(R.string.unit_point));
		values.add(summary.getWithdrawPoints() + getString(R.string.unit_yuan));
		values.add(summary.getWithdrawablePoints()
				+ getString(R.string.unit_yuan));
		MyProfitListAdapter adapter = new MyProfitListAdapter(
				MyProfitActivity.this, titles, values);
		summaryListView.setAdapter(adapter);
	}

	/**
	 * 获取收益概览信息
	 * 
	 * @param context
	 * @param userId
	 *            用户ID
	 */
	public void getSummary(int userId) {
		// 根据用户id构造请求地址
		String url = Constant.SUMMARY_URL + "?userId=" + userId;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				summary = (Summary) ResponseTools.handleResponse(
						getApplication(), response, Summary.class);
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}
				initView();
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

		StringRequest request = new StringRequest(url, listener, errorListener);
		RequestManager.getRequestQueue().add(request);
	}
}