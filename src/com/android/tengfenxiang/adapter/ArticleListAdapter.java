package com.android.tengfenxiang.adapter;

import java.util.List;

import com.android.tengfenxiang.bean.Article;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 文章列表适配器
 * @author ccz
 *
 */
public class ArticleListAdapter extends BaseAdapter{

	/**
	 * 要显示的文章列表
	 */
	private List<Article> articles;

	@Override
	public int getCount() {
		return articles.size();
	}

	@Override
	public Object getItem(int arg0) {
		return articles.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		return null;
	}

	public List<Article> getArticles() {
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

}