package com.android.tengfenxiang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 监听保存分享记录的广播
 * 
 * @author ccz
 * 
 */
public class SaveShareRecordReceiver extends BroadcastReceiver {

	private OnSaveRecordsListener onSaveRecordsListener;

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		if (null != onSaveRecordsListener) {
			onSaveRecordsListener.onSaveShareRecords();
		}
	}

	public OnSaveRecordsListener getOnSaveRecordsListener() {
		return onSaveRecordsListener;
	}

	public void setOnSaveRecordsListener(
			OnSaveRecordsListener onSaveRecordsListener) {
		this.onSaveRecordsListener = onSaveRecordsListener;
	}

	public static interface OnSaveRecordsListener {
		public void onSaveShareRecords();
	}
}