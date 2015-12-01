package com.android.tengfenxiang.activity;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.MessageListAdapter;
import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.Message;
import com.android.tengfenxiang.bean.ResponseResult;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestManager;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MessageActivity extends Activity {

	private ListView messageListView;
	private List<Message> messages;
	private List<Boolean> isOpen;

	private TitleBar titleBar;
	private LoadingDialog dialog;
	private User currentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);

		MainApplication application = ((MainApplication) getApplication());
		currentUser = application.getCurrentUser();

		dialog = new LoadingDialog(this);
		dialog.showDialog();
		getSystemMessages(currentUser.getId());
	}

	private void initView() {
		messageListView = (ListView) findViewById(R.id.messages);
		isOpen = new ArrayList<Boolean>();
		for (int i = 0; i < messages.size(); i++) {
			isOpen.add(false);
		}

		final MessageListAdapter adapter = new MessageListAdapter(
				MessageActivity.this, messages, isOpen);
		messageListView.setAdapter(adapter);
		messageListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				isOpen.set(arg2, !isOpen.get(arg2));
				adapter.notifyDataSetChanged();
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

	private void getSystemMessages(int userId) {
		String url = Constant.MESSAGE_URL + "?userId=" + userId;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				System.err.println(response);

				ResponseResult result = JSON.parseObject(response,
						ResponseResult.class);
				messages = JSON.parseArray(result.getData().toString(),
						Message.class);
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