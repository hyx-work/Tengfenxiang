package com.android.tengfenxiang.view.dialog;

import com.android.tengfenxiang.R;

import android.app.Dialog;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class LoadingDialog extends Dialog {

	public LoadingDialog(Context context) {
		super(context, R.style.LoadingDialog);
		this.context = context;
		initDialog(context);
	}

	private ImageView ivProgress;
	private TextView tvInfo;
	private Context context;

	private void initDialog(Context context) {
		setContentView(R.layout.loading_dialog_view);
		ivProgress = (ImageView) findViewById(R.id.img);
		tvInfo = (TextView) findViewById(R.id.tipTextView);
		// 按返回键和点击对话框区域外都不会消失
		this.setCancelable(false);
	}

	public void showDialog() {
		Animation animation = AnimationUtils.loadAnimation(context,
				R.anim.dialog_load_animation);
		ivProgress.startAnimation(animation);
		show();
	}

	public void cancelDialog() {
		ivProgress.clearAnimation();
		this.cancel();
	}

	public void setProgressText(String text) {
		tvInfo.setText(text);
	}

}