package com.android.tengfenxiang.view.gridview;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.MyChannelAdapter;
import com.android.tengfenxiang.util.DensityUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ChannelGridView extends GridView {
	/**
	 * 点击时候的X位置
	 */
	public int downX;

	/**
	 * 点击时候的Y位置
	 */
	public int downY;

	/**
	 * 点击时候对应整个界面的X位置
	 */
	public int windowX;

	/**
	 * 点击时候对应整个界面的Y位置
	 */
	public int windowY;

	/**
	 * 屏幕上的X
	 */
	private int win_view_x;

	/**
	 * 屏幕上的Y
	 */
	private int win_view_y;

	/**
	 * 拖动的里x的距离
	 */
	int dragOffsetX;

	/**
	 * 拖动的里Y的距离
	 */
	int dragOffsetY;

	/**
	 * 长按时候对应postion
	 */
	public int dragPosition;

	/**
	 * Up后对应的ITEM的Position
	 */
	private int dropPosition;

	/**
	 * 开始拖动的ITEM的Position
	 */
	private int startPosition;

	/**
	 * item高
	 */
	private int itemHeight;

	/**
	 * item宽
	 */
	private int itemWidth;

	/**
	 * 拖动的时候对应ITEM的VIEW
	 */
	private View dragImageView = null;

	/**
	 * 长按的时候ITEM的VIEW
	 */
	@SuppressWarnings("unused")
	private ViewGroup dragItemView = null;

	private WindowManager windowManager = null;

	private WindowManager.LayoutParams windowParams = null;

	/**
	 * item总量
	 */
	private int itemTotalCount;

	/**
	 * 一行的ITEM数量
	 */
	private int nColumns = 4;

	/**
	 * 行数
	 */
	@SuppressWarnings("unused")
	private int nRows;

	private int Remainder;

	/**
	 * 是否在移动
	 */
	private boolean isMoving = false;

	private int holdPosition;

	/**
	 * 拖动的时候放大的倍数
	 */
	private double dragScale = 1.2D;

	/**
	 * 每个ITEM之间的水平间距
	 */
	private int mHorizontalSpacing = 15;

	/**
	 * 每个ITEM之间的竖直间距
	 */
	private int mVerticalSpacing = 15;

	private String LastAnimationID;

	public ChannelGridView(Context context) {
		super(context);
		init(context);
	}

	public ChannelGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ChannelGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void init(Context context) {
		mHorizontalSpacing = DensityUtil.dip2px(context, mHorizontalSpacing);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			downX = (int) ev.getX();
			downY = (int) ev.getY();
			windowX = (int) ev.getX();
			windowY = (int) ev.getY();
			setOnItemClickListener(ev);
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		@SuppressWarnings("unused")
		boolean bool = true;
		if (dragImageView != null
				&& dragPosition != AdapterView.INVALID_POSITION) {
			bool = super.onTouchEvent(ev);
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downX = (int) ev.getX();
				windowX = (int) ev.getX();
				downY = (int) ev.getY();
				windowY = (int) ev.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				onDrag(x, y, (int) ev.getRawX(), (int) ev.getRawY());
				if (!isMoving) {
					OnMove(x, y);
				}
				if (pointToPosition(x, y) != AdapterView.INVALID_POSITION) {
					break;
				}
				break;
			case MotionEvent.ACTION_UP:
				stopDrag();
				onDrop(x, y);
				requestDisallowInterceptTouchEvent(false);
				break;

			default:
				break;
			}
		}
		return super.onTouchEvent(ev);
	}

	private void onDrag(int x, int y, int rawx, int rawy) {
		if (dragImageView != null) {
			windowParams.alpha = 0.6f;
			windowParams.x = rawx - win_view_x;
			windowParams.y = rawy - win_view_y;
			windowManager.updateViewLayout(dragImageView, windowParams);
		}
	}

	private void onDrop(int x, int y) {
		int tempPostion = pointToPosition(x, y);
		dropPosition = tempPostion;
		MyChannelAdapter mDragAdapter = (MyChannelAdapter) getAdapter();
		mDragAdapter.setShowDropItem(true);
		mDragAdapter.notifyDataSetChanged();
	}

	/**
	 * 长按点击监听
	 * 
	 * @param ev
	 */
	public void setOnItemClickListener(final MotionEvent ev) {
		setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				int x = (int) ev.getX();
				int y = (int) ev.getY();

				startPosition = position;
				dragPosition = position;
				if (startPosition <= 0) {
					return false;
				}
				ViewGroup dragViewGroup = (ViewGroup) getChildAt(dragPosition
						- getFirstVisiblePosition());
				TextView dragTextView = (TextView) dragViewGroup
						.findViewById(R.id.text_item);
				dragTextView.setSelected(true);
				dragTextView.setEnabled(false);
				itemHeight = dragViewGroup.getHeight();
				itemWidth = dragViewGroup.getWidth();
				itemTotalCount = ChannelGridView.this.getCount();
				int row = itemTotalCount / nColumns;
				Remainder = (itemTotalCount % nColumns);
				if (Remainder != 0) {
					nRows = row + 1;
				} else {
					nRows = row;
				}

				if (dragPosition != AdapterView.INVALID_POSITION) {
					win_view_x = windowX - dragViewGroup.getLeft();
					win_view_y = windowY - dragViewGroup.getTop();
					dragOffsetX = (int) (ev.getRawX() - x);
					dragOffsetY = (int) (ev.getRawY() - y);
					dragItemView = dragViewGroup;
					dragViewGroup.destroyDrawingCache();
					dragViewGroup.setDrawingCacheEnabled(true);
					Bitmap dragBitmap = Bitmap.createBitmap(dragViewGroup
							.getDrawingCache());
					startDrag(dragBitmap, (int) ev.getRawX(),
							(int) ev.getRawY());
					hideDropItem();
					dragViewGroup.setVisibility(View.INVISIBLE);
					isMoving = false;
					requestDisallowInterceptTouchEvent(true);
					return true;
				}
				return false;
			}
		});
	}

	public void startDrag(Bitmap dragBitmap, int x, int y) {
		stopDrag();
		windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.TOP | Gravity.LEFT;
		windowParams.x = x - win_view_x;
		windowParams.y = y - win_view_y;
		windowParams.width = (int) (dragScale * dragBitmap.getWidth());
		windowParams.height = (int) (dragScale * dragBitmap.getHeight());
		this.windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		this.windowParams.format = PixelFormat.TRANSLUCENT;
		this.windowParams.windowAnimations = 0;
		ImageView iv = new ImageView(getContext());
		iv.setImageBitmap(dragBitmap);
		windowManager = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		windowManager.addView(iv, windowParams);
		dragImageView = iv;
	}

	private void stopDrag() {
		if (dragImageView != null) {
			windowManager.removeView(dragImageView);
			dragImageView = null;
		}
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	private void hideDropItem() {
		((MyChannelAdapter) getAdapter()).setShowDropItem(false);
	}

	public Animation getMoveAnimation(float toXValue, float toYValue) {
		TranslateAnimation mTranslateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0F, Animation.RELATIVE_TO_SELF,
				toXValue, Animation.RELATIVE_TO_SELF, 0.0F,
				Animation.RELATIVE_TO_SELF, toYValue);
		mTranslateAnimation.setFillAfter(true);
		mTranslateAnimation.setDuration(300L);
		return mTranslateAnimation;
	}

	public void OnMove(int x, int y) {
		int dPosition = pointToPosition(x, y);
		if (dPosition > 0) {
			if ((dPosition == -1) || (dPosition == dragPosition)) {
				return;
			}
			dropPosition = dPosition;
			if (dragPosition != startPosition) {
				dragPosition = startPosition;
			}
			int movecount;
			if ((dragPosition == startPosition)
					|| (dragPosition != dropPosition)) {
				movecount = dropPosition - dragPosition;
			} else {
				movecount = 0;
			}
			if (movecount == 0) {
				return;
			}

			int movecount_abs = Math.abs(movecount);

			if (dPosition != dragPosition) {
				ViewGroup dragGroup = (ViewGroup) getChildAt(dragPosition);
				dragGroup.setVisibility(View.INVISIBLE);

				float to_x = 1;
				float to_y;
				float x_vlaue = ((float) mHorizontalSpacing / (float) itemWidth) + 1.0f;
				float y_vlaue = ((float) mVerticalSpacing / (float) itemHeight) + 1.0f;
				for (int i = 0; i < movecount_abs; i++) {
					to_x = x_vlaue;
					to_y = y_vlaue;
					if (movecount > 0) {
						holdPosition = dragPosition + i + 1;
						if (dragPosition / nColumns == holdPosition / nColumns) {
							to_x = -x_vlaue;
							to_y = 0;
						} else if (holdPosition % 4 == 0) {
							to_x = 3 * x_vlaue;
							to_y = -y_vlaue;
						} else {
							to_x = -x_vlaue;
							to_y = 0;
						}
					} else {
						holdPosition = dragPosition - i - 1;
						if (dragPosition / nColumns == holdPosition / nColumns) {
							to_x = x_vlaue;
							to_y = 0;
						} else if ((holdPosition + 1) % 4 == 0) {
							to_x = -3 * x_vlaue;
							to_y = y_vlaue;
						} else {
							to_x = x_vlaue;
							to_y = 0;
						}
					}
					ViewGroup moveViewGroup = (ViewGroup) getChildAt(holdPosition);
					Animation moveAnimation = getMoveAnimation(to_x, to_y);
					moveViewGroup.startAnimation(moveAnimation);
					if (holdPosition == dropPosition) {
						LastAnimationID = moveAnimation.toString();
					}
					moveAnimation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub
							isMoving = true;
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							// TODO Auto-generated method stub
							if (animation.toString().equalsIgnoreCase(
									LastAnimationID)) {
								MyChannelAdapter mDragAdapter = (MyChannelAdapter) getAdapter();
								mDragAdapter.exchange(startPosition,
										dropPosition);
								startPosition = dropPosition;
								dragPosition = dropPosition;
								isMoving = false;
							}
						}
					});
				}
			}
		}
	}
}