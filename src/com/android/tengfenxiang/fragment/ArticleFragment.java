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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
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
import com.android.tengfenxiang.util.ListViewUtil;
import com.android.tengfenxiang.util.RequestUtil;
import com.android.tengfenxiang.util.ResponseUtil;
import com.android.tengfenxiang.util.VolleyErrorUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.toptoast.TopToast;
import com.android.tengfenxiang.view.toptoast.TopToast.Style;
import com.android.tengfenxiang.view.viewpager.RollPagerView;
import com.android.tengfenxiang.view.viewpager.RollPagerView.OnPageSelectedListener;
import com.android.tengfenxiang.view.xscrollview.XScrollView;
import com.android.tengfenxiang.view.xscrollview.XScrollView.IXScrollViewListener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class ArticleFragment extends LazyFragment {

	private static final String ARG_POSITION = "position";
	private int position;
	private LoadingDialog dialog;

	private int limit = 10;
	private int offset = 0;

	private ArticleListAdapter articleListAdapter;
	private ArticleDao articleDao;
	private BannerDao bannerDao;
	private BannerAdapter bannerAdapter;
	private Activity context;

	private MainApplication application;
	private List<Article> articles = new ArrayList<Article>();
	private List<Article> banners = new ArrayList<Article>();

	private ListView articleListView;
	private TextView hintTextView;
	private XScrollView scrollView;

	private RollPagerView bannerPager;
	private TextView titleTextView;
	private LinearLayout bannerLayout;

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

	private int currentIndex = 0;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD_CECHE_FINISH:
				refreshArticleList();
				refreshBannerList();
				offset = 0;
				getArticleList(application.getCurrentUser().getId(), limit,
						offset, position);
				getBannerList(application.getCurrentUser().getId(), position);
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
		bannerPager = (RollPagerView) content.findViewById(R.id.viewpager);
		// 根据屏幕的宽度调节ViewPager的长宽
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		@SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
		int height = width / 2;
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				width, height);
		bannerPager.setLayoutParams(layoutParams);
		bannerAdapter = new BannerAdapter(bannerPager, getActivity(), banners,
				application.getCurrentUser());
		bannerPager.setAnimationDurtion(500);
		bannerPager.setAdapter(bannerAdapter);
		bannerPager.setOnPageSelectedListener(new OnPageSelectedListener() {

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				if (banners.size() > 0) {
					currentIndex = position % banners.size();
					titleTextView.setText(banners.get(currentIndex).getTitle());
				}
			}
		});

		titleTextView = (TextView) content.findViewById(R.id.tv_bannertext);
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
				handler.sendEmptyMessage(LOAD_CECHE_FINISH);
			}
		}).start();
	}

	/**
	 * 刷新文章Banner列表
	 */
	private void refreshBannerList() {
		// TODO Auto-generated method stub
		bannerAdapter.notifyDataSetChanged();
		if (null == banners || banners.size() == 0) {
			bannerPager.setVisibility(View.GONE);
			titleTextView.setVisibility(View.GONE);
			bannerLayout.setVisibility(View.GONE);
		} else {
			bannerPager.setVisibility(View.VISIBLE);
			titleTextView.setVisibility(View.VISIBLE);
			bannerLayout.setVisibility(View.VISIBLE);

			// 图片回滚到第一页的位置
			int current = bannerPager.getViewPager().getCurrentItem();
			for (int i = 0; i <= current; i++) {
				bannerPager.getViewPager().setCurrentItem(current - i, false);
			}
			currentIndex = 0;
			// 将标题设置为第一条的标题
			titleTextView.setText(banners.get(currentIndex).getTitle());
		}
	}

	/**
	 * 列表刷新完成的回调
	 */
	private void loadComplete() {
		// 更新文章列表

		refreshArticleList();
		// 停止刷新动画
		scrollView.stopRefresh();
		scrollView.stopLoadMore();

		// 更新最后一次刷新时间
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss",
				Locale.CHINA);
		Date curDate = new Date(System.currentTimeMillis());
		scrollView.setRefreshTime(formatter.format(curDate));

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
	 * 显示文章更新提示信息
	 * 
	 * @param number
	 */
	private void showRefreshNumber(int number) {
		if (number != 0) {
			String text = context.getString(R.string.update_article_number);
			text = text.replace("?", number + "");
			Style style = new Style(TopToast.LENGTH_SHORT, R.color.dodger_blue);
			TopToast toast = TopToast.makeText(context, text, style);
			toast.show();
		}
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
								List<Article> olds = articleDao.findAll(type);
								int refreshNumber = calculateRefreshNumber(tmp,
										olds);
								if (refreshNumber > 0) {
									articleDao.deleteAll(type);
									articleDao.insert(tmp, type);
								}

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