package com.android.tengfenxiang.view.dialog;

import com.android.tengfenxiang.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SharePopupWindow extends PopupWindow {

	private ImageView btn_wechat, btn_moment;
	private Button btn_cancel;
	private TextView titleTextView;
	private View mMenuView;

	public SharePopupWindow(Activity context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.share_menu, null);
		titleTextView = (TextView) mMenuView.findViewById(R.id.title);
		btn_wechat = (ImageView) mMenuView.findViewById(R.id.wechat_btn);
		btn_moment = (ImageView) mMenuView.findViewById(R.id.moment_btn);
		btn_cancel = (Button) mMenuView.findViewById(R.id.cancel_btn);

		btn_cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dismiss();
			}
		});
		// 设置按钮监听
		btn_moment.setOnClickListener(itemsOnClick);
		btn_wechat.setOnClickListener(itemsOnClick);
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottom);
		// 实例化一个ColorDrawable颜色为白色
		ColorDrawable dw = new ColorDrawable(0xffffff);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});
	}

	public void setTitle(String title) {
		titleTextView.setText(title);
	}
}