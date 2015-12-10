package com.android.tengfenxiang.activity;

import java.util.HashMap;
import java.util.Map;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestManager;
import com.android.tengfenxiang.util.ResponseUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.dialog.SharePopupWindow;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebActivity extends BaseActivity {

	private TitleBar titleBar;
	private WebView webView;
	private LoadingDialog dialog;

	/**
	 * activity显示的标题
	 */
	private String title;
	/**
	 * 要打开的网页
	 */
	private String url;
	/**
	 * 如果是某个文章链接，则将文章id保存起来
	 */
	private int articleId;
	/**
	 * 如果是某个任务链接，则将任务id保存起来
	 */
	private int taskId;
	/**
	 * 分享到微信时候显示的标题
	 */
	private String webTitle;
	/**
	 * 分享到微信时候显示的内容
	 */
	private String webContent;

	private IWXAPI wxApi;

	private SharePopupWindow window;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_PROGRESS);

		setContentView(R.layout.web_activity);

		dialog = new LoadingDialog(this);
		wxApi = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID);
		wxApi.registerApp(Constant.WX_APP_ID);

		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		url = intent.getStringExtra("url");
		taskId = intent.getIntExtra("task_id", -1);
		articleId = intent.getIntExtra("article_id", -1);
		webTitle = intent.getStringExtra("web_title");
		webContent = intent.getStringExtra("web_content");
		initView();
	}

	private void initView() {
		webView = (WebView) findViewById(R.id.web_view);
		initWebView();

		titleBar = (TitleBar) findViewById(R.id.title_bar);
		if (taskId == -1 && articleId == -1) {
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

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		// TODO Auto-generated method stub
		if (null != url && !url.equals("")) {
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setSupportZoom(false);
			webView.setWebViewClient(new WebViewClient() {

				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					Toast.makeText(getApplication(), R.string.unknow_error,
							Toast.LENGTH_SHORT).show();
				}

				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}
			});
			webView.loadUrl(url);
		}
	}

	/**
	 * 为弹出窗口实现监听类
	 */
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			window.dismiss();
			switch (v.getId()) {
			case R.id.wechat_btn:
				wechatShare(0);
				break;
			case R.id.moment_btn:
				wechatShare(1);
				break;
			default:
				break;
			}
		}
	};

	private void saveRetweetRecord(String destination) {
		if (taskId == -1 && articleId == -1) {
			return;
		}
		dialog.showDialog();
		if (taskId == -1) {
			saveArticleRetweet(currentUser.getId(), articleId, destination);
		} else {
			saveTaskRetweet(currentUser.getId(), taskId, destination);
		}
	}

	/**
	 * 微信分享
	 * 
	 * @param flag
	 *            0:分享到微信好友，1：分享到微信朋友圈
	 */
	private void wechatShare(int flag) {
		WXWebpageObject webpage = new WXWebpageObject();
		// 分享的网页链接
		webpage.webpageUrl = url;

		WXMediaMessage msg = new WXMediaMessage(webpage);
		// 分享对话框显示的标题
		msg.title = webTitle;
		// 分享对话框显示的文字内容
		msg.description = webContent;
		// 分享对话框显示的图片
		Bitmap thumb = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		msg.thumbData = Util.bmpToByteArray(thumb, true);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession
				: SendMessageToWX.Req.WXSceneTimeline;
		wxApi.sendReq(req);
	}

	/**
	 * 文章分享记录
	 * 
	 * @param userId
	 *            用户ID
	 * @param articleId
	 *            文章ID
	 * @param destination
	 *            分享的类型
	 */
	private void saveArticleRetweet(final int userId, final int articleId,
			final String destination) {

		String postUrl = Constant.ARTICLE_RETWEET_URL;
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
					Toast.makeText(getApplication(), R.string.share_success,
							Toast.LENGTH_SHORT).show();
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
		StringRequest stringRequest = new StringRequest(Method.POST, postUrl,
				listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", userId + "");
				map.put("articleId", articleId + "");
				map.put("destination", destination);
				return map;
			}
		};
		RequestManager.getRequestQueue(getApplication()).add(stringRequest);
	}

	/**
	 * 保存任务分享记录
	 * 
	 * @param userId
	 *            用户ID
	 * @param taskId
	 *            任务ID
	 * @param destination
	 *            分享的类型
	 */
	private void saveTaskRetweet(final int userId, final int taskId,
			final String destination) {

		String postUrl = Constant.TASK_RETWEET_URL;
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
					Toast.makeText(getApplication(), R.string.share_success,
							Toast.LENGTH_SHORT).show();
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
		StringRequest stringRequest = new StringRequest(Method.POST, postUrl,
				listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", userId + "");
				map.put("taskId", taskId + "");
				map.put("destination", destination);
				return map;
			}
		};
		RequestManager.getRequestQueue(getApplication()).add(stringRequest);
	}

}