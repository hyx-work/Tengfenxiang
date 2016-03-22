package com.android.tengfenxiang.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.MyChannelAdapter;
import com.android.tengfenxiang.adapter.OtherChannelAdapter;
import com.android.tengfenxiang.bean.ChannelItem;
import com.android.tengfenxiang.util.ChannelUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.gridview.ChannelGridView;
import com.android.tengfenxiang.view.gridview.OtherGridView;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;

public class ChannelActivity extends BaseActivity implements
		OnItemClickListener {

	private ChannelGridView userGridView;
	private OtherGridView otherGridView;
	private MyChannelAdapter userAdapter;
	private OtherChannelAdapter otherAdapter;
	private ArrayList<ChannelItem> otherChannelList = new ArrayList<ChannelItem>();
	private ArrayList<ChannelItem> userChannelList = new ArrayList<ChannelItem>();
	private boolean isMove = false;
	private TitleBar titleBar;
	private LoadingDialog dialog;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (dialog.isShowing()) {
				dialog.cancelDialog();
			}

			if (msg.what == 0 && userAdapter.isListChanged()) {
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				setResult(-1, intent);
			}
			finish();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel);
		dialog = new LoadingDialog(this);

		initView();
		initData();
	}

	private void initData() {
		userChannelList = ((ArrayList<ChannelItem>) ChannelUtil.getInstance(
				application).getUserChannelItems());
		otherChannelList = ((ArrayList<ChannelItem>) ChannelUtil.getInstance(
				application).getOtherChannelItems());
		userAdapter = new MyChannelAdapter(this, userChannelList);
		userGridView.setAdapter(userAdapter);
		otherAdapter = new OtherChannelAdapter(this, otherChannelList);
		otherGridView.setAdapter(otherAdapter);
		otherGridView.setOnItemClickListener(this);
		userGridView.setOnItemClickListener(this);
	}

	private void initView() {
		userGridView = (ChannelGridView) findViewById(R.id.userGridView);
		otherGridView = (OtherGridView) findViewById(R.id.otherGridView);
		titleBar = (TitleBar) findViewById(R.id.title_bar);
		titleBar.setOnClickListener(new OnTitleClickListener() {

			@Override
			public void OnClickRight() {

			}

			@Override
			public void OnClickLeft() {
				saveChannel();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, final View view,
			final int position, long id) {
		if (isMove) {
			return;
		}
		switch (parent.getId()) {
		case R.id.userGridView:
			if (position != 0) {
				final ImageView moveImageView = getView(view);
				TextView newTextView = (TextView) view
						.findViewById(R.id.text_item);
				final int[] startLocation = new int[2];
				newTextView.getLocationInWindow(startLocation);
				final ChannelItem channel = ((MyChannelAdapter) parent
						.getAdapter()).getItem(position);
				otherAdapter.setVisible(false);
				otherAdapter.addItem(channel);
				new Handler().postDelayed(new Runnable() {
					public void run() {
						try {
							int[] endLocation = new int[2];
							otherGridView.getChildAt(
									otherGridView.getLastVisiblePosition())
									.getLocationInWindow(endLocation);
							MoveAnim(moveImageView, startLocation, endLocation,
									channel, userGridView);
							userAdapter.setRemove(position);
						} catch (Exception localException) {
						}
					}
				}, 50L);
			}
			break;
		case R.id.otherGridView:
			final ImageView moveImageView = getView(view);
			if (moveImageView != null) {
				TextView newTextView = (TextView) view
						.findViewById(R.id.text_item);
				final int[] startLocation = new int[2];
				newTextView.getLocationInWindow(startLocation);
				final ChannelItem channel = ((OtherChannelAdapter) parent
						.getAdapter()).getItem(position);
				userAdapter.setVisible(false);
				userAdapter.addItem(channel);
				new Handler().postDelayed(new Runnable() {
					public void run() {
						try {
							int[] endLocation = new int[2];
							userGridView.getChildAt(
									userGridView.getLastVisiblePosition())
									.getLocationInWindow(endLocation);
							MoveAnim(moveImageView, startLocation, endLocation,
									channel, otherGridView);
							otherAdapter.setRemove(position);
						} catch (Exception localException) {
						}
					}
				}, 50L);
			}
			break;
		default:
			break;
		}
	}

	private void MoveAnim(View moveView, int[] startLocation,
			int[] endLocation, final ChannelItem moveChannel,
			final GridView clickGridView) {
		int[] initLocation = new int[2];
		moveView.getLocationInWindow(initLocation);
		final ViewGroup moveViewGroup = getMoveViewGroup();
		final View mMoveView = getMoveView(moveViewGroup, moveView,
				initLocation);
		TranslateAnimation moveAnimation = new TranslateAnimation(
				startLocation[0], endLocation[0], startLocation[1],
				endLocation[1]);
		moveAnimation.setDuration(300L);
		AnimationSet moveAnimationSet = new AnimationSet(true);
		moveAnimationSet.setFillAfter(false);
		moveAnimationSet.addAnimation(moveAnimation);
		mMoveView.startAnimation(moveAnimationSet);
		moveAnimationSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				isMove = true;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				moveViewGroup.removeView(mMoveView);
				if (clickGridView instanceof ChannelGridView) {
					otherAdapter.setVisible(true);
					otherAdapter.notifyDataSetChanged();
					userAdapter.remove();
				} else {
					userAdapter.setVisible(true);
					userAdapter.notifyDataSetChanged();
					otherAdapter.remove();
				}
				isMove = false;
			}
		});
	}

	private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
		int x = initLocation[0];
		int y = initLocation[1];
		viewGroup.addView(view);
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mLayoutParams.leftMargin = x;
		mLayoutParams.topMargin = y;
		view.setLayoutParams(mLayoutParams);
		return view;
	}

	private ViewGroup getMoveViewGroup() {
		ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
		LinearLayout moveLinearLayout = new LinearLayout(this);
		moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		moveViewGroup.addView(moveLinearLayout);
		return moveLinearLayout;
	}

	private ImageView getView(View view) {
		view.destroyDrawingCache();
		view.setDrawingCacheEnabled(true);
		Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(false);
		ImageView iv = new ImageView(this);
		iv.setImageBitmap(cache);
		return iv;
	}

	private void saveChannel() {
		dialog.showDialog();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ChannelUtil.getInstance(application).deleteAllChannel();
				ChannelUtil.getInstance(application).saveUserChannel(
						userAdapter.getChannnelLst());
				ChannelUtil.getInstance(application).saveOtherChannel(
						otherAdapter.getChannnelLst());
				handler.sendEmptyMessage(0);
			}
		}).start();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		saveChannel();
	}
}