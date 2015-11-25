package com.android.tengfenxiang.view.titlebar;

import com.android.tengfenxiang.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitleBar extends RelativeLayout {

	private ImageView leftImageView;
	private ImageView rightImageView;
	private TextView titleTextView;

	private String titleText;
	private int titleTextColor;
	private float titleTextSize;
	private Drawable leftImage;
	private Drawable rightImage;

	private LayoutParams rightParams, leftParams, titleParams;
	private OnTitleClickListener listener;

	public interface OnTitleClickListener {
		public void OnClickLeft();

		public void OnClickRight();
	};

	public void setOnClickListener(OnTitleClickListener listener) {
		this.listener = listener;
	}

	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.TitleBar);
		titleText = array.getString(R.styleable.TitleBar_title);
		titleTextColor = array.getColor(R.styleable.TitleBar_titleTextColor,
				0xFFFFFF);
		titleTextSize = array.getDimension(R.styleable.TitleBar_titleTextSize,
				20);
		leftImage = array.getDrawable(R.styleable.TitleBar_titleLeftImg);
		rightImage = array.getDrawable(R.styleable.TitleBar_titleRightImg);
		array.recycle();

		titleTextView = new TextView(context);
		rightImageView = new ImageView(context);
		leftImageView = new ImageView(context);

		titleTextView.setText(titleText);
		titleTextView.setTextColor(titleTextColor);
		titleTextView.setTextSize(titleTextSize);
		titleTextView.setGravity(Gravity.CENTER);
		rightImageView.setImageDrawable(rightImage);
		leftImageView.setImageDrawable(leftImage);

		leftParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
		leftParams.setMargins(8, 0, 8, 0);
		addView(leftImageView, leftParams);
		rightParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
		rightParams.setMargins(8, 0, 8, 0);
		addView(rightImageView, rightParams);
		titleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		titleParams.addRule(Gravity.CENTER_VERTICAL, TRUE);
		titleParams.setMargins(80, 0, 0, 0);
		addView(titleTextView, titleParams);

		leftImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				listener.OnClickLeft();
			}
		});

		rightImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				listener.OnClickRight();
			}
		});
	}

	public void setLeftImageVisable(boolean visable) {
		if (visable)
			leftImageView.setVisibility(View.VISIBLE);
		else
			leftImageView.setVisibility(View.GONE);
	}

	public ImageView getLeftImageView() {
		return leftImageView;
	}

	public void setLeftImageView(ImageView leftImageView) {
		this.leftImageView = leftImageView;
	}

	public ImageView getRightImageView() {
		return rightImageView;
	}

	public void setRightImageView(ImageView rightImageView) {
		this.rightImageView = rightImageView;
	}

	public String getTitleText() {
		return titleText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	public int getTitleTextColor() {
		return titleTextColor;
	}

	public void setTitleTextColor(int titleTextColor) {
		this.titleTextColor = titleTextColor;
	}

	public float getTitleTextSize() {
		return titleTextSize;
	}

	public void setTitleTextSize(float titleTextSize) {
		this.titleTextSize = titleTextSize;
	}

	public void setRightImageVisable(boolean visable) {
		if (visable)
			rightImageView.setVisibility(View.VISIBLE);
		else
			rightImageView.setVisibility(View.GONE);
	}
}