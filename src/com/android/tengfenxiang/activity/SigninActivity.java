package com.android.tengfenxiang.activity;

import java.util.HashMap;
import java.util.Map;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.SigninListAdapter;
import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.SigninStatus;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestManager;
import com.android.tengfenxiang.util.ResponseTools;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SigninActivity extends Activity {

	private TextView statusTextView;
	private Button signinButton;
	private ListView signinListView;

	private SigninStatus signinStatus;
	private SigninListAdapter adapter;

	private User currentUser;
	private LoadingDialog dialog;
	private TitleBar titleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signin);

		MainApplication application = ((MainApplication) getApplication());
		currentUser = application.getCurrentUser();

		dialog = new LoadingDialog(this);
		dialog.showDialog();
		getSigninStatus(currentUser.getId());
	}

	private void initView() {
		signinButton = (Button) findViewById(R.id.signin_button);
		statusTextView = (TextView) findViewById(R.id.today_info);
		signinListView = (ListView) findViewById(R.id.recent_list);

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

		signinButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.showDialog();
				signin(currentUser.getId());
			}
		});

		// 当日已签到
		if (1 == signinStatus.getStatus()) {
			statusTextView.setText(R.string.signin_today);
			String info = getString(R.string.get_profit_info);
			info = info.replace("?", signinStatus.getPoints() + "");
			signinButton.setText(info);
			signinButton.setClickable(false);
			signinButton.setEnabled(false);
		} else {
			statusTextView.setText(R.string.not_signin_today);
			signinButton.setText(R.string.signin);
		}

		if (null != signinStatus && null != signinStatus.getRecent()
				&& 0 != signinStatus.getRecent().size()) {
			adapter = new SigninListAdapter(SigninActivity.this,
					signinStatus.getRecent());
			signinListView.setAdapter(adapter);
		} else {

		}
	}

	/**
	 * 获取当前用户的签到情况
	 * 
	 * @param userId
	 */
	private void getSigninStatus(final int userId) {
		String url = Constant.SIGNIN_STATUS_URL + "?userId=" + userId;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				signinStatus = (SigninStatus) ResponseTools.handleResponse(
						getApplication(), response, SigninStatus.class);
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

	/**
	 * 用户进行签到操作
	 * 
	 * @param userId
	 */
	private void signin(final int userId) {
		String url = Constant.SIGNIN_URL;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}
				Object result = ResponseTools.handleResponse(getApplication(), response, null);
				if (null != result) {
					Toast.makeText(getApplication(), R.string.signin_success,
							Toast.LENGTH_SHORT).show();
					// 刷新界面
					dialog.showDialog();
					getSigninStatus(userId);
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
				return map;
			}
		};
		RequestManager.getRequestQueue().add(stringRequest);
	}
}