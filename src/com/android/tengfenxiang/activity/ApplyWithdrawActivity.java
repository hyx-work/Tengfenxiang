package com.android.tengfenxiang.activity;

import java.util.HashMap;
import java.util.Map;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestManager;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ApplyWithdrawActivity extends Activity {

	private User currentUser;

	private TitleBar titleBar;
	private TextView withdrawableTextView;
	private EditText withdrawEditText;
	private TextView alipayTextView;
	private Button applybButton;
	private RelativeLayout alipayLayout;

	private LoadingDialog dialog;

	private float withdrawPoints;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.withdraw_apply);

		MainApplication application = ((MainApplication) getApplication());
		currentUser = application.getCurrentUser();

		Intent intent = getIntent();
		withdrawPoints = intent.getFloatExtra("withdrawPoints", 1);

		dialog = new LoadingDialog(this);
		initView();
	}

	private void initView() {
		alipayTextView = (TextView) findViewById(R.id.alipay_account);
		alipayTextView.setText(currentUser.getAlipay());

		withdrawEditText = (EditText) findViewById(R.id.withdraw_points);
		withdrawEditText.setText(withdrawPoints + "");

		withdrawableTextView = (TextView) findViewById(R.id.withdrawable_points);
		withdrawableTextView.setText(withdrawPoints
				+ getString(R.string.unit_yuan));

		alipayLayout = (RelativeLayout) findViewById(R.id.alipay_account_layout);
		alipayLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ApplyWithdrawActivity.this,
						EditActivity.class);
				intent.putExtra("attributeName", "alipay");
				intent.putExtra("attributeValue", currentUser.getAlipay());
				intent.putExtra("title", getString(R.string.alipay_account));
				startActivity(intent);
			}
		});

		applybButton = (Button) findViewById(R.id.apply_button);
		applybButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				float tmp = Float.parseFloat(withdrawEditText.getText()
						.toString());
				if (tmp == 0) {
					Toast.makeText(getApplication(), R.string.zero_warn,
							Toast.LENGTH_SHORT).show();
				} else if (tmp > withdrawPoints) {
					Toast.makeText(getApplication(), R.string.overflow_warn,
							Toast.LENGTH_SHORT).show();
				} else {
					dialog.showDialog();
					applyWithdraw(currentUser.getId(), tmp);
				}
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

	private void applyWithdraw(final int userId, final float points) {
		String url = Constant.WITHDRAW_URL;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}
				Toast.makeText(getApplication(), R.string.submit_success,
						Toast.LENGTH_SHORT).show();
				finish();
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
				map.put("points", points + "");
				return map;
			}
		};
		RequestManager.getRequestQueue().add(stringRequest);
	}
}