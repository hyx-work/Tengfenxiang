package com.android.tengfenxiang.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.alibaba.fastjson.JSON;
import com.android.tengfenxiang.R;
import com.android.tengfenxiang.activity.WebActivity;
import com.android.tengfenxiang.adapter.ArticleListAdapter;
import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.Article;
import com.android.tengfenxiang.bean.ResponseResult;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.db.ArticleDao;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.ImageLoadUtil;
import com.android.tengfenxiang.util.RequestManager;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.xlistview.XListView;
import com.android.tengfenxiang.view.xlistview.XListView.IXListViewListener;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

public class ArticleFragment extends BaseFragment {

	private static final String ARG_POSITION = "position";
	private int position;
	private FrameLayout layout;
	private LoadingDialog dialog;

	private int limit = 10;
	private int offset;

	private ArticleListAdapter adapter;
	private ArticleDao dao;

	/**
	 * 是否已被加载过一次，第二次就不再去请求数据了
	 */
	private boolean mHasLoadedOnce;

	private MainApplication application;
	private User currentUser;
	private List<Article> articles;
	private XListView articleListView;
	private TextView hintTextView;

	public static ArticleFragment newInstance(int position) {
		ArticleFragment fragment = new ArticleFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(ARG_POSITION, position);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);

		dialog = new LoadingDialog(getActivity());
		application = (MainApplication) getActivity().getApplication();
		currentUser = application.getCurrentUser();
		dao = ArticleDao.getInstance(getActivity());
		initView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		layout = new FrameLayout(getActivity());
		layout.setLayoutParams(params);
		// 将ListView添加到布局中
		layout.addView(articleListView);
		// 将TextView添加到布局中
		layout.addView(hintTextView);
		return layout;
	}

	private void getArticleList(int userId, int limit, final int offset,
			int type) {
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
						dao.deleteAll(position);
						dao.insert(tmp, position);
					}
					articles.addAll(tmp);
					adapter.notifyDataSetChanged();
				} else {
					Toast.makeText(getActivity(), result.getData().toString(),
							Toast.LENGTH_SHORT).show();
				}
				loadComplete();
			}
		};
		// 请求失败的回调函数
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				loadComplete();
				Toast.makeText(getActivity(), R.string.unknow_error,
						Toast.LENGTH_SHORT).show();
			}
		};

		StringRequest request = new StringRequest(url, listener, errorListener);
		RequestManager.getRequestQueue(getActivity()).add(request);
	}

	private void initView() {
		// TODO Auto-generated method stub
		initListView();
		initTextView();
	}

	/**
	 * 初始化提示文字的TextView
	 */
	private void initTextView() {
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		hintTextView = new TextView(getActivity());
		hintTextView.setLayoutParams(params);
		hintTextView.setText(R.string.empty_article_hint);
		hintTextView.setVisibility(View.GONE);
		hintTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.showDialog();
				getArticleList(currentUser.getId(), limit, offset, position);
			}
		});
	}

	/**
	 * 初始化文章列表的ListView
	 */
	private void initListView() {
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		// 添加ListView，设置监听事件
		articleListView = new XListView(getActivity());
		articleListView.setVisibility(View.GONE);
		articleListView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				offset = 0;
				getArticleList(currentUser.getId(), limit, offset, position);
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				offset = offset + limit;
				getArticleList(currentUser.getId(), limit, offset, position);
			}
		});
		articleListView.setLayoutParams(params);
		articleListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (arg2 >= 1 && arg2 <= articles.size()) {
					Intent intent = new Intent(getActivity(), WebActivity.class);
					intent.putExtra("title", getString(R.string.share));
					String url = articles.get(arg2 - 1).getShareUrl();
					if (null != currentUser.getToken()
							&& !currentUser.getToken().equals("")) {
						url = url + "&token=" + currentUser.getToken();
					}
					intent.putExtra("url", url);
					intent.putExtra("article_id", articles.get(arg2 - 1)
							.getId());
					intent.putExtra("web_title", articles.get(arg2 - 1)
							.getTitle());
					intent.putExtra("web_content", articles.get(arg2 - 1)
							.getContent());
					intent.putExtra("image", articles.get(arg2 - 1)
							.getThumbnails());
					startActivity(intent);
				}
			}
		});
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ImageLoadUtil.clearMemoryCache();
	}

	/**
	 * 列表刷新完成的回调
	 */
	private void loadComplete() {
		articleListView.stopRefresh();
		articleListView.stopLoadMore();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss",
				Locale.CHINA);
		Date curDate = new Date(System.currentTimeMillis());
		articleListView.setRefreshTime(formatter.format(curDate));

		// 设置空间的可见性
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

	@Override
	protected void lazyLoad() {
		// TODO Auto-generated method stub
		// 当前为可见且还没加载过数据，则需要读取缓存数据
		// 如果缓存数据为空，则请求服务器数据
		if (isVisible && !mHasLoadedOnce) {
			articles = dao.findAll(position);
			adapter = new ArticleListAdapter(getActivity(), articles);
			articleListView.setAdapter(adapter);
			mHasLoadedOnce = true;
			// 没有缓存数据，请求服务器数据
			if (null == articles || articles.size() == 0) {
				getArticleList(currentUser.getId(), limit, offset, position);
				articleListView.setVisibility(View.GONE);
				hintTextView.setVisibility(View.VISIBLE);
			} else {
				articleListView.setVisibility(View.VISIBLE);
				hintTextView.setVisibility(View.GONE);
			}
		}
	}

}