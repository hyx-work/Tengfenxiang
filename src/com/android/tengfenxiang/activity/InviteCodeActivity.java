package com.android.tengfenxiang.activity;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.util.BitmapCompressUtil;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.DensityUtil;
import com.android.tengfenxiang.util.QRCodeUtil;
import com.android.tengfenxiang.view.dialog.SharePopupWindow;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InviteCodeActivity extends BaseActivity {

	private TitleBar titleBar;
	private ImageView codeImageView;
	private TextView codeTextView;
	private TextView hintTextView;

	/**
	 * 原始的二维码位图
	 */
	private Bitmap codeBitmap;
	/**
	 * 压缩的二维码位图，用于微信的显示
	 */
	private Bitmap thumbCodeBitmap;
	private SharePopupWindow window;

	private IWXAPI wxApi;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (null != codeBitmap) {
					codeImageView.setImageBitmap(codeBitmap);
					hintTextView.setVisibility(View.GONE);
				} else {
					Toast.makeText(getApplication(),
							R.string.generate_code_fail, Toast.LENGTH_SHORT)
							.show();
					hintTextView.setText(R.string.generate_code_fail);
				}
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite_code);

		wxApi = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID);
		wxApi.registerApp(Constant.WX_APP_ID);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initView();
		new Thread(new Runnable() {

			@Override
			public void run() {
				codeBitmap = QRCodeUtil.createImage(Constant.REGISTER_URL
						+ "?inviteCode="
						+ application.getCurrentUser().getInviteCode(),
						DensityUtil.dip2px(getApplication(), 200),
						DensityUtil.dip2px(getApplication(), 200));
				if (null != codeBitmap)
					thumbCodeBitmap = BitmapCompressUtil.compressImage(
							codeBitmap, 32);
				handler.sendEmptyMessage(1);
			}
		}).start();
	}

	private void initView() {
		codeTextView = (TextView) findViewById(R.id.invite_code_text);
		StringBuffer buffer = new StringBuffer();
		buffer.append(getString(R.string.invite_code_hint))
				.append(application.getCurrentUser().getInviteCode())
				.append("\n").append(getString(R.string.click_to_copy));
		codeTextView.setText(buffer);
		codeTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				cmb.setPrimaryClip(ClipData.newPlainText(null, application
						.getCurrentUser().getInviteCode()));
				Toast.makeText(getApplication(), R.string.copy_success,
						Toast.LENGTH_SHORT).show();
			}
		});

		hintTextView = (TextView) findViewById(R.id.hint_text);
		hintTextView.setVisibility(View.VISIBLE);

		titleBar = (TitleBar) findViewById(R.id.title_bar);
		titleBar.setOnClickListener(new OnTitleClickListener() {

			@Override
			public void OnClickRight() {
				window = new SharePopupWindow(InviteCodeActivity.this,
						itemsOnClick);
				window.setTitle(getString(R.string.invite_user));
				window.showAtLocation(findViewById(R.id.invite_code_text),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}

			@Override
			public void OnClickLeft() {
				finish();
			}
		});
		codeImageView = (ImageView) findViewById(R.id.invite_code);
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

	/**
	 * 微信分享
	 * 
	 * @param flag
	 *            0:分享到微信好友，1：分享到微信朋友圈
	 */
	private void wechatShare(int flag) {
		// 网页对象
		WXWebpageObject webpage = new WXWebpageObject();
		String url = Constant.REGISTER_URL + "?inviteCode="
				+ application.getCurrentUser().getInviteCode();
		// 分享的网页链掿
		webpage.webpageUrl = url;

		WXMediaMessage msg = new WXMediaMessage(webpage);
		// 分享对话框显示的标题
		msg.title = getString(R.string.app_name);
		// 分享对话框显示的文字内容
		msg.description = getString(R.string.invite_text);
		// 分享对话框显示的图片
		if (null != thumbCodeBitmap) {
			msg.thumbData = Util.bmpToByteArray(thumbCodeBitmap, false);
		}

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession
				: SendMessageToWX.Req.WXSceneTimeline;
		wxApi.sendReq(req);
	}

}