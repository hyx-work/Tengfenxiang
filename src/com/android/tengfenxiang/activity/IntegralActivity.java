package com.android.tengfenxiang.activity;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.Integral;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestManager;
import com.android.tengfenxiang.util.ResponseTools;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class IntegralActivity extends BaseActivity {

	private TitleBar titleBar;
	private String title;

	/**
	 * 要查询的收益的日期
	 */
	private String profitData;

	/**
	 * 是否是查询历史积分，是为1不是为0
	 */
	private int isTotal;

	private LoadingDialog dialog;
	private Integral integral;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.integral_detail);

		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		profitData = intent.getStringExtra("profitData");
		isTotal = intent.getIntExtra("isTotal", 1);

		dialog = new LoadingDialog(this);
		dialog.showDialog();
		getDetails(currentUser.getId(), profitData, isTotal);
	}

	private void initView() {
		titleBar = (TitleBar) findViewById(R.id.title_bar);
		titleBar.setTitleText(title);
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

	private void getDetails(int userId, String profitData, int isTotal) {
		String url = Constant.PROFIT_DETAIL_URL + "?userId=" + userId
				+ "&isTotal=" + isTotal;
		if (null != profitData && !profitData.equals("")) {
			url = url + "&profitData=" + profitData;
		}

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				System.err.println(response);
				integral = (Integral) ResponseTools.handleResponse(
						getApplication(), response, Integral.class);
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
				Toast.makeText(getApplication(), R.string.unknow_error,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		};

		StringRequest request = new StringRequest(url, listener, errorListener);
		RequestManager.getRequestQueue().add(request);
	}
}