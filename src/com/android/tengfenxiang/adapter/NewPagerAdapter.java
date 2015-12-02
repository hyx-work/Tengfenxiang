package com.android.tengfenxiang.adapter;

import java.util.ArrayList;
import java.util.List;

import com.android.tengfenxiang.activity.NewsFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class NewPagerAdapter extends FragmentPagerAdapter {

	private final List<String> catalogs = new ArrayList<String>();

	public NewPagerAdapter(FragmentManager fm) {
		super(fm);
		catalogs.add("\u672c\u5730");
		catalogs.add("\u672c\u5730");
		catalogs.add("\u672c\u5730");
		catalogs.add("\u672c\u5730");
		catalogs.add("\u672c\u5730");
		catalogs.add("\u672c\u5730");
		catalogs.add("\u672c\u5730");
		catalogs.add("\u672c\u5730");
		catalogs.add("\u672c\u5730");
		catalogs.add("\u672c\u5730");
		catalogs.add("\u672c\u5730");
		catalogs.add("\u672c\u5730");
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return catalogs.get(position);
	}

	@Override
	public int getCount() {
		return catalogs.size();
	}

	@Override
	public Fragment getItem(int position) {
		return NewsFragment.newInstance(position);
	}

}