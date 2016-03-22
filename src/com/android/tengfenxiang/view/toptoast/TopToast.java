package com.android.tengfenxiang.view.toptoast;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.tengfenxiang.R;

public class TopToast {

	public static final int LENGTH_SHORT = 2000;

	public static final int LENGTH_LONG = 5000;

	private final Activity mActivity;
	private int mDuration = LENGTH_SHORT;
	private View mView;
	private LayoutParams mLayoutParams;
	private boolean mFloating;

	public TopToast(Activity activity) {
		mActivity = activity;
	}

	public static TopToast makeText(Activity context, CharSequence text,
			Style style) {
		return makeText(context, text, style, R.layout.top_toast_layout);
	}

	public static TopToast makeText(Activity context, CharSequence text,
			Style style, float textSize) {
		return makeText(context, text, style, R.layout.top_toast_layout,
				textSize);
	}

	public static TopToast makeText(Activity context, CharSequence text,
			Style style, int layoutId) {
		LayoutInflater inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflate.inflate(layoutId, null);

		return makeText(context, text, style, v, true);
	}

	public static TopToast makeText(Activity context, CharSequence text,
			Style style, int layoutId, float textSize) {
		LayoutInflater inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflate.inflate(layoutId, null);

		return makeText(context, text, style, v, true, textSize);
	}

	public static TopToast makeText(Activity context, CharSequence text,
			Style style, View customView) {
		return makeText(context, text, style, customView, false);
	}

	private static TopToast makeText(Activity context, CharSequence text,
			Style style, View view, boolean floating) {
		return makeText(context, text, style, view, floating, 0);
	}

	private static TopToast makeText(Activity context, CharSequence text,
			Style style, View view, boolean floating, float textSize) {
		TopToast result = new TopToast(context);

		TextView tv = (TextView) view.findViewById(android.R.id.message);
		tv.setBackgroundResource(style.background);

		if (textSize > 0)
			tv.setTextSize(textSize);
		tv.setText(text);

		result.mView = view;
		result.mDuration = style.duration;
		result.mFloating = floating;

		return result;
	}

	public static TopToast makeText(Activity context, int resId, Style style,
			View customView, boolean floating) {
		return makeText(context, context.getResources().getText(resId), style,
				customView, floating);
	}

	public static TopToast makeText(Activity context, int resId, Style style)
			throws Resources.NotFoundException {
		return makeText(context, context.getResources().getText(resId), style);
	}

	public static TopToast makeText(Activity context, int resId, Style style,
			int layoutId) throws Resources.NotFoundException {
		return makeText(context, context.getResources().getText(resId), style,
				layoutId);
	}

	public void show() {
		ToastManager manager = ToastManager.obtain(mActivity);
		manager.add(this);
	}

	public boolean isShowing() {
		if (mFloating) {
			return mView != null && mView.getParent() != null;
		} else {
			return mView.getVisibility() == View.VISIBLE;
		}
	}

	public void cancel() {
		ToastManager.obtain(mActivity).clearMsg(this);

	}

	public static void cancelAll() {
		ToastManager.clearAll();
	}

	public static void cancelAll(Activity activity) {
		ToastManager.release(activity);
	}

	public Activity getActivity() {
		return mActivity;
	}

	public void setView(View view) {
		mView = view;
	}

	public View getView() {
		return mView;
	}

	public void setDuration(int duration) {
		mDuration = duration;
	}

	public int getDuration() {
		return mDuration;
	}

	public void setText(int resId) {
		setText(mActivity.getText(resId));
	}

	public void setText(CharSequence s) {
		if (mView == null) {
			throw new RuntimeException(
					"This AppMsg was not created with AppMsg.makeText()");
		}
		TextView tv = (TextView) mView.findViewById(android.R.id.message);
		if (tv == null) {
			throw new RuntimeException(
					"This AppMsg was not created with AppMsg.makeText()");
		}
		tv.setText(s);
	}

	public LayoutParams getLayoutParams() {
		if (mLayoutParams == null) {
			mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
		}
		return mLayoutParams;
	}

	public TopToast setLayoutParams(LayoutParams layoutParams) {
		mLayoutParams = layoutParams;
		return this;
	}

	public TopToast setLayoutGravity(int gravity) {
		mLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, gravity);
		return this;
	}

	public boolean isFloating() {
		return mFloating;
	}

	public void setFloating(boolean mFloating) {
		this.mFloating = mFloating;
	}

	public static class Style {

		private final int duration;
		private final int background;

		public Style(int duration, int resId) {
			this.duration = duration;
			this.background = resId;
		}

		public int getDuration() {
			return duration;
		}

		public int getBackground() {
			return background;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof TopToast.Style)) {
				return false;
			}
			Style style = (Style) o;
			return style.duration == duration && style.background == background;
		}

	}

}