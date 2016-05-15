package com.android.tengfenxiang.view.xscrollview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.view.xlistview.XListViewFooter;
import com.android.tengfenxiang.view.xlistview.XListViewFooter.OnLoadMoreClick;
import com.android.tengfenxiang.view.xlistview.XListViewHeader;

public class XScrollView extends ScrollView implements OnScrollListener,
		OnLoadMoreClick {

	private final static int SCROLL_BACK_HEADER = 0;
	private final static int SCROLL_BACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400;
	private final static int PULL_LOAD_MORE_DELTA = 50;
	private final static float OFFSET_RADIO = 1.8f;

	private float mLastY = -1;

	private Scroller mScroller;
	private OnScrollListener mScrollListener;
	private int mScrollBack;

	private IXScrollViewListener mListener;

	private LinearLayout mLayout;
	private LinearLayout mContentLayout;

	private XListViewHeader mHeader;

	private RelativeLayout mHeaderContent;
	private TextView mHeaderTime;
	private int mHeaderHeight;

	private XListViewFooter mFooterView;

	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false;

	private boolean mEnablePullLoad = true;
	private boolean mEnableAutoLoad = false;
	private boolean mPullLoading = false;

	/**
	 * 用于标示是不是已经加载完全部数据
	 */
	private boolean isLoadAll = false;

	public XScrollView(Context context) {
		super(context);
		initWithContext(context);
	}

	public XScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public XScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	private void initWithContext(Context context) {
		mLayout = (LinearLayout) View.inflate(context,
				R.layout.vw_xscrollview_layout, null);
		mContentLayout = (LinearLayout) mLayout
				.findViewById(R.id.content_layout);

		mScroller = new Scroller(context, new DecelerateInterpolator());
		this.setOnScrollListener(this);

		mHeader = new XListViewHeader(context);
		mHeaderContent = (RelativeLayout) mHeader
				.findViewById(R.id.xlistview_header_content);
		mHeaderTime = (TextView) mHeader
				.findViewById(R.id.xlistview_header_time);
		LinearLayout headerLayout = (LinearLayout) mLayout
				.findViewById(R.id.header_layout);
		headerLayout.addView(mHeader);

		mFooterView = new XListViewFooter(context);
		mFooterView.setListener(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;
		LinearLayout footLayout = (LinearLayout) mLayout
				.findViewById(R.id.footer_layout);
		footLayout.addView(mFooterView, params);

		ViewTreeObserver observer = mHeader.getViewTreeObserver();
		if (null != observer) {
			observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
				@SuppressWarnings("deprecation")
				@Override
				public void onGlobalLayout() {
					mHeaderHeight = mHeaderContent.getHeight();
					ViewTreeObserver observer = getViewTreeObserver();
					if (null != observer) {
						if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
							observer.removeGlobalOnLayoutListener(this);
						} else {
							observer.removeOnGlobalLayoutListener(this);
						}
					}
				}
			});
		}

		this.addView(mLayout);
	}

	public void setContentView(ViewGroup content) {
		if (mLayout == null) {
			return;
		}

		if (mContentLayout == null) {
			mContentLayout = (LinearLayout) mLayout
					.findViewById(R.id.content_layout);
		}

		if (mContentLayout.getChildCount() > 0) {
			mContentLayout.removeAllViews();
		}
		mContentLayout.addView(content);
	}

	public void setView(View content) {
		if (mLayout == null) {
			return;
		}

		if (mContentLayout == null) {
			mContentLayout = (LinearLayout) mLayout
					.findViewById(R.id.content_layout);
		}
		mContentLayout.addView(content);
	}

	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		mHeaderContent.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
	}

	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;

		if (!mEnablePullLoad) {
			mFooterView.setBottomMargin(0);
			mFooterView.hide();
			mFooterView.setPadding(0, 0, 0, mFooterView.getHeight() * (-1));
			mFooterView.setOnClickListener(null);

		} else {
			mPullLoading = false;
			mFooterView.setPadding(0, 0, 0, 0);
			mFooterView.show();

			// 根据是否已经加载完成所有数据，来动态修改底部提示文字
			if (isLoadAll) {
				mFooterView.setState(XListViewFooter.STATE_LOAD_ALL);
			} else {
				mFooterView.setState(XListViewFooter.STATE_NORMAL);
			}

			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	public void setAutoLoadEnable(boolean enable) {
		mEnableAutoLoad = enable;
	}

	public void stopRefresh() {
		if (mPullRefreshing) {
			mPullRefreshing = false;
			resetHeaderHeight();
		}

		// 根据是否已经加载完成所有数据，来动态修改底部提示文字
		if (isLoadAll) {
			mFooterView.setState(XListViewFooter.STATE_LOAD_ALL);
		} else {
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
		}
	}

	public void stopLoadMore() {
		if (mPullLoading) {
			mPullLoading = false;
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
		}

		// 根据是否已经加载完成所有数据，来动态修改底部提示文字
		if (isLoadAll) {
			mFooterView.setState(XListViewFooter.STATE_LOAD_ALL);
		} else {
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
		}
	}

	public void setRefreshTime(String time) {
		mHeaderTime.setText(time);
	}

	public void setIXScrollViewListener(IXScrollViewListener listener) {
		mListener = listener;
	}

	public void autoRefresh() {
		mHeader.setVisiableHeight(mHeaderHeight);

		if (mEnablePullRefresh && !mPullRefreshing) {
			if (mHeader.getVisiableHeight() > mHeaderHeight) {
				mHeader.setState(XListViewHeader.STATE_READY);
			} else {
				mHeader.setState(XListViewHeader.STATE_NORMAL);
			}
		}

		mPullRefreshing = true;
		mHeader.setState(XListViewHeader.STATE_REFRESHING);
		refresh();
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	private void updateHeaderHeight(float delta) {
		mHeader.setVisiableHeight((int) delta + mHeader.getVisiableHeight());

		if (mEnablePullRefresh && !mPullRefreshing) {
			if (mHeader.getVisiableHeight() > mHeaderHeight) {
				mHeader.setState(XListViewHeader.STATE_READY);
			} else {
				mHeader.setState(XListViewHeader.STATE_NORMAL);
			}
		}
	}

	private void resetHeaderHeight() {
		int height = mHeader.getVisiableHeight();
		if (height == 0)
			return;

		if (mPullRefreshing && height <= mHeaderHeight)
			return;

		int finalHeight = 0;
		if (mPullRefreshing && height > mHeaderHeight) {
			finalHeight = mHeaderHeight;
		}

		mScrollBack = SCROLL_BACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);

		invalidate();
	}

	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin();
		mFooterView.setBottomMargin(height);
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();

		if (bottomMargin > 0) {
			mScrollBack = SCROLL_BACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}
	}

	private void startLoadMore() {
		if (!mPullLoading) {
			mPullLoading = true;
			mFooterView.setState(XListViewFooter.STATE_LOADING);
			loadMore();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;

		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;

			if (mEnablePullRefresh && isTop()
					&& (mHeader.getVisiableHeight() >= 0 || deltaY > 0)) {
				mLastY = ev.getRawY();
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();
			} else if (isBottom()
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				mLastY = ev.getRawY();
				updateFooterHeight(-deltaY / OFFSET_RADIO);
			}
			break;

		default:
			mLastY = -1;
			resetHeaderOrBottom();
			break;
		}

		return super.dispatchTouchEvent(ev);
	}

	private void resetHeaderOrBottom() {
		if (isTop()) {
			if (mEnablePullRefresh
					&& mHeader.getVisiableHeight() > mHeaderHeight) {
				mPullRefreshing = true;
				mHeader.setState(XListViewHeader.STATE_REFRESHING);
				refresh();
			}
			resetHeaderHeight();

		} else if (isBottom()) {
			if (mEnablePullLoad
					&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
				startLoadMore();
			}
			resetFooterHeight();
		}
	}

	private boolean isTop() {
		return getScrollY() <= 0 || mHeader.getVisiableHeight() > mHeaderHeight
				|| mContentLayout.getTop() > 0;
	}

	private boolean isBottom() {
		return Math.abs(getScrollY() + getHeight()
				- computeVerticalScrollRange()) <= 5
				|| (getScrollY() > 0 && null != mFooterView && mFooterView
						.getBottomMargin() > 0);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLL_BACK_HEADER) {
				mHeader.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}

			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		View view = getChildAt(getChildCount() - 1);

		if (null != view) {
			int diff = (view.getBottom() - (view.getHeight() + view
					.getScrollY()));

			if (diff == 0 && mEnableAutoLoad) {
				startLoadMore();
			}
		}

		super.onScrollChanged(l, t, oldl, oldt);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	private void refresh() {
		if (mEnablePullRefresh && null != mListener) {
			mListener.onRefresh();
		}
	}

	private void loadMore() {
		if (mEnablePullLoad && null != mListener) {
			mListener.onLoadMore();
		}
	}

	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	public interface IXScrollViewListener {
		public void onRefresh();

		public void onLoadMore();
	}

	public boolean isLoadAll() {
		return isLoadAll;
	}

	public void setLoadAll(boolean isLoadAll) {
		this.isLoadAll = isLoadAll;
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		startLoadMore();
	}

	/**
	 * 隐藏底部的提示文字
	 */
	public void hiddenFooter() {
		mFooterView.setVisibility(View.GONE);
	}

	/**
	 * 显示底部的提示文字
	 */
	public void showFooter() {
		mFooterView.setVisibility(View.VISIBLE);
	}
}