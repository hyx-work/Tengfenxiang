package com.android.tengfenxiang.activity;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.ArticlePagerAdapter;
import com.android.tengfenxiang.util.ImageLoadUtil;
import com.android.tengfenxiang.view.tab.CategoryTabStrip;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class ArticleActivity extends FragmentActivity {

	private CategoryTabStrip tabs;
	private ViewPager pager;
	private ArticlePagerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(initResource());
		initComponent();
	}

	/**
	 * 初始化布局资源文件
	 */
	public int initResource() {
		return R.layout.article;
	}

	/**
	 * 初始化组件
	 */
	public void initComponent() {
		tabs = (CategoryTabStrip) findViewById(R.id.category_strip);
		pager = (ViewPager) findViewById(R.id.view_pager);
		pager.setOffscreenPageLimit(1);
		adapter = new ArticlePagerAdapter(getSupportFragmentManager(),
				getApplication());

		pager.setAdapter(adapter);
		tabs.setViewPager(pager);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ImageLoadUtil.clearMemoryCache();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

	}
}