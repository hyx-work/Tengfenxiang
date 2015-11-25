package com.android.tengfenxiang.activity;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.Subordinate;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestManager;
import com.android.tengfenxiang.util.ResponseTools;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class SubordinateActivity extends Activity {

	private Subordinate subordinate;
	private User currentUser;

	private TextView numberTextView;
	private TextView profitTextView;
	private TitleBar titleBar;
	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subordinate);

		MainApplication application = ((MainApplication) getApplication());
		currentUser = application.getCurrentUser();

		dialog = new LoadingDialog(this);
		dialog.showDialog();
		getSubordinate(currentUser.getId());
	}

	private void initView() {
		numberTextView = (TextView) findViewById(R.id.number);
		profitTextView = (TextView) findViewById(R.id.profit);

		String count = subordinate.getSubordinateCount() + numberTextView.getText().toString();
		numberTextView.setText(count);
		String profit = subordinate.getSubordinatePoints() + profitTextView.getText().toString();
		profitTextView.setText(profit);

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
		String url = Constant.SUBORDINATE_URL + "?userId="+ userId;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				subordinate = (Subordinate) ResponseTools.handleResponse(
						getApplicationContext(), response, Subordinate.class);
				initView();
				if(dialog.isShowing()) {
					dialog.cancelDialog();
				}
			}
		};
		// 请求失败的回调函数
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if(dialog.isShowing()) {
					dialog.cancelDialog();
				}
				Toast.makeText(getApplication(),
						getString(R.string.unknow_error), Toast.LENGTH_SHORT)
						.show();
			}
		};

		StringRequest request = new StringRequest(url, listener, errorListener);
		RequestManager.getRequestQueue().add(request);
	}
}