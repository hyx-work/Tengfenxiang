package com.android.tengfenxiang.wxapi;

import com.android.tengfenxiang.util.Constant;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

/**
 * 微信分享回调
 * 
 * @author ccz
 * 
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;
	private LocalBroadcastManager localBroadcastManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		api = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID, false);
		api.handleIntent(getIntent(), this);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq arg0) {

	}

	@Override
	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			// 分享成功
			localBroadcastManager = LocalBroadcastManager.getInstance(this);
			Intent broadcast = new Intent(Constant.SAVE_RETWEET_RECORD);
			localBroadcastManager.sendBroadcast(broadcast);
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			// 分享取消
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			// 分享拒绝
			break;
		}
		// 要在这里执行一下finish，否则点击返回应用时无反应
		finish();
	}

}