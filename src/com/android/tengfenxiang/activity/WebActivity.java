package com.android.tengfenxiang.activity;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.view.dialog.SharePopupWindow;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;

public class WebActivity extends Activity {
	private TitleBar titleBar;
	private String title;
	private String url;

	private SharePopupWindow window;

	/**
	 * 是否显示分享按钮，默认不显示
	 */
	private boolean isShare;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_activity);

		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		url = intent.getStringExtra("url");
		isShare = intent.getBooleanExtra("isShare", false);

		initView();
	}

	private void initView() {
		titleBar = (TitleBar) findViewById(R.id.title_bar);
		if (!isShare) {
			titleBar.getRightImageView().setVisibility(View.GONE);
		}
		titleBar.setTitleText(title);
		titleBar.setOnClickListener(new OnTitleClickListener() {

			@Override
			public void OnClickRight() {
				window = new SharePopupWindow(WebActivity.this, itemsOnClick);
				window.showAtLocation(
						WebActivity.this.findViewById(R.id.web_view),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}

			@Override
			public void OnClickLeft() {
				finish();
			}
		});
	}

	/**
	 * 为弹出窗口实现监听类
	 */
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			window.dismiss();
			switch (v.getId()) {
			case R.id.wechat_btn:
				break;
			case R.id.moment_btn:
				break;
			default:
				break;
			}
		}
	};

}