package com.android.tengfenxiang.activity;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.util.QRCodeUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InviteCodeActivity extends BaseActivity {

	private TitleBar titleBar;
	private ImageView codeImageView;
	private TextView codeTextView;

	private Bitmap codeBitmap;
	private LoadingDialog dialog;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (dialog.isShowing())
					dialog.cancelDialog();
				initView();
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

		dialog = new LoadingDialog(this);
		dialog.showDialog();
		new Thread(new Runnable() {

			@Override
			public void run() {
				codeBitmap = QRCodeUtil.createImage(
						currentUser.getInviteCode(), 400, 400);
				handler.sendEmptyMessage(1);
			}
		}).start();
	}

	private void initView() {
		codeTextView = (TextView) findViewById(R.id.invite_code_text);
		codeTextView.setText(codeTextView.getText()
				+ currentUser.getInviteCode());
		titleBar = (TitleBar) findViewById(R.id.title_bar);
		titleBar.setOnClickListener(new OnTitleClickListener() {

			@Override
			public void OnClickRight() {
				shareCode();
			}

			@Override
			public void OnClickLeft() {
				finish();
			}
		});
		codeImageView = (ImageView) findViewById(R.id.invite_code);
		if (null != codeBitmap) {
			codeImageView.setImageBitmap(codeBitmap);
		} else {
			Toast.makeText(getApplication(), R.string.generate_code_fail,
					Toast.LENGTH_SHORT).show();
		}
	}

	protected void shareCode() {

	}
}