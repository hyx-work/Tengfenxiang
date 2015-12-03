package com.android.tengfenxiang.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
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

	/**
	 * 是否已被加载过一次，第二次就不再去请求数据了
	 */
	private boolean mHasLoadedOnce;

	private MainApplication application;
	private User currentUser;
	private List<Article> articles;
	private XListView articleListView;

	public static ArticleFragment newInstance(int position) {
		ArticleFragment f = new ArticleFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);
		application = (MainApplication) getActivity().getApplication();
		currentUser = application.getCurrentUser();
		articles = new ArrayList<Article>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		layout = new FrameLayout(getActivity());
		layout.setLayoutParams(params);
		return layout;
	}

	private void getArticleList(int userId, int limit, int offset, int type) {
		String url = Constant.ARTICLE_LIST_URL + "?userId=" + userId
				+ "&limit=" + limit + "&offset=" + offset + "&type=" + type;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				System.err.println(response);
				ResponseResult result = JSON.parseObject(response,
						ResponseResult.class);
				List<Article> tmp = JSON.parseArray(
						result.getData().toString(), Article.class);
				articles.addAll(tmp);
				if (dialog.isShowing()) {
					dialog.cancelDialog();
					initView();
					// 标志为已经加载过
					mHasLoadedOnce = true;
				}
				loadComplete();
			}
		};
		// 请求失败的回调函数
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}
				Toast.makeText(getActivity(), R.string.unknow_error,
						Toast.LENGTH_SHORT).show();
			}
		};

		StringRequest request = new StringRequest(url, listener, errorListener);
		RequestManager.getRequestQueue(getActivity()).add(request);
	}

	// private void addEmptyHint() {
	// LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
	// LayoutParams.WRAP_CONTENT);
	// // params.
	// }

	private void initView() {
		// TODO Auto-generated method stub
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		// 添加ListView，设置监听事件
		articleListView = new XListView(getActivity());
		articleListView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				offset = 0;
				articles.clear();
				getArticleList(currentUser.getId(), limit, offset, position);
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				offset = offset + limit;
				getArticleList(currentUser.getId(), limit, offset, position);
			}
		});

		List<String> infos = new ArrayList<String>();
		if (articles != null) {
			for (int i = 0; i < articles.size(); i++) {
				infos.add(articles.get(i).getTitle());
			}
		}

		adapter = new ArticleListAdapter(getActivity(), articles);
		articleListView.setAdapter(adapter);
		articleListView.setLayoutParams(params);
		articleListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (arg2 >= 1 && arg2 <= articles.size()) {
					Intent intent = new Intent(getActivity(), WebActivity.class);
					intent.putExtra("title", getString(R.string.share));
					intent.putExtra("url", articles.get(arg2 - 1).getShareUrl());
					intent.putExtra("isShare", true);
					startActivity(intent);
				}
			}
		});

		layout.addView(articleListView);
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
	}

	@Override
	protected void lazyLoad() {
		// TODO Auto-generated method stub
		// 当前为可见且还没加载过数据，则需要去服务器请求数据
		if (isVisible && !mHasLoadedOnce) {
			dialog = new LoadingDialog(getActivity());
			dialog.showDialog();
			getArticleList(currentUser.getId(), limit, offset, position);
		}
	}

}