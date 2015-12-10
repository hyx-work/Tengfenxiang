package com.android.tengfenxiang.wxapi;

import com.android.tengfenxiang.util.Constant;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.os.Bundle;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		api = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID, false);
		api.handleIntent(getIntent(), this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onReq(BaseReq arg0) {

	}

	@Override
	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			// 分享成功
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			// 分享取消
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			// 分享拒绝
			break;
		}
	}

}