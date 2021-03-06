package com.android.tengfenxiang.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.SimpleListAdapter;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.ImageLoadUtil;
import com.android.tengfenxiang.util.RequestUtil;
import com.android.tengfenxiang.util.VolleyErrorUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class SettingActivity extends BaseActivity {

	private TitleBar titleBar;
	private LoadingDialog dialog;

	private Button logoutButton;
	private ListView modifyListView;
	private ListView cacheListView;
	private LocalBroadcastManager localBroadcastManager;

	private SimpleListAdapter cacheAdapter;
	private SimpleListAdapter modifyAdapter;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Toast.makeText(getApplication(),
					getString(R.string.clear_cache_success), Toast.LENGTH_SHORT)
					.show();
			cacheAdapter.notifyDataSetChanged();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		localBroadcastManager = LocalBroadcastManager.getInstance(this);
		dialog = new LoadingDialog(this);
		initView();
	}

	private void initView() {
		logoutButton = (Button) findViewById(R.id.logout_btn);
		logoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDialog();
			}
		});

		ArrayList<String> modify = new ArrayList<String>();
		modify.add(getString(R.string.modify_password));
		modifyAdapter = new SimpleListAdapter(SettingActivity.this, modify);
		modifyListView = (ListView) findViewById(R.id.modify_password);
		modifyListView.setAdapter(modifyAdapter);
		modifyListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(SettingActivity.this,
						ModifyActivity.class);
				startActivity(intent);
			}
		});

		ArrayList<String> cacheInfo = new ArrayList<String>();
		final ArrayList<String> cacheValue = new ArrayList<String>();
		cacheInfo.add(getString(R.string.clear_cache));
		cacheValue.add(ImageLoadUtil.getCacheSize(getApplication()));
		cacheAdapter = new SimpleListAdapter(SettingActivity.this, cacheInfo,
				cacheValue);
		cacheListView = (ListView) findViewById(R.id.clear_cache);
		cacheListView.setAdapter(cacheAdapter);
		cacheListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// 在子线程中执行清理操作
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						ImageLoadUtil.clearDiskCache(getApplication());
						// 更新界面上的显示数据
						cacheValue.remove(0);
						cacheValue.add(ImageLoadUtil
								.getCacheSize(getApplication()));
						handler.sendEmptyMessage(0);
					}
				}).start();
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

	private void showDialog() {
		Builder builder = new Builder(SettingActivity.this);
		builder.setMessage(R.string.confirm_to_logout);
		builder.setTitle(R.string.dialog_title);
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface,
							int which) {
						dialogInterface.dismiss();
						dialog.showDialog();
						logout(application.getCurrentUser().getId());
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	private void logout(final int userId) {
		String url = Constant.LOGOUT_URL;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				System.err.println(response);
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}
				// 发送注销登录广播
				Intent broadcast = new Intent(Constant.LOGOUT_BROADCAST);
				localBroadcastManager.sendBroadcast(broadcast);
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
				return map;
			}
		};
		RequestUtil.getRequestQueue(getApplication()).add(stringRequest);
	}
}