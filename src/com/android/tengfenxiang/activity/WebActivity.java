package com.android.tengfenxiang.activity;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.util.RequestUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class WebActivity extends BaseActivity {

	private TitleBar titleBar;
	private WebView webView;
	private LoadingDialog dialog;
	private RelativeLayout webviewLayout;

	/**
	 * activity显示的标题
	 */
	private String title;

	/**
	 * 要打开的网页
	 */
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_PROGRESS);

		setContentView(R.layout.web_activity);

		dialog = new LoadingDialog(this);

		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		url = intent.getStringExtra("url");
		initView();
	}

	private void initView() {
		webviewLayout = (RelativeLayout) findViewById(R.id.webview_layout);
		webView = (WebView) findViewById(R.id.web_view);
		initWebView();

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

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		// TODO Auto-generated method stub
		if (null != url && !url.equals("")) {
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setSupportZoom(false);
			webView.getSettings()
					.setJavaScriptCanOpenWindowsAutomatically(true);
			webView.setWebViewClient(new WebViewClient() {

				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					dialog.showDialog();
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					if (dialog.isShowing()) {
						dialog.cancelDialog();
					}
				}

				@Override
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					if (dialog.isShowing()) {
						dialog.cancelDialog();
					}
					Toast.makeText(getApplication(),
							R.string.fail_to_load_webpage, Toast.LENGTH_SHORT)
							.show();
				}

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}
			});
			webView.setWebChromeClient(new WebChromeClient() {

				// 在标题上显示页面加载进度
				@Override
				public void onProgressChanged(WebView view, int progress) {
					titleBar.setTitleText(getString(R.string.web_loading)
							+ progress + "%");
					setProgress(progress * 100);
					if (progress == 100) {
						titleBar.setTitleText(title);
					}
				}
			});
			RequestUtil.synCookies(getApplication(), url);
			webView.loadUrl(url);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (null != webView) {
			webviewLayout.removeView(webView);
			webView.removeAllViews();
			webView.destroy();
		}
	}

	/**
	 * 监听返回键，控制网页返回
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}