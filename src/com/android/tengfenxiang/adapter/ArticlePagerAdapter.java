package com.android.tengfenxiang.adapter;

import java.util.ArrayList;
import java.util.List;

import com.android.tengfenxiang.bean.ChannelItem;
import com.android.tengfenxiang.fragment.ArticleFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ArticlePagerAdapter extends FragmentStatePagerAdapter {

	private List<ChannelItem> catalogs = new ArrayList<ChannelItem>();

	public ArticlePagerAdapter(FragmentManager fm, Context context,
			List<ChannelItem> catalogs) {
		super(fm);
		this.catalogs = catalogs;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return catalogs.get(position).getName();
	}

	@Override
	public int getCount() {
		return catalogs.size();
	}

	@Override
	public Fragment getItem(int position) {
		return ArticleFragment.newInstance(catalogs.get(position).getId());
	}

}