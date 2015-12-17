package com.android.tengfenxiang.activity;

import java.util.ArrayList;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.ArticlePagerAdapter;
import com.android.tengfenxiang.bean.ChannelItem;
import com.android.tengfenxiang.db.DBHelper;
import com.android.tengfenxiang.util.ChannelManage;
import com.android.tengfenxiang.util.ImageLoadUtil;
import com.android.tengfenxiang.view.tab.CategoryTabStrip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ArticleActivity extends FragmentActivity {

	private CategoryTabStrip tabs;
	private ViewPager pager;
	private ArticlePagerAdapter adapter;
	private ImageView channelImageView;
	private DBHelper helper;
	private ArrayList<ChannelItem> userChannelList = new ArrayList<ChannelItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(initResource());

		helper = new DBHelper(getApplication());
		userChannelList = ((ArrayList<ChannelItem>) ChannelManage.getManage(
				helper).getUserChannel());
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
		pager.setOffscreenPageLimit(1);
		adapter = new ArticlePagerAdapter(getSupportFragmentManager(),
				getApplication(), userChannelList);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 0 && resultCode == -1) {
			userChannelList = ((ArrayList<ChannelItem>) ChannelManage
					.getManage(helper).getUserChannel());
			pager.setOffscreenPageLimit(1);
			adapter = new ArticlePagerAdapter(getSupportFragmentManager(),
					getApplication(), userChannelList);
			pager.setAdapter(adapter);
			tabs.setViewPager(pager);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}