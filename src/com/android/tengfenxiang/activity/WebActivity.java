package com.android.tengfenxiang.activity;

import java.util.HashMap;
import java.util.Map;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.receiver.SaveShareRecordReceiver;
import com.android.tengfenxiang.receiver.SaveShareRecordReceiver.OnSaveRecordsListener;
import com.android.tengfenxiang.util.BitmapCompressUtil;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
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

	/**
	 * 缩略图，用于显示在分享对话框
	 */
	private Bitmap imageBitmap;

	/**
	 * 微信API的实例对象
	 */
	private IWXAPI wxApi;

	/**
	 * 弹出菜单
	 */
	private SharePopupWindow window;

	/**
	 * 记录分享的类型，朋友圈或微信好友
	 */
	private String destination;

	/**
	 * 广播管理对象
	 */
	private LocalBroadcastManager localBroadcastManager;

	/**
	 * 广播过滤
	 */
	private IntentFilter intentFilter;

	/**
	 * 保存分享信息的广播接收器
	 */
	private SaveShareRecordReceiver receiver;

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

		if (taskId != -1 || articleId != -1) {
			dialog.showDialog();
			loadThumbnails(intent.getStringExtra("image"));
		}
		initView();
		initReceiver();
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

	private void initReceiver() {
		intentFilter = new IntentFilter(Constant.SAVE_RETWEET_RECORD);
		receiver = new SaveShareRecordReceiver();
		receiver.setOnSaveRecordsListener(new OnSaveRecordsListener() {

			@Override
			public void onSaveShareRecords() {
				// TODO Auto-generated method stub
				saveRetweetRecord(destination);
			}
		});

		localBroadcastManager = LocalBroadcastManager.getInstance(this);
		localBroadcastManager.registerReceiver(receiver, intentFilter);
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
			webView.setWebChromeClient(new WebChromeClient());
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
				destination = "wechat_friend";
				break;
			case R.id.moment_btn:
				wechatShare(1);
				destination = "wechat_moment";
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 保存分享记录到后台
	 * 
	 * @param destination
	 */
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
		// 网页对象
		WXWebpageObject webpage = new WXWebpageObject();
		// 分享的网页链接
		webpage.webpageUrl = url;

		WXMediaMessage msg = new WXMediaMessage(webpage);
		// 分享对话框显示的标题
		msg.title = webTitle;
		// 分享对话框显示的文字内容
		msg.description = webContent;
		// 分享对话框显示的图片
		msg.thumbData = Util.bmpToByteArray(imageBitmap, false);

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

	/**
	 * 加载缩略图
	 * 
	 * @param imageUrl
	 *            缩略图URL
	 */
	private void loadThumbnails(String imageUrl) {
		// 显示图片的配置
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisk(true)
				.showImageOnLoading(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).build();

		ImageLoader.getInstance().loadImage(imageUrl, null, options,
				new SimpleImageLoadingListener() {

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						super.onLoadingComplete(imageUri, view, loadedImage);
						// 必须将位图压缩到32k以下，否则微信分享出错
						imageBitmap = BitmapCompressUtil.compressImage(
								loadedImage, 32);
						if (dialog.isShowing()) {
							dialog.cancelDialog();
						}
					}
				});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		localBroadcastManager.unregisterReceiver(receiver);
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