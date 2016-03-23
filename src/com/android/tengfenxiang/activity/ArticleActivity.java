package com.android.tengfenxiang.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.ArticlePagerAdapter;
import com.android.tengfenxiang.bean.ChannelItem;
import com.android.tengfenxiang.util.ChannelUtil;
import com.android.tengfenxiang.view.tab.CategoryTabStrip;

public class ArticleActivity extends FragmentActivity {

	private CategoryTabStrip tabs;
	private ViewPager pager;
	private ArticlePagerAdapter adapter;
	private ImageView channelImageView;
	private ArrayList<ChannelItem> userChannelList = new ArrayList<ChannelItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(initResource());

		userChannelList = ((ArrayList<ChannelItem>) ChannelUtil.getInstance(
				getApplication()).getUserChannelItems());
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
	private void initComponent() {
		tabs = (CategoryTabStrip) findViewById(R.id.category_strip);
		pager = (ViewPager) findViewById(R.id.view_pager);
		channelImageView = (ImageView) findViewById(R.id.icon_category);
		channelImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ArticleActivity.this,
						ChannelActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		// 缓存两页
		pager.setOffscreenPageLimit(2);
		adapter = new ArticlePagerAdapter(getSupportFragmentManager(),
				getApplication(), userChannelList);
		pager.setAdapter(adapter);
		tabs.setViewPager(pager);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 0 && resultCode == -1) {
			userChannelList = ((ArrayList<ChannelItem>) ChannelUtil
					.getInstance(getApplication()).getUserChannelItems());
			pager.setOffscreenPageLimit(1);
			adapter = new ArticlePagerAdapter(getSupportFragmentManager(),
					getApplication(), userChannelList);
			pager.setAdapter(adapter);
			tabs.setViewPager(pager);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 复写返回键，如果是点击了返回键则用父activity处理点击事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return getParent().onKeyDown(keyCode, event);
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}