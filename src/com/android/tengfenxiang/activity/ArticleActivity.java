package com.android.tengfenxiang.activity;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.NewPagerAdapter;
import com.android.tengfenxiang.view.tab.CategoryTabStrip;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class ArticleActivity extends FragmentActivity {

	private CategoryTabStrip tabs;
	private ViewPager pager;
	private NewPagerAdapter adapter;

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
		adapter = new NewPagerAdapter(getSupportFragmentManager());

		pager.setAdapter(adapter);
		tabs.setViewPager(pager);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

	}
}