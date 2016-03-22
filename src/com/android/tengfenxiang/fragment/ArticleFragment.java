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
import android.os.Message;
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
import com.android.tengfenxiang.util.ListViewUtil;
import com.android.tengfenxiang.util.RequestUtil;
import com.android.tengfenxiang.util.ResponseUtil;
import com.android.tengfenxiang.util.VolleyErrorUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.toptoast.TopToast;
import com.android.tengfenxiang.view.toptoast.TopToast.Style;
import com.android.tengfenxiang.view.viewpager.AutoScrollViewPager;
import com.android.tengfenxiang.view.xscrollview.XScrollView;
import com.android.tengfenxiang.view.xscrollview.XScrollView.IXScrollViewListener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

public class ArticleFragment extends LazyFragment {

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

	/**
	 * 加载缓存数据完成
	 */
	private final int LOAD_CECHE_FINISH = 0;
	/**
	 * 存储缓存数据完成
	 */
	private final int STORE_CECHE_FINISH = 1;
	/**
	 * bundle数据的key
	 */
	private final String BUNDLE_KEY = "refresh_number";

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD_CECHE_FINISH:
				refreshArticleList();
				refreshBannerList();
				break;

			case STORE_CECHE_FINISH:
				int number = msg.getData().getInt(BUNDLE_KEY);
				showRefreshNumber(number);
				break;

			default:
				break;
			}
		};
	};

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

	/**
	 * 初始化操作
	 */
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
		bannerListener = new BannerListener();
	}

	/**
	 * 控件初始化
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_scroll_view, container,
				false);
		View content = inflater.inflate(R.layout.artical_fragment, null);

		// 初始化提示文字
		hintTextView = (TextView) view.findViewById(R.id.empty_article_hint);
		// hintTextView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// dialog.showDialog();
		// getArticleList(application.getCurrentUser().getId(), limit,
		// offset, position);
		// getBannerList(application.getCurrentUser().getId(), position);
		// }
		// });

		// 初始化文章列表
		articleListView = (ListView) content.findViewById(R.id.article_list);
		articleListView.setAdapter(articleListAdapter);
		articleListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				User user = application.getCurrentUser();
				Intent intent = new Intent(context, X5WebActivity.class);
				intent.putExtra("title", context.getString(R.string.share));
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

		// 初始化banner的PagerView
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
		bannerPager.setInterval(4000);
		bannerPager.setAdapter(bannerAdapter);
		// 设置监听器
		bannerPager.setOnPageChangeListener(bannerListener);

		titleTextView = (TextView) content.findViewById(R.id.tv_bannertext);
		pointLayout = (LinearLayout) content.findViewById(R.id.points);
		bannerLayout = (LinearLayout) content.findViewById(R.id.banner_layout);

		// 初始化ScrollView
		scrollView = (XScrollView) view.findViewById(R.id.scroll_view);
		scrollView.setView(content);
		scrollView.hiddenFooter();
		scrollView.setPullRefreshEnable(true);
		scrollView.setPullLoadEnable(true);
		scrollView.setAutoLoadEnable(false);
		scrollView.setIXScrollViewListener(new IXScrollViewListener() {

			@Override
			public void onRefresh() {
				offset = 0;
				getArticleList(application.getCurrentUser().getId(), limit,
						offset, position);
				getBannerList(application.getCurrentUser().getId(), position);
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				offset = offset + limit;
				getArticleList(application.getCurrentUser().getId(), limit,
						offset, position);
			}
		});
		return view;
	}

	/**
	 * 第一次可见，需要去加载数据
	 */
	@Override
	public void onFirstUserVisible() {
		// TODO Auto-generated method stub
		super.onFirstUserVisible();
		// 在子线程加载数据
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// 加载缓存数据
				articles.clear();
				articles.addAll(articleDao.findAll(position));
				banners.clear();
				banners.addAll(bannerDao.findAll(position));

				// 如果缓存的文章列表为空则加载服务器数据
				if (articles.size() == 0) {
					getArticleList(application.getCurrentUser().getId(), limit,
							offset, position);
				}
				// 如果缓存的banner列表为空则加载服务器数据
				if (banners.size() == 0) {
					getBannerList(application.getCurrentUser().getId(),
							position);
				}
				if (articles.size() > 0 || banners.size() > 0) {
					handler.sendEmptyMessage(LOAD_CECHE_FINISH);
				}
			}
		}).start();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		bannerPager.stopAutoScroll();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		bannerPager.startAutoScroll();
	}

	/**
	 * 刷新文章Banner列表
	 */
	private void refreshBannerList() {
		// TODO Auto-generated method stub
		refreshBannerData();
		refreshBannerAction();
	}

	/**
	 * 刷新文章Banner的显示数据
	 */
	private void refreshBannerData() {
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
				Glide.with(context.getApplicationContext())
						.load(banners.get(i).getThumbnails()).centerCrop()
						.crossFade().into(imageView);

				final int arg2 = i;
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						User user = application.getCurrentUser();
						Intent intent = new Intent(context, X5WebActivity.class);
						intent.putExtra("title",
								context.getString(R.string.share));
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
	 * 刷新文章Banner滑动动作
	 */
	private void refreshBannerAction() {
		if (null != banners && banners.size() > 0) {
			int index = 0;
			bannerPager.setCurrentItem(index);
			pointLayout.getChildAt(pointIndex).setEnabled(true);
		}
	}

	/**
	 * 列表刷新完成的回调
	 */
	private void loadComplete() {
		// 停止刷新动画
		scrollView.stopRefresh();
		scrollView.stopLoadMore();

		// 更新最后一次刷新时间
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss",
				Locale.CHINA);
		Date curDate = new Date(System.currentTimeMillis());
		scrollView.setRefreshTime(formatter.format(curDate));

		// 更新文章列表
		refreshArticleList();

		// 隐藏等待对话框
		if (dialog.isShowing()) {
			dialog.cancelDialog();
		}
	}

	/**
	 * 更新文章列表的状态，在这里判断文章列表是不是为空，同时通过代码设置高度
	 */
	private void refreshArticleList() {
		// 设置控件的可见性
		if (null == articles || articles.size() == 0) {
			articleListView.setVisibility(View.GONE);
			hintTextView.setVisibility(View.VISIBLE);
			scrollView.hiddenFooter();
		} else {
			articleListView.setVisibility(View.VISIBLE);
			hintTextView.setVisibility(View.GONE);
			scrollView.showFooter();
		}
		ListViewUtil.setListViewHeightBasedOnChildren(articleListView);
		articleListAdapter.notifyDataSetChanged();
	}

	/**
	 * 文章Banner的监听器
	 * 
	 * @author 陈楚昭
	 * 
	 */
	class BannerListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			// 设置标题
			titleTextView.setText(banners.get(position).getTitle());
			// 设置点的状态为选中
			pointLayout.getChildAt(position).setEnabled(true);
			// 设置前一个点的状态为未选中
			pointLayout.getChildAt(pointIndex).setEnabled(false);
			// 更新当前选中的索引
			pointIndex = position;
		}

	}

	/**
	 * 显示文章更新提示信息
	 * 
	 * @param number
	 */
	private void showRefreshNumber(int number) {
		String text;
		if (number == 0) {
			text = context.getString(R.string.no_update_article);
		} else {
			text = context.getString(R.string.update_article_number);
			text = text.replace("?", number + "");
		}
		Style style = new Style(TopToast.LENGTH_SHORT, R.color.dodger_blue);
		TopToast toast = TopToast.makeText(context, text, style);
		toast.show();
	}

	/**
	 * 计算更新了多少篇文章
	 * 
	 * @param news
	 * @param olds
	 * @return
	 */
	private int calculateRefreshNumber(List<Article> news, List<Article> olds) {
		int number = 0;
		if (olds.size() > 0) {
			for (Article article : news) {
				if (article.getId() == olds.get(0).getId()) {
					break;
				}
				number++;
			}
		} else {
			number = news.size();
		}
		return number;
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
					final List<Article> tmp = JSON.parseArray(result.getData()
							.toString(), Article.class);
					// 如果是刷新操作则要更新数据库中的缓存数据
					if (offset == 0) {
						articles.clear();
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								// 计算更新了多少条数据
								int refreshNumber = calculateRefreshNumber(tmp,
										articleDao.findAll(type));
								articleDao.deleteAll(type);
								articleDao.insert(tmp, type);

								// 数据库操作完成通知UI线程显示刷新的数据数量
								Message msg = new Message();
								msg.what = STORE_CECHE_FINISH;
								Bundle bundle = new Bundle();
								bundle.putInt(BUNDLE_KEY, refreshNumber);
								msg.setData(bundle);
								handler.sendMessage(msg);
							}
						}).start();
					}
					articles.addAll(tmp);
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
					final List<Article> tmp = JSON.parseArray(result.getData()
							.toString(), Article.class);
					banners.clear();
					banners.addAll(tmp);
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							bannerDao.deleteAll(type);
							bannerDao.insert(tmp, type);
						}
					}).start();
				} else {
					ResponseUtil.handleErrorInfo(context.getApplication(),
							result);
				}
				refreshBannerList();
			}
		};
		// 请求失败的回调函数
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyErrorUtil.handleVolleyError(context, error);
				refreshBannerList();
			}
		};

		StringRequest request = new StringRequest(url, listener, errorListener);
		RequestUtil.getRequestQueue(context).add(request);
	}
}