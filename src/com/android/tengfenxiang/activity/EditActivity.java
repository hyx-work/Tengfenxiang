package com.android.tengfenxiang.activity;

import java.util.HashMap;
import java.util.Map;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestUtil;
import com.android.tengfenxiang.util.ResponseUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends BaseActivity {

	private String attributeName;
	private String attributeValue;
	private String title;

	private EditText information;
	private TitleBar titleBar;
	private Button saveButton;

	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);

		Intent intent = getIntent();
		// 提交到后台接收的属性名称
		attributeName = intent.getStringExtra("attributeName");
		// 提交到后台接收的属性值
		attributeValue = intent.getStringExtra("attributeValue");
		title = intent.getStringExtra("title");

		dialog = new LoadingDialog(this);
		initView();
	}

	private void initView() {
		information = (EditText) findViewById(R.id.infomation);
		information.setText(attributeValue);
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
		saveButton = (Button) findViewById(R.id.save_button);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String info = information.getText().toString();
				if (info != null && !info.equals("")) {
					dialog.showDialog();
					saveInformation(currentUser.getId(), attributeName, info);
				} else {
					Toast.makeText(getApplication(), R.string.empty_input,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void saveInformation(final int userId, final String attributeName,
			final String attributeValue) {
		String url = Constant.MODIFY_INFO_URL;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}
				User result = (User) ResponseUtil.handleResponse(
						getApplication(), response, User.class);
				if (null != result) {
					Toast.makeText(getApplication(), R.string.modify_success,
							Toast.LENGTH_SHORT).show();
					application.setCurrentUser(result);
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
				map.put(attributeName, attributeValue);
				return map;
			}
		};
		RequestUtil.getRequestQueue(getApplication()).add(stringRequest);
	}
}