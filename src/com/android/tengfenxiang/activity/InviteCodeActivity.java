package com.android.tengfenxiang.activity;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.DensityUtil;
import com.android.tengfenxiang.util.QRCodeUtil;
import com.android.tengfenxiang.view.dialog.SharePopupWindow;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
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

	private Bitmap codeBitmap;
	private SharePopupWindow window;

	private static final int THUMB_SIZE = 150;
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
		WXImageObject imgObj = new WXImageObject(codeBitmap);

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;

		Bitmap thumbBmp = Bitmap.createScaledBitmap(codeBitmap, THUMB_SIZE,
				THUMB_SIZE, true);
		codeBitmap.recycle();
		msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;
		req.scene = (flag == 1) ? SendMessageToWX.Req.WXSceneTimeline
				: SendMessageToWX.Req.WXSceneSession;
		wxApi.sendReq(req);
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}
}