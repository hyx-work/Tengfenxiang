package com.android.tengfenxiang.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewStub;
import android.widget.ListView;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.IntegralListAdapter;
import com.android.tengfenxiang.bean.Integral;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.ListViewUtil;
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

public class IntegralActivity extends BaseActivity {

	private TitleBar titleBar;
	private String title;
	private int detailType;

	private LoadingDialog dialog;
	private Integral integral;

	private ListView taskListView;
	private ListView articleListView;
	private ListView otherListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.integral_detail);

		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		detailType = intent.getIntExtra("detailType", 0);

		dialog = new LoadingDialog(this);
		dialog.showDialog();
		getDetails(application.getCurrentUser().getId(), detailType);
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

		initTaskList();
		initArticleList();
		initOtherList();
	}

	private void initTaskList() {
		taskListView = (ListView) findViewById(R.id.task_list);
		// 添加上下部分的分割线
		ViewStub viewStub = new ViewStub(this);
		taskListView.addFooterView(viewStub);

		// 添加数据
		ArrayList<String> infos = new ArrayList<String>();
		infos.add(getString(R.string.task_points));
		infos.add(getString(R.string.task_count));
		infos.add(getString(R.string.task_view_count));
		ArrayList<String> values = new ArrayList<String>();
		if (null != integral) {
			values.add(integral.getTaskPoints() + "");
			values.add(integral.getTaskCount() + "");
			values.add(integral.getTaskViewCount() + "");
		}
		IntegralListAdapter adapter = new IntegralListAdapter(this, infos,
				values);
		taskListView.setAdapter(adapter);
		ListViewUtil.setListViewHeightBasedOnChildren(taskListView);
	}

	private void initArticleList() {
		articleListView = (ListView) findViewById(R.id.article_list);
		// 添加上下部分的分割线
		ViewStub viewStub = new ViewStub(this);
		articleListView.addFooterView(viewStub);

		// 添加数据
		ArrayList<String> infos = new ArrayList<String>();
		infos.add(getString(R.string.article_points));
		infos.add(getString(R.string.article_count));
		infos.add(getString(R.string.article_view_count));
		ArrayList<String> values = new ArrayList<String>();
		if (null != integral) {
			values.add(integral.getArticlePoints() + "");
			values.add(integral.getArticleCount() + "");
			values.add(integral.getArticleViewCount() + "");
		}
		IntegralListAdapter adapter = new IntegralListAdapter(this, infos,
				values);
		articleListView.setAdapter(adapter);
		ListViewUtil.setListViewHeightBasedOnChildren(articleListView);
	}

	private void initOtherList() {
		otherListView = (ListView) findViewById(R.id.other_list);
		// 添加上下部分的分割线
		ViewStub viewStub = new ViewStub(this);
		otherListView.addFooterView(viewStub);

		// 添加数据
		ArrayList<String> infos = new ArrayList<String>();
		infos.add(getString(R.string.register));
		infos.add(getString(R.string.signin));
		infos.add(getString(R.string.read_article_in_app_points));
		infos.add(getString(R.string.like_article_in_app_points));
		ArrayList<String> values = new ArrayList<String>();
		if (null != integral) {
			values.add(integral.getRegisterPoints() + "");
			values.add(integral.getSigninPoints() + "");
			values.add(integral.getReadArticleInAppPoints() + "");
			values.add(integral.getLikeArticleInAppPoints() + "");
		}
		IntegralListAdapter adapter = new IntegralListAdapter(this, infos,
				values);
		otherListView.setAdapter(adapter);
		ListViewUtil.setListViewHeightBasedOnChildren(otherListView);
	}

	private void getDetails(int userId, int detailType) {
		String url = Constant.PROFIT_DETAIL_URL + "?userId=" + userId
				+ "&detailType=" + detailType;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				integral = (Integral) ResponseUtil.handleResponse(
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
				VolleyErrorUtil.handleVolleyError(getApplication(), error);
				initView();
			}
		};

		StringRequest request = new StringRequest(url, listener, errorListener);
		RequestUtil.getRequestQueue(getApplication()).add(request);
	}

}