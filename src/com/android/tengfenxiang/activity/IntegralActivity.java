package com.android.tengfenxiang.activity;

import java.util.ArrayList;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.SimpleListAdapter;
import com.android.tengfenxiang.bean.Integral;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestManager;
import com.android.tengfenxiang.util.ResponseUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.Toast;

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
		getDetails(currentUser.getId(), detailType);
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
		ArrayList<String> values = new ArrayList<String>();
		values.add(integral.getTaskPoints() + getString(R.string.unit_point));
		SimpleListAdapter adapter = new SimpleListAdapter(this, infos, values);
		taskListView.setAdapter(adapter);
	}

	private void initArticleList() {
		articleListView = (ListView) findViewById(R.id.article_list);
		// 添加上下部分的分割线
		ViewStub viewStub = new ViewStub(this);
		articleListView.addFooterView(viewStub);

		// 添加数据
		ArrayList<String> infos = new ArrayList<String>();
		infos.add(getString(R.string.article_points));
		ArrayList<String> values = new ArrayList<String>();
		values.add(integral.getArticlePoints() + getString(R.string.unit_point));
		SimpleListAdapter adapter = new SimpleListAdapter(this, infos, values);
		articleListView.setAdapter(adapter);
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
		values.add(integral.getRegisterPoints()
				+ getString(R.string.unit_point));
		values.add(integral.getSigninPoints() + getString(R.string.unit_point));
		values.add(integral.getReadArticleInAppPoints()
				+ getString(R.string.unit_point));
		values.add(integral.getLikeArticleInAppPoints()
				+ getString(R.string.unit_point));
		SimpleListAdapter adapter = new SimpleListAdapter(this, infos, values);
		otherListView.setAdapter(adapter);
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
				Toast.makeText(getApplication(), R.string.unknow_error,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		};

		StringRequest request = new StringRequest(url, listener, errorListener);
		RequestManager.getRequestQueue(getApplication()).add(request);
	}
}