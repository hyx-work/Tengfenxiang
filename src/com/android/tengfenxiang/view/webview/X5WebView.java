package com.android.tengfenxiang.view.webview;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tengfenxiang.R;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient.CustomViewCallback;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebStorage;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;

public class X5WebView extends WebView {

	public static final int FILE_CHOOSER = 0;
	private static boolean isSmallWebViewDisplayed = false;
	private boolean isClampedY = false;
	private Map<String, Object> mJsBridges;
	private TextView tog;
	RelativeLayout.LayoutParams layoutParams;
	private RelativeLayout refreshRela;
	TextView title;

	@SuppressLint("SetJavaScriptEnabled")
	public X5WebView(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);

		@SuppressWarnings("unused")
		WebStorage webStorage = WebStorage.getInstance();
		WebSettings webSetting = this.getSettings();
		webSetting.setJavaScriptEnabled(true);
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		webSetting.setAllowFileAccess(true);
		webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webSetting.setSupportZoom(true);
		webSetting.setBuiltInZoomControls(true);
		webSetting.setUseWideViewPort(true);
		webSetting.setSupportMultipleWindows(true);
		webSetting.setLoadWithOverviewMode(true);
		webSetting.setAppCacheEnabled(true);
		webSetting.setDatabaseEnabled(true);
		webSetting.setDomStorageEnabled(true);
		webSetting.setGeolocationEnabled(true);
		webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
		webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
		webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
		webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);

		this.getView().setClickable(true);
		this.getView().setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		this.setWebViewClientExtension(new X5WebViewEventHandler(this));// 配置X5webview的事件处理

		this.setWebViewClient(new WebViewClient() {

			/**
			 * 防止加载网页时调起系统浏览器
			 */
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			public void onReceivedHttpAuthRequest(
					WebView webview,
					com.tencent.smtt.export.external.interfaces.HttpAuthHandler httpAuthHandlerhost,
					String host, String realm) {
			}

			@Override
			public void onDetectedBlankScreen(String arg0, int arg1) {
				// TODO Auto-generated method stub
				super.onDetectedBlankScreen(arg0, arg1);
			}

			@Override
			public WebResourceResponse shouldInterceptRequest(WebView arg0,
					String arg1) {
				// TODO Auto-generated method stub
				return super.shouldInterceptRequest(arg0, arg1);
			}
		});

		this.setWebChromeClient(new WebChromeClient() {
			View myVideoView;
			View myNormalView;
			CustomViewCallback callback;

			/**
			 * 全屏播放配置
			 */
			@Override
			public void onShowCustomView(View view,
					CustomViewCallback customViewCallback) {
				// TODO Auto-generated method stub
				FrameLayout normalView = (FrameLayout) ((Activity) getContext())
						.findViewById(R.id.web_filechooser);
				ViewGroup viewGroup = (ViewGroup) normalView.getParent();
				viewGroup.removeView(normalView);
				viewGroup.addView(view);
				myVideoView = view;
				myNormalView = normalView;
				callback = customViewCallback;
			}

			@Override
			public void onHideCustomView() {
				// TODO Auto-generated method stub
				if (callback != null) {
					callback.onCustomViewHidden();
					callback = null;
				}
				if (myVideoView != null) {
					ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
					viewGroup.removeView(myVideoView);
					viewGroup.addView(myNormalView);
				}
			}

			@Override
			public void onProgressChanged(WebView arg0, int arg1) {
				// TODO Auto-generated method stub
				super.onProgressChanged(arg0, arg1);
			}

			@Override
			public void openFileChooser(ValueCallback<Uri> uploadFile,
					String acceptType, String captureType) {
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				((Activity) (X5WebView.this.getContext()))
						.startActivityForResult(
								Intent.createChooser(i, "choose files"),
								X5WebView.FILE_CHOOSER);
				super.openFileChooser(uploadFile, acceptType, captureType);
			}

			@Override
			public void onShowCustomView(View arg0, int arg1,
					CustomViewCallback arg2) {
				// TODO Auto-generated method stub
				@SuppressWarnings("unused")
				CustomViewCallback callback = new CustomViewCallback() {

					@Override
					public void onCustomViewHidden() {
						// TODO Auto-generated method stub
					}
				};
				super.onShowCustomView(arg0, arg1, arg2);
			}

			/**
			 * webview 的窗口转移
			 */
			@Override
			public boolean onCreateWindow(WebView arg0, boolean arg1,
					boolean arg2, Message msg) {
				// TODO Auto-generated method stub
				if (X5WebView.isSmallWebViewDisplayed == true) {

					WebView.WebViewTransport webViewTransport = (WebView.WebViewTransport) msg.obj;
					WebView webView = new WebView(X5WebView.this.getContext()) {

						@SuppressLint("DrawAllocation")
						protected void onDraw(Canvas canvas) {
							super.onDraw(canvas);
						};
					};
					webView.setWebViewClient(new WebViewClient() {
						public boolean shouldOverrideUrlLoading(WebView arg0,
								String arg1) {
							arg0.loadUrl(arg1);
							return true;
						};
					});
					FrameLayout.LayoutParams lp = new LayoutParams(400, 600);
					lp.gravity = Gravity.CENTER_HORIZONTAL
							| Gravity.CENTER_VERTICAL;
					X5WebView.this.addView(webView, lp);
					webViewTransport.setWebView(webView);
					msg.sendToTarget();
				}
				return true;
			}

			@Override
			public boolean onJsAlert(WebView arg0, String arg1, String arg2,
					JsResult arg3) {
				// TODO Auto-generated method stub
				arg3.confirm();
				return super.onJsAlert(null, "www.baidu.com", "aa", arg3);
			}

			@Override
			public boolean onJsPrompt(WebView arg0, String arg1, String arg2,
					String arg3, JsPromptResult arg4) {
				// TODO Auto-generated method stub
				if (X5WebView.this.isMsgPrompt(arg1)) {
					if (X5WebView.this.onJsPrompt(arg2, arg3)) {
						return true;
					} else {
						return false;
					}
				}
				return super.onJsPrompt(arg0, arg1, arg2, arg3, arg4);
			}

			@Override
			public void onReceivedTitle(WebView arg0, final String arg1) {
				// TODO Auto-generated method stub
				super.onReceivedTitle(arg0, arg1);
			}
		});
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		boolean ret = super.drawChild(canvas, child, drawingTime);
		return ret;
	}

	public X5WebView(Context arg0) {
		super(arg0);
		setBackgroundColor(85621);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.onInterceptTouchEvent(ev);
	}

	public static void setSmallWebViewEnabled(boolean enabled) {
		isSmallWebViewDisplayed = enabled;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
	}

	public void addJavascriptBridge(SecurityJsBridgeBundle jsBridgeBundle) {
		if (this.mJsBridges == null) {
			this.mJsBridges = new HashMap<String, Object>(5);
		}

		if (jsBridgeBundle != null) {
			String tag = SecurityJsBridgeBundle.BLOCK
					+ jsBridgeBundle.getJsBlockName() + "-"
					+ SecurityJsBridgeBundle.METHOD
					+ jsBridgeBundle.getMethodName();
			this.mJsBridges.put(tag, jsBridgeBundle);
		}
	}

	/**
	 * 当webchromeClient收到 web的prompt请求后进行拦截判断，用于调起本地android方法
	 * 
	 * @param methodName
	 *            方法名称
	 * @param blockName
	 *            区块名称
	 * @return true ：调用成功 ； false ：调用失败
	 */
	private boolean onJsPrompt(String methodName, String blockName) {
		String tag = SecurityJsBridgeBundle.BLOCK + blockName + "-"
				+ SecurityJsBridgeBundle.METHOD + methodName;

		if (this.mJsBridges != null && this.mJsBridges.containsKey(tag)) {
			((SecurityJsBridgeBundle) this.mJsBridges.get(tag)).onCallMethod();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判定当前的prompt消息是否为用于调用native方法的消息
	 * 
	 * @param msg
	 *            消息名称
	 * @return true 属于prompt消息方法的调用
	 */
	private boolean isMsgPrompt(String msg) {
		if (msg != null
				&& msg.startsWith(SecurityJsBridgeBundle.PROMPT_START_OFFSET)) {
			return true;
		} else {
			return false;
		}
	}

	// TBS: Do not use @Override to avoid false calls
	public boolean tbs_dispatchTouchEvent(MotionEvent ev, View view) {
		boolean r = super.super_dispatchTouchEvent(ev);
		android.util.Log.d("Bran", "dispatchTouchEvent " + ev.getAction() + " "
				+ r);
		return r;
	}

	// TBS: Do not use @Override to avoid false calls
	public boolean tbs_onInterceptTouchEvent(MotionEvent ev, View view) {
		boolean r = super.super_onInterceptTouchEvent(ev);
		return r;
	}

	protected void tbs_onScrollChanged(int l, int t, int oldl, int oldt,
			View view) {
		// TODO Auto-generated method stub
		super_onScrollChanged(l, t, oldl, oldt);
	}

	protected void tbs_onOverScrolled(int scrollX, int scrollY,
			boolean clampedX, boolean clampedY, View view) {
		// TODO Auto-generated method stub
		super_onOverScrolled(scrollX, scrollY, clampedX, clampedY);
	}

	protected void tbs_computeScroll(View view) {
		// TODO Auto-generated method stub
		super_computeScroll();
	}

	protected boolean tbs_overScrollBy(int deltaX, int deltaY, int scrollX,
			int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent,
			View view) {
		// TODO Auto-generated method stub
		if (this.isClampedY) {
			if ((refreshRela.getTop() + (-deltaY)) / 2 < 255) {
				this.tog.setAlpha((refreshRela.getTop() + (-deltaY)) / 2);
			} else
				this.tog.setAlpha(255);
			this.refreshRela.layout(refreshRela.getLeft(), refreshRela.getTop()
					+ (-deltaY), refreshRela.getRight(),
					refreshRela.getBottom() + (-deltaY));
			this.layout(this.getLeft(), this.getTop() + (-deltaY) / 2,
					this.getRight(), this.getBottom() + (-deltaY) / 2);
		}
		return super_overScrollBy(deltaX, deltaY, scrollX, scrollY,
				scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
				isTouchEvent);
	}

	public void setTitle(TextView title) {
		this.title = title;
	}

	protected boolean tbs_onTouchEvent(MotionEvent event, View view) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_UP && this.tog != null) {
			this.isClampedY = false;
			this.tog.setAlpha(0);
			this.refreshRela.layout(refreshRela.getLeft(), 0,
					refreshRela.getRight(), refreshRela.getBottom());
			this.layout(this.getLeft(), 0, this.getRight(), this.getBottom());
		}
		return super_onTouchEvent(event);
	}

}