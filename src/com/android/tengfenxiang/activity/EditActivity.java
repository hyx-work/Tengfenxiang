package com.android.tengfenxiang.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.db.UserDao;
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

public class EditActivity extends BaseActivity {

	private String attributeName;
	private String attributeValue;
	private String title;

	private EditText information;
	private TitleBar titleBar;
	private Button saveButton;
	private TextView alipayTextView;

	private LoadingDialog dialog;
	private UserDao userDao;

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

		userDao = UserDao.getInstance(getApplication());
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
					saveInformation(application.getCurrentUser().getId(),
							attributeName, info);
				} else {
					Toast.makeText(getApplication(), R.string.empty_input,
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// 支付宝账户只允许修改一次，如果当前用户的支付宝不为空则不可修改
		if (attributeName.equals("alipay") && null != attributeValue
				&& !attributeValue.equals("")) {
			// 编辑框设置为不可编辑
			information.setClickable(false);
			information.setFocusable(false);
			information.setEnabled(false);

			// 按钮设置为不可点击
			saveButton.setClickable(false);
			// 按钮设置为不可见
			saveButton.setVisibility(View.GONE);
			// 提示联系客服修改
			Toast.makeText(getApplication(), R.string.change_alipay_notify,
					Toast.LENGTH_SHORT).show();
		}

		// 如果支付宝账户没填写则显示提示文字
		if (attributeName.equals("alipay")
				&& (null == attributeValue || attributeValue.equals(""))) {
			alipayTextView.setVisibility(View.VISIBLE);
		}

		// 初始化编辑框的提示文字
		if (attributeName.equals("email")) {
			information.setHint(R.string.email_hint);
		} else if (attributeName.equals("qq")) {
			information.setHint(R.string.qq_hint);
		} else if (attributeName.equals("wechat")) {
			information.setHint(R.string.wechat_hint);
		} else if (attributeName.equals("nickName")) {
			information.setHint(R.string.nickname_hint);
		} else if (attributeName.equals("streetInfo")) {
			information.setHint(R.string.delivery_address_hint);
		} else if (attributeName.equals("alipay")) {
			information.setHint(R.string.alipay_hint);
		}
	}

	private void saveInformation(final int userId, final String attributeName,
			final String attributeValue) {
		String url = Constant.MODIFY_INFO_URL;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				System.err.println(response);
				User result = (User) ResponseUtil.handleResponse(
						getApplication(), response, User.class);
				if (null != result) {
					Toast.makeText(getApplication(), R.string.modify_success,
							Toast.LENGTH_SHORT).show();
					// 因为返回的对象token为null
					if (null == result.getToken()) {
						result.setToken(application.getCurrentUser().getToken());
					}
					// 因为返回的withdrawableCash为null
					result.setWithdrawableCash(application.getCurrentUser()
							.getWithdrawableCash());
					// 更新内存中的用户对象
					application.setCurrentUser(result);
					// 更新数据库中缓存的用户对象
					userDao.update(result);
					finish();
				} else {
					Toast.makeText(getApplication(), R.string.modify_fail,
							Toast.LENGTH_SHORT).show();
				}
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