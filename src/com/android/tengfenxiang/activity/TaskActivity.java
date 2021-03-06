package com.android.tengfenxiang.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.TaskListAdapter;
import com.android.tengfenxiang.bean.ResponseResult;
import com.android.tengfenxiang.bean.Task;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.db.TaskDao;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestUtil;
import com.android.tengfenxiang.util.ResponseUtil;
import com.android.tengfenxiang.util.VolleyErrorUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.xlistview.XListView;
import com.android.tengfenxiang.view.xlistview.XListView.IXListViewListener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class TaskActivity extends BaseActivity implements IXListViewListener {

	private XListView taskListView;
	private TextView hintTextView;
	private LoadingDialog dialog;

	private List<Task> tasks;
	private TaskListAdapter adapter;
	private TaskDao dao;

	private int limit = 10;
	private int offset = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task);

		dialog = new LoadingDialog(this);
		dao = TaskDao.getInstance(getApplication());
		// 从数据库中查找缓存的任务列表
		tasks = dao.findAll();
		adapter = new TaskListAdapter(TaskActivity.this, tasks);
		initView();
	}

	private void getTaskList(int userId, final int limit, final int offset) {
		String url = Constant.TASK_LIST_URL + "?userId=" + userId + "&limit="
				+ limit + "&offset=" + offset;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				ResponseResult result = JSON.parseObject(response,
						ResponseResult.class);
				if (result.getCode() == 200) {
					List<Task> tmp = JSON.parseArray(result.getData()
							.toString(), Task.class);
					// 刷新完成更新数据库中的缓存数据
					if (offset == 0) {
						tasks.clear();
						dao.deleteAll();
						dao.insert(tmp);
					}
					tasks.addAll(tmp);
					adapter.notifyDataSetChanged();
					taskListView.setLoadAll(tmp.size() < limit);
				} else {
					ResponseUtil.handleErrorInfo(getApplication(), result);
				}
				loadComplete();
			}
		};
		// 请求失败的回调函数
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				loadComplete();
				VolleyErrorUtil.handleVolleyError(getApplication(), error);
			}
		};

		StringRequest request = new StringRequest(url, listener, errorListener);
		RequestUtil.getRequestQueue(getApplication()).add(request);
	}

	private void initView() {
		hintTextView = (TextView) findViewById(R.id.empty_task_hint);
		hintTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getTaskList(application.getCurrentUser().getId(), limit, offset);
			}
		});

		taskListView = (XListView) findViewById(R.id.task_list);
		taskListView.setXListViewListener(this);
		taskListView.setAdapter(adapter);
		taskListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				User currentUser = application.getCurrentUser();
				if (arg2 >= 1 && arg2 <= tasks.size()) {
					Intent intent = new Intent(TaskActivity.this,
							X5WebActivity.class);
					intent.putExtra("title", getString(R.string.task_detail));
					String url = tasks.get(arg2 - 1).getShareUrl();
					if (null != currentUser.getToken()
							&& !currentUser.getToken().equals("")) {
						url = url + "&token=" + currentUser.getToken();
					}
					intent.putExtra("url", url);
					intent.putExtra("task_id", tasks.get(arg2 - 1).getId());
					intent.putExtra("web_title", tasks.get(arg2 - 1).getTitle());
					intent.putExtra("web_content", tasks.get(arg2 - 1)
							.getContent());
					intent.putExtra("image", tasks.get(arg2 - 1)
							.getThumbnails());
					startActivity(intent);
				}
			}
		});

		// 数据库中的缓存数据不为空，则隐藏提示文字，静默刷新数据
		// 如果数据库中的缓存数据为空，隐藏列表，尝试请求数据
		if (null != tasks && tasks.size() > 0) {
			hintTextView.setVisibility(View.GONE);
			taskListView.setVisibility(View.VISIBLE);
		} else {
			taskListView.setVisibility(View.GONE);
			hintTextView.setVisibility(View.VISIBLE);
		}
		getTaskList(application.getCurrentUser().getId(), limit, offset);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		offset = 0;
		getTaskList(application.getCurrentUser().getId(), limit, offset);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		offset = offset + limit;
		getTaskList(application.getCurrentUser().getId(), limit, offset);
	}

	private void loadComplete() {
		taskListView.stopRefresh();
		taskListView.stopLoadMore();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss",
				Locale.CHINA);
		Date curDate = new Date(System.currentTimeMillis());
		taskListView.setRefreshTime(formatter.format(curDate));

		// 设置控件的可见性
		if (null != tasks && tasks.size() > 0) {
			taskListView.setVisibility(View.VISIBLE);
			hintTextView.setVisibility(View.GONE);
		} else {
			hintTextView.setVisibility(View.VISIBLE);
			taskListView.setVisibility(View.GONE);
		}

		// 隐藏等待对话框
		if (dialog.isShowing()) {
			dialog.cancelDialog();
		}
	}

	/**
	 * 复写返回键，如果是点击了返回键则用父activity处理点击事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return getParent().onKeyDown(keyCode, event);
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}