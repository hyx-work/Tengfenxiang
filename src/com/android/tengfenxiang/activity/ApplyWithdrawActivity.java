package com.android.tengfenxiang.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestUtil;
import com.android.tengfenxiang.util.ResponseUtil;
import com.android.tengfenxiang.util.VolleyErrorUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class ApplyWithdrawActivity extends BaseActivity {

	private TitleBar titleBar;
	private TextView withdrawableTextView;
	private EditText withdrawEditText;
	private TextView alipayTextView;
	private Button applybButton;

	private LoadingDialog dialog;
	private float withdrawPoints;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.withdraw_apply);

		Intent intent = getIntent();
		withdrawPoints = intent.getFloatExtra("withdrawPoints", 1);
		dialog = new LoadingDialog(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initView();
	}

	private void initView() {
		String alipay = application.getCurrentUser().getAlipay();
		alipayTextView = (TextView) findViewById(R.id.alipay_account);
		if (null == alipay || alipay.equals("")) {
			alipayTextView.setText(R.string.not_setting);
			showDialog();
		} else {
			alipayTextView.setText(alipay);
		}

		withdrawEditText = (EditText) findViewById(R.id.withdraw_points);
		withdrawEditText.setText(withdrawPoints + "");

		withdrawableTextView = (TextView) findViewById(R.id.withdrawable_points);
		withdrawableTextView.setText(withdrawPoints
				+ getString(R.string.unit_yuan));

		applybButton = (Button) findViewById(R.id.apply_button);
		applybButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String withdrawNum = withdrawEditText.getText().toString();
				if (null == withdrawNum || withdrawNum.equals("")) {
					Toast.makeText(getApplication(),
							R.string.empty_withdraw_input, Toast.LENGTH_SHORT)
							.show();
				} else {
					float tmp = Float.parseFloat(withdrawNum);
					if (tmp == 0) {
						Toast.makeText(getApplication(), R.string.zero_warn,
								Toast.LENGTH_SHORT).show();
					} else if (tmp > withdrawPoints) {
						Toast.makeText(getApplication(),
								R.string.overflow_warn, Toast.LENGTH_SHORT)
								.show();
					} else {
						dialog.showDialog();
						applyWithdraw(application.getCurrentUser().getId(), tmp);
					}
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
				Object result = ResponseUtil.handleResponse(getApplication(),
						response, null);
				if (null != result) {
					Toast.makeText(getApplication(), R.string.submit_success,
							Toast.LENGTH_SHORT).show();
					finish();
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
		RequestUtil.getRequestQueue(getApplication()).add(stringRequest);
	}

	private void showDialog() {
		Builder builder = new Builder(ApplyWithdrawActivity.this);
		builder.setMessage(R.string.empty_alipy);
		builder.setTitle(R.string.dialog_title);
		builder.setPositiveButton(R.string.setting_now,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(ApplyWithdrawActivity.this,
								EditActivity.class);
						intent.putExtra("attributeName", "alipay");
						intent.putExtra("attributeValue", application
								.getCurrentUser().getAlipay());
						intent.putExtra("title",
								getString(R.string.alipay_account));
						startActivity(intent);
					}
				});
		builder.setNegativeButton(R.string.cancel_withdraw,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
		builder.create().show();
	}
}