package com.android.tengfenxiang.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.tengfenxiang.R;
import com.android.tengfenxiang.activity.X5WebActivity;
import com.android.tengfenxiang.adapter.ArticleListAdapter;
import com.android.tengfenxiang.adapter.BannerAdapter;
import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.Article;
import com.android.tengfenxiang.bean.ResponseResult;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.db.ArticleDao;
import com.android.tengfenxiang.db.BannerDao;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.DensityUtil;
import com.android.tengfenxiang.util.RequestUtil;
import com.android.tengfenxiang.util.ResponseUtil;
import com.android.tengfenxiang.util.VolleyErrorUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.viewpager.AutoScrollViewPager;
import com.android.tengfenxiang.view.xscrollview.XScrollView;
import com.android.tengfenxiang.view.xscrollview.XScrollView.IXScrollViewListener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

public class ArticleFragment extends Fragment {

	private static final String ARG_POSITION = "position";
	private int position;
	private LoadingDialog dialog;

	private int limit = 10;
	private int offset = 0;

	private ArticleListAdapter articleListAdapter;
	private ArticleDao articleDao;
	private BannerDao bannerDao;

	private MainApplication application;
	private List<Article> articles = new ArrayList<Article>();
	private List<Article> banners = new ArrayList<Article>();

	private ListView articleListView;
	private TextView hintTextView;
	private XScrollView scrollView;

	private AutoScrollViewPager bannerPager;
	private List<ImageView> bannerViews = new ArrayList<ImageView>();
	private TextView titleTextView;
	private LinearLayout pointLayout;
	private LinearLayout bannerLayout;

	private BannerAdapter bannerAdapter;
	private BannerListener bannerListener;

	private int pointIndex = 0;
	private Activity context;

	private Runnable LOAD_DATA = new Runnable() {
		@Override
		public void run() {
			// 加载缓存数据
			articles.clear();
			articles.addAll(articleDao.findAll(position));
			banners.clear();
			banners.addAll(bannerDao.findAll(position));

			// 设置控件的可见性
			if (null == articles || articles.size() == 0) {
				articleListView.setVisibility(View.GONE);
				hintTextView.setVisibility(View.VISIBLE);
			} else {
				articleListView.setVisibility(View.VISIBLE);
				hintTextView.setVisibility(View.GONE);
				articleListAdapter.notifyDataSetChanged();
			}
			initView();
			// 请求最新的数据
			getArticleList(application.getCurrentUser().getId(), limit, offset,
					position);
		}
	};

	private Handler handler = new Handler();

	public static ArticleFragment newInstance(int position) {
		ArticleFragment fragment = new ArticleFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(ARG_POSITION, position);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.context = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);

		dialog = new LoadingDialog(context);
		application = (MainApplication) context.getApplication();
		articleDao = ArticleDao.getInstance(context);
		bannerDao = BannerDao.getInstance(context);

		// 初始化适配器
		articleListAdapter = new ArticleListAdapter(context, articles);
		bannerAdapter = new BannerAdapter(bannerViews);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_scroll_view, container,
				false);
		View content = inflater.inflate(R.layout.artical_fragment, null);

		scrollView = (XScrollView) view.findViewById(R.id.scroll_view);
		articleListView = (ListView) content.findViewById(R.id.article_list);
		hintTextView = (TextView) view.findViewById(R.id.empty_article_hint);
		articleListView.setAdapter(articleListAdapter);

		bannerPager = (AutoScrollViewPager) content
				.findViewById(R.id.viewpager);
		// 根据屏幕的宽度调节ViewPager的长宽
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		@SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
		int height = width / 2;
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				width, height);
		bannerPager.setLayoutParams(layoutParams);

		titleTextView = (TextView) content.findViewById(R.id.tv_bannertext);
		pointLayout = (LinearLayout) content.findViewById(R.id.points);
		bannerLayout = (LinearLayout) content.findViewById(R.id.banner_layout);
		bannerPager.setInterval(4000);
		bannerPager.setAdapter(bannerAdapter);

		scrollView.setView(content);
		scrollView.setPullRefreshEnable(true);
		scrollView.setPullLoadEnable(true);
		scrollView.setAutoLoadEnable(false);

		// 延迟一秒钟加载数据，防止频繁切换时闪退
		handler.postDelayed(LOAD_DATA, 1000);
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		bannerPager.startAutoScroll();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		bannerPager.stopAutoScroll();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (!isVisibleToUser) {
			handler.removeCallbacks(LOAD_DATA);
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		initListView();
		initTextView();
		initBannerData();
		initBannerAction();
	}

	/**
	 * 初始化提示文字的TextView
	 */
	private void initTextView() {
		hintTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.showDialog();
				getArticleList(application.getCurrentUser().getId(), limit,
						offset, position);
			}
		});
	}

	/**
	 * 初始化文章列表的ListView
	 */
	private void initListView() {
		scrollView.setIXScrollViewListener(new IXScrollViewListener() {

			@Override
			public void onRefresh() {
				offset = 0;
				getArticleList(application.getCurrentUser().getId(), limit,
						offset, position);
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				offset = offset + limit;
				getArticleList(application.getCurrentUser().getId(), limit,
						offset, position);
			}
		});
		setListViewHeightBasedOnChildren(articleListView);
		articleListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				User user = application.getCurrentUser();
				Intent intent = new Intent(context, X5WebActivity.class);
				intent.putExtra("title", getString(R.string.share));
				String url = articles.get(arg2).getShareUrl();
				if (null != user.getToken() && !user.getToken().equals("")) {
					url = url + "&token=" + user.getToken();
				}
				intent.putExtra("url", url);
				intent.putExtra("article_id", articles.get(arg2).getId());
				intent.putExtra("web_title", articles.get(arg2).getTitle());
				intent.putExtra("web_content", articles.get(arg2).getContent());
				intent.putExtra("image", articles.get(arg2).getThumbnails());
				startActivity(intent);
			}
		});
	}

	/**
	 * 初始化广告banner的显示
	 */
	private void initBannerData() {
		if (null == banners || banners.size() == 0) {
			bannerPager.setVisibility(View.GONE);
			titleTextView.setVisibility(View.GONE);
			pointLayout.setVisibility(View.GONE);
			bannerLayout.setVisibility(View.GONE);
		} else {
			bannerPager.setVisibility(View.VISIBLE);
			titleTextView.setVisibility(View.VISIBLE);
			pointLayout.setVisibility(View.VISIBLE);
			bannerLayout.setVisibility(View.VISIBLE);
			titleTextView.setText(banners.get(0).getTitle());

			View view;
			LayoutParams params;
			bannerViews.clear();
			pointLayout.removeAllViews();

			for (int i = 0; i < banners.size(); i++) {
				// 设置广告图
				ImageView imageView = new ImageView(context);
				imageView.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				imageView.setScaleType(ScaleType.FIT_XY);
				Glide.with(context).load(banners.get(i).getThumbnails())
						.centerCrop().crossFade().into(imageView);

				final int arg2 = i;
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						System.err.println("dianjile " + arg2);
						User user = application.getCurrentUser();
						Intent intent = new Intent(context, X5WebActivity.class);
						intent.putExtra("title", getString(R.string.share));
						String url = banners.get(arg2).getShareUrl();
						if (null != user.getToken()
								&& !user.getToken().equals("")) {
							url = url + "&token=" + user.getToken();
						}
						intent.putExtra("url", url);
						intent.putExtra("article_id", banners.get(arg2).getId());
						intent.putExtra("web_title", banners.get(arg2)
								.getTitle());
						intent.putExtra("web_content", banners.get(arg2)
								.getContent());
						intent.putExtra("image", banners.get(arg2)
								.getThumbnails());
						startActivity(intent);
					}
				});
				bannerViews.add(imageView);

				// 设置圆圈点
				view = new View(context);
				params = new LayoutParams(DensityUtil.dip2px(context, 5),
						DensityUtil.dip2px(context, 5));
				params.leftMargin = DensityUtil.dip2px(context, 10);
				view.setBackgroundResource(R.drawable.point_background);
				view.setLayoutParams(params);
				view.setEnabled(false);

				pointLayout.addView(view);
			}
			bannerAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 初始化banner滑动的监听
	 */
	private void initBannerAction() {
		if (null != banners && banners.size() > 0) {
			bannerListener = new BannerListener();
			bannerPager.setOnPageChangeListener(bannerListener);
			int index = 0;
			bannerPager.setCurrentItem(index);
			pointLayout.getChildAt(pointIndex).setEnabled(true);
		}
	}

	/**
	 * 列表刷新完成的回调
	 */
	private void loadComplete() {
		// 列表加载完成后加载banner
		getBannerList(application.getCurrentUser().getId(), position);

		scrollView.stopRefresh();
		scrollView.stopLoadMore();
		setListViewHeightBasedOnChildren(articleListView);

		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss",
				Locale.CHINA);
		Date curDate = new Date(System.currentTimeMillis());
		scrollView.setRefreshTime(formatter.format(curDate));

		// 设置控件的可见性
		if (null == articles || articles.size() == 0) {
			articleListView.setVisibility(View.GONE);
			hintTextView.setVisibility(View.VISIBLE);
		} else {
			articleListView.setVisibility(View.VISIBLE);
			hintTextView.setVisibility(View.GONE);
		}

		// 隐藏等待对话框
		if (dialog.isShowing()) {
			dialog.cancelDialog();
		}
	}

	private void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	// 实现VierPager监听器接口
	class BannerListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			titleTextView.setText(banners.get(position).getTitle());
			pointLayout.getChildAt(position).setEnabled(true);
			pointLayout.getChildAt(pointIndex).setEnabled(false);
			pointIndex = position;
		}

	}

	private void getArticleList(final int userId, final int limit,
			final int offset, final int type) {
		String url = Constant.ARTICLE_LIST_URL + "?userId=" + userId
				+ "&limit=" + limit + "&offset=" + offset + "&type=" + type;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				ResponseResult result = JSON.parseObject(response,
						ResponseResult.class);
				if (result.getCode() == 200) {
					List<Article> tmp = JSON.parseArray(result.getData()
							.toString(), Article.class);
					// 如果是刷新操作则要更新数据库中的缓存数据
					if (offset == 0) {
						articles.clear();
						articleDao.deleteAll(type);
						articleDao.insert(tmp, type);
					}
					articles.addAll(tmp);
					articleListAdapter.notifyDataSetChanged();
					scrollView.setLoadAll(tmp.size() < limit);
				} else {
					ResponseUtil.handleErrorInfo(context.getApplication(),
							result);
				}
				loadComplete();
			}
		};
		// 请求失败的回调函数
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyErrorUtil.handleVolleyError(context, error);
				loadComplete();
			}
		};

		StringRequest request = new StringRequest(url, listener, errorListener);
		RequestUtil.getRequestQueue(context).add(request);
	}

	private void getBannerList(int userId, final int type) {
		String url = Constant.ARTICLE_BANNER_URL + "?userId=" + userId
				+ "&type=" + type;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				ResponseResult result = JSON.parseObject(response,
						ResponseResult.class);
				if (result.getCode() == 200) {
					List<Article> tmp = JSON.parseArray(result.getData()
							.toString(), Article.class);
					banners.clear();
					banners.addAll(tmp);
					bannerDao.deleteAll(type);
					bannerDao.insert(tmp, type);
				} else {
					ResponseUtil.handleErrorInfo(context.getApplication(),
							result);
				}

				initBannerData();
				initBannerAction();
			}
		};
		// 请求失败的回调函数
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyErrorUtil.handleVolleyError(context, error);
				initBannerData();
				initBannerAction();
			}
		};

		StringRequest request = new StringRequest(url, listener, errorListener);
		RequestUtil.getRequestQueue(context).add(request);
	}
}